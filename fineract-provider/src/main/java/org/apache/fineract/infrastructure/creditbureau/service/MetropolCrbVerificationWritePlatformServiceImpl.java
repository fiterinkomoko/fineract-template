/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.creditbureau.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.creditbureau.data.CRBREPORTTYPES;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.loanaccount.data.CrbKenyaMetropolRequestData;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaConsumerVerificationData;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaCorporateVerificationData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.MetropolCrbCreditInfoEnhancedReport;
import org.apache.fineract.portfolio.loanaccount.domain.MetropolCrbCreditInfoEnhancedRepository;
import org.apache.fineract.portfolio.loanaccount.domain.MetropolCrbIdentityReport;
import org.apache.fineract.portfolio.loanaccount.domain.MetropolCrbIdentityVerificationRepository;
import org.apache.fineract.portfolio.loanaccount.service.TransUnionCrbConsumerVerificationReadPlatformService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetropolCrbVerificationWritePlatformServiceImpl implements MetropolCrbVerificationWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(MetropolCrbVerificationWritePlatformServiceImpl.class);
    public static final String FORM_URL_CONTENT_TYPE = "Content-Type";
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final TransUnionCrbConsumerVerificationReadPlatformService verificationReadPlatformService;
    private final MetropolCrbIdentityVerificationRepository metropolCrbIdentityVerificationRepository;
    private final MetropolCrbCreditInfoEnhancedRepository metropolCrbCreditInfoEnhancedRepository;

    @Autowired
    private final Environment env;

    @Override
    public CommandProcessingResult loanVerificationToMetropolKenya(Long loanId, JsonCommand command) {
        Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);

        TransUnionRwandaConsumerVerificationData individualClient = null;
        TransUnionRwandaCorporateVerificationData corporateClient = null;

        if (!loan.getCurrencyCode().equals("KES")) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.currency.is.not.support.on.metropol.crb.document.verification",
                    "Document verification is only supported for loans in KSH but found " + loan.getCurrencyCode() + " currency ");
        }

        Client clientObj = this.clientRepositoryWrapper.findOneWithNotFoundDetection(loan.getClientId());
        MetropolCrbIdentityReport metropolCrbIdentityReport = null;
        try {
            if (clientObj.getLegalForm().equals(LegalForm.PERSON.getValue())) {
                individualClient = this.verificationReadPlatformService.retrieveConsumerToBeVerifiedToTransUnion(clientObj.getId());
                metropolCrbIdentityReport = verifyIdentityDocument(individualClient.getNationalID(), loan, clientObj);
            } else {
                corporateClient = this.verificationReadPlatformService.retrieveCorporateToBeVerifiedToTransUnion(clientObj.getId());
                metropolCrbIdentityReport = verifyIdentityDocument(corporateClient.getCompanyRegNo(), loan, clientObj);
            }
        } catch (Exception e) {
            throw new GeneralPlatformDomainRuleException("Verification failed with error: ", e.getMessage());
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(clientObj.getId()).withResourceIdAsString(metropolCrbIdentityReport.getId().toString()) //
                .build();
    }

    @Override
    public CommandProcessingResult loanCreditInfoEnhancedToMetropolKenya(Long loanId, JsonCommand command) {
        Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);

        TransUnionRwandaConsumerVerificationData individualClient = null;
        TransUnionRwandaCorporateVerificationData corporateClient = null;

        if (!loan.getCurrencyCode().equals("KES")) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.currency.is.not.support.on.metropol.crb.credit.info.enhanced",
                    "Credit Info Enhanced is only supported for loans in KSH but found " + loan.getCurrencyCode() + " currency ");
        }

        Client clientObj = this.clientRepositoryWrapper.findOneWithNotFoundDetection(loan.getClientId());
        MetropolCrbCreditInfoEnhancedReport metropolCrbCreditInfoEnhancedReport = null;
        try {
            if (clientObj.getLegalForm().equals(LegalForm.PERSON.getValue())) {
                individualClient = this.verificationReadPlatformService.retrieveConsumerToBeVerifiedToTransUnion(clientObj.getId());
                metropolCrbCreditInfoEnhancedReport = verifyCreditInfoEnhanced(individualClient.getNationalID(), loan, clientObj);
            } else {
                corporateClient = this.verificationReadPlatformService.retrieveCorporateToBeVerifiedToTransUnion(clientObj.getId());
                metropolCrbCreditInfoEnhancedReport = verifyCreditInfoEnhanced(corporateClient.getCompanyRegNo(), loan, clientObj);
            }
        } catch (Exception e) {
            throw new GeneralPlatformDomainRuleException("Credit Info Enhanced report failed with error: ", e.getMessage());
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(clientObj.getId()).withResourceIdAsString(metropolCrbCreditInfoEnhancedReport.getId().toString()) //
                .build();
    }

    private MetropolCrbIdentityReport verifyIdentityDocument(String documentId, Loan loan, Client client)
            throws NoSuchAlgorithmException, IOException {
        CrbKenyaMetropolRequestData requestData = new CrbKenyaMetropolRequestData(1, documentId, "001");
        String jsonPayload = convertRequestPayloadToJson(requestData);

        String timestamp = DateUtils.generateTimestamp();
        String hash = generateHash(jsonPayload, timestamp);

        JsonObject jsonResponse = sendRequest(getConfigProperty("fineract.integrations.metropol.crb.rest.clientVerify"), jsonPayload,
                timestamp, hash, loan, client);

        MetropolCrbIdentityReport metropolCrbIdentityReport = getMetropolCrbIdentityReport(jsonResponse, loan, client);
        metropolCrbIdentityReport = metropolCrbIdentityVerificationRepository.saveAndFlush(metropolCrbIdentityReport);
        return metropolCrbIdentityReport;
    }

    private MetropolCrbCreditInfoEnhancedReport verifyCreditInfoEnhanced(String documentId, Loan loan, Client client)
            throws NoSuchAlgorithmException, IOException {
        CrbKenyaMetropolRequestData requestData = new CrbKenyaMetropolRequestData(10, "45555", documentId, "001",
                loan.getApprovedPrincipal().intValue(), 1);
        String jsonPayload = convertRequestPayloadToJson(requestData);

        String timestamp = DateUtils.generateTimestamp();
        String hash = generateHash(jsonPayload, timestamp);

        JsonObject jsonResponse = sendRequest(getConfigProperty("fineract.integrations.metropol.crb.rest.clientVerifyCreditInfoEnhanced"),
                jsonPayload, timestamp, hash, loan, client);

        MetropolCrbCreditInfoEnhancedReport crbCreditInfoEnhancedReport = getMetropolCrbCreditInfoEnhancedReport(jsonResponse, loan,
                client);
        crbCreditInfoEnhancedReport = metropolCrbCreditInfoEnhancedRepository.saveAndFlush(crbCreditInfoEnhancedReport);
        return crbCreditInfoEnhancedReport;
    }

    private JsonObject sendRequest(String urlStr, String payload, String timestamp, String hash, Loan loan, Client client)
            throws IOException {
        OkHttpClient httpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(MediaType.parse(FORM_URL_CONTENT_TYPE), payload);
        Request request = new Request.Builder().url(urlStr).post(requestBody)
                .addHeader("X-METROPOL-REST-API-KEY", getConfigProperty("fineract.integrations.metropol.crb.rest.publicKey"))
                .addHeader("X-METROPOL-REST-API-HASH", hash).addHeader("X-METROPOL-REST-API-TIMESTAMP", timestamp)
                .addHeader("Content-Type", "application/json").build();

        Response response = httpClient.newCall(request).execute();

        if (response.isSuccessful()) {

            String resObject = response.body().string();
            LOG.info("Response from Metropol CRB: " + resObject);
            return JsonParser.parseString(resObject).getAsJsonObject();
        } else {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.credit.info.enhanced.failed",
                    "Credit Info Enhanced failed with error: " + response.code() + ":" + response.message() + "");
        }

    }

    @NotNull
    private MetropolCrbIdentityReport getMetropolCrbIdentityReport(JsonObject jsonResponse, Loan loan, Client client) {
        MetropolCrbIdentityReport metropolCrbIdentityReport = new MetropolCrbIdentityReport();

        metropolCrbIdentityReport.setClientId(client);
        metropolCrbIdentityReport.setLoanId(loan);
        metropolCrbIdentityReport.setCitizenship(getStringField(jsonResponse, "citizenship"));
        metropolCrbIdentityReport.setClan(getStringField(jsonResponse, "clan"));
        metropolCrbIdentityReport.setDateOfBirth(getStringField(jsonResponse, "dob"));
        metropolCrbIdentityReport.setDateOfDeath(getStringField(jsonResponse, "dod"));
        metropolCrbIdentityReport.setEthnicGroup(getStringField(jsonResponse, "ethnic_group"));
        metropolCrbIdentityReport.setFamily(getStringField(jsonResponse, "family"));
        metropolCrbIdentityReport.setFingerprint(getStringField(jsonResponse, "fingerprint"));
        metropolCrbIdentityReport.setFirstName(getStringField(jsonResponse, "first_name"));
        metropolCrbIdentityReport.setGender(getStringField(jsonResponse, "gender"));
        metropolCrbIdentityReport.setIdentityNumber(getStringField(jsonResponse, "id_number"));
        metropolCrbIdentityReport.setIdentityType(getStringField(jsonResponse, "identity_type"));
        metropolCrbIdentityReport.setOccupation(getStringField(jsonResponse, "occupation"));
        metropolCrbIdentityReport.setOtherName(getStringField(jsonResponse, "other_name"));
        metropolCrbIdentityReport.setPhoto(getStringField(jsonResponse, "photo"));
        metropolCrbIdentityReport.setPlaceOfBirth(getStringField(jsonResponse, "place_of_birth"));
        metropolCrbIdentityReport.setPlaceOfDeath(getStringField(jsonResponse, "place_of_death"));
        metropolCrbIdentityReport.setPlaceOfLive(getStringField(jsonResponse, "place_of_live"));
        metropolCrbIdentityReport.setRegOffice(getStringField(jsonResponse, "regoffice"));
        metropolCrbIdentityReport.setSerialNumber(getStringField(jsonResponse, "serial_number"));
        metropolCrbIdentityReport.setSignature(getStringField(jsonResponse, "signature"));
        metropolCrbIdentityReport.setSurname(getStringField(jsonResponse, "surname"));
        metropolCrbIdentityReport.setTrxId(getStringField(jsonResponse, "trx_id"));
        return metropolCrbIdentityReport;
    }

    private String generateHash(String payload, String timestamp) throws NoSuchAlgorithmException {
        String concatenatedString = getConfigProperty("fineract.integrations.metropol.crb.rest.privateKey") + payload
                + getConfigProperty("fineract.integrations.metropol.crb.rest.publicKey") + timestamp;
        LOG.info("Concatenated string: " + concatenatedString);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(concatenatedString.getBytes(StandardCharsets.UTF_8));

        StringBuilder hashHex = new StringBuilder();
        for (byte b : hashBytes) {
            hashHex.append(String.format("%02x", b));
        }

        return hashHex.toString();

    }

    private String getConfigProperty(String propertyName) {
        return this.env.getProperty(propertyName);
    }

    private String convertRequestPayloadToJson(CrbKenyaMetropolRequestData requestData) {
        Gson gson = new GsonBuilder().create();
        String request = gson.toJson(requestData);
        LOG.info("Actual Payload to be sent - - >" + request);
        return request;
    }

    public String getStringField(JsonObject jsonObject, String fieldName) {
        if (jsonObject != null && jsonObject.has(fieldName) && jsonObject.get(fieldName).isJsonPrimitive()
                && jsonObject.get(fieldName).getAsJsonPrimitive().isString()) {
            return jsonObject.get(fieldName).getAsString();
        }
        return null;
    }

    public Boolean getBooleanField(JsonObject jsonObject, String fieldName) {
        if (jsonObject != null && jsonObject.has(fieldName) && jsonObject.get(fieldName).isJsonPrimitive()
                && jsonObject.get(fieldName).getAsJsonPrimitive().isBoolean()) {
            return jsonObject.get(fieldName).getAsBoolean();
        }
        return null;
    }

    @NotNull
    private MetropolCrbCreditInfoEnhancedReport getMetropolCrbCreditInfoEnhancedReport(JsonObject jsonResponse, Loan loan, Client client) {
        MetropolCrbCreditInfoEnhancedReport enhancedReport = new MetropolCrbCreditInfoEnhancedReport();

        enhancedReport.setClientId(client);
        enhancedReport.setLoanId(loan);
        enhancedReport.setReportType(CRBREPORTTYPES.CREDIT_INFO_ENHANCED.name());
        enhancedReport.setApiCode(getStringField(jsonResponse, "api_code"));
        enhancedReport.setApiCodeDescription(getStringField(jsonResponse, "api_code_description"));
        enhancedReport.setApplicationRefNo(getStringField(jsonResponse, "application_ref_no"));
        enhancedReport.setCreditScore(getStringField(jsonResponse, "credit_score"));
        enhancedReport.setDelinquencyCode(getStringField(jsonResponse, "delinquency_code"));
        enhancedReport.setHasError(getBooleanField(jsonResponse, "has_error"));
        enhancedReport.setHasFraud(getBooleanField(jsonResponse, "has_fraud"));
        enhancedReport.setIdentityNumber(getStringField(jsonResponse, "id_number"));
        enhancedReport.setIdentityType(getStringField(jsonResponse, "identity_type"));
        enhancedReport.setIsGuarantor(getBooleanField(jsonResponse, "is_guarantor"));
        enhancedReport.setTrxId(getStringField(jsonResponse, "trx_id"));
        return enhancedReport;
    }
}
