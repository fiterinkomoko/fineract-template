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
package org.apache.fineract.portfolio.loanaccount.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.loanaccount.data.RwandaConsumerCreditData;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionAuthenticationData;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaConsumerCreditData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransUnionCrbServiceImpl implements TransUnionCrbService {

    private static final Logger LOG = LoggerFactory.getLogger(TransUnionCrbServiceImpl.class);
    public static final String FORM_URL_CONTENT_TYPE = "Content-Type";
    @Autowired
    private Environment env;

    @Override
    @CronTarget(jobName = JobName.POST_RWANDA_CONSUMER_CREDIT_TO_TRANSUNION_CRB)
    public void ConsumerCreditDataUploadToTransUnion() throws JobExecutionException {
        LOG.info("Starting Consumer Credit Data Upload To TransUnion CRB");
        String token = authenticateToTransUnionRestApi();
        LOG.info("CRB Token == > " + token);

        String request = getConsumerCreditRequestData();
        postRwandaConsumerCreditToTransUnion(token, request);

    }

    private String authenticateToTransUnionRestApi() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(getConfigProperty("fineract.integrations.transUnion.crb.rest.authenticationUrl"))
                .newBuilder();
        String url = urlBuilder.build().toString();

        TransUnionAuthenticationData transUnionAuthenticationData = new TransUnionAuthenticationData(
                getConfigProperty("fineract.integrations.transUnion.crb.rest.username"),
                getConfigProperty("fineract.integrations.transUnion.crb.rest.password"),
                getConfigProperty("fineract.integrations.transUnion.crb.rest.infinityCode"));
        Gson gson = new GsonBuilder().create();
        RequestBody formBody = RequestBody.create(MediaType.parse(FORM_URL_CONTENT_TYPE), gson.toJson(transUnionAuthenticationData));

        OkHttpClient client = new OkHttpClient();
        Response response = null;
        Request request = new Request.Builder().url(url).post(formBody).build();

        List<Throwable> exceptions = new ArrayList<>();

        try {
            response = client.newCall(request).execute();
            String resObject = response.body().string();
            if (response.isSuccessful()) {

                JsonObject jsonResponse = JsonParser.parseString(resObject).getAsJsonObject();
                String accessToken = jsonResponse.get("token").getAsString();

                log.info("Login to CRB TransUnion is Successful");

                return accessToken;
            } else {
                log.error("Login to CRB TransUnion failed with Message:" + resObject);

                handleAPIIntegrityIssues(resObject);

            }
        } catch (Exception e) {
            log.error("Authentication to CRB TransUnion has failed" + e);
            exceptions.add(e);
        }
        if (!CollectionUtils.isEmpty(exceptions)) {
            try {
                throw new JobExecutionException(exceptions);
            } catch (JobExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private void handleAPIIntegrityIssues(String httpResponse) {
        throw new PlatformDataIntegrityException(httpResponse, httpResponse);
    }

    private String getConfigProperty(String propertyName) {
        return this.env.getProperty(propertyName);
    }

    private String postRwandaConsumerCreditToTransUnion(String accessToken, String consumerCreditData) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getConfigProperty("fineract.integrations.transUnion.crb.rest.postConsumerCredit"))
                .newBuilder();
        String url = urlBuilder.build().toString();

        OkHttpClient client = new OkHttpClient();
        Response response = null;

        RequestBody formBody = RequestBody.create(MediaType.parse(FORM_URL_CONTENT_TYPE), consumerCreditData);

        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json ").post(formBody).build();

        List<Throwable> exceptions = new ArrayList<>();

        try {
            response = client.newCall(request).execute();
            String resObject = response.body().string();
            if (response.isSuccessful()) {

                JsonObject jsonResponse = JsonParser.parseString(resObject).getAsJsonObject();
                log.info("Consumer Credit Response from TransUnion :=>" + resObject);

                return jsonResponse.get("callbackId").getAsString();

            } else {
                log.error("Post Consumer Credit to TransUnion failed with Message:" + resObject);

                handleAPIIntegrityIssues(resObject);

            }
        } catch (Exception e) {
            log.error("Post Consumer Credit to TransUnion has failed" + e);
            exceptions.add(e);
        }
        if (!CollectionUtils.isEmpty(exceptions)) {
            try {
                throw new JobExecutionException(exceptions);
            } catch (JobExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private static String getConsumerCreditRequestData() {
        RwandaConsumerCreditData rwandaConsumerCreditData = new RwandaConsumerCreditData();
        TransUnionRwandaConsumerCreditData transUnionRwandaConsumerCreditData = new TransUnionRwandaConsumerCreditData();
        transUnionRwandaConsumerCreditData.setCurrencyType("RWF");
        transUnionRwandaConsumerCreditData.setCountry("RWANDA");
        transUnionRwandaConsumerCreditData.setDrivingLicenseNumber("");
        transUnionRwandaConsumerCreditData.setOccupation("Other personal service activities n.e.c.");
        transUnionRwandaConsumerCreditData.setSurName("surname");
        transUnionRwandaConsumerCreditData.setStudentNumber(null);
        transUnionRwandaConsumerCreditData.setPostalCode("P.O.Box 000");
        transUnionRwandaConsumerCreditData.setDateAccountUpdated(20200926L);
        transUnionRwandaConsumerCreditData.setPhysicalAddressPlotNumber("");
        transUnionRwandaConsumerCreditData.setDaysInArrears(0);
        transUnionRwandaConsumerCreditData.setResidenceType("T");
        transUnionRwandaConsumerCreditData.setEmailAddress("");
        transUnionRwandaConsumerCreditData.setForeName3("");
        transUnionRwandaConsumerCreditData.setForeName2("");
        transUnionRwandaConsumerCreditData.setForeName1("HABIYAMBEREJOSEPHINE");
        transUnionRwandaConsumerCreditData.setOpeningBalance(BigDecimal.valueOf(16120000));
        transUnionRwandaConsumerCreditData.setPassportNumber("");
        transUnionRwandaConsumerCreditData.setSectorOfActivity("Other personal service activities n.e.c.");
        transUnionRwandaConsumerCreditData.setOldAccountNumber("");
        transUnionRwandaConsumerCreditData.setAvailableCredit(BigDecimal.ZERO);
        transUnionRwandaConsumerCreditData.setFacsimile("");
        transUnionRwandaConsumerCreditData.setAccountRepaymentTerm("MTH");
        transUnionRwandaConsumerCreditData.setAccountType("I");
        transUnionRwandaConsumerCreditData.setCurrentBalance(BigDecimal.valueOf(10549060));
        transUnionRwandaConsumerCreditData.setTaxNumber("");
        transUnionRwandaConsumerCreditData.setAccountOwner("O");
        transUnionRwandaConsumerCreditData.setClassification("1");
        transUnionRwandaConsumerCreditData.setPhysicalAddressDistrict("HUYE");
        transUnionRwandaConsumerCreditData.setEmployerAddressLine2("");
        transUnionRwandaConsumerCreditData.setEmployerAddressLine1("");
        transUnionRwandaConsumerCreditData.setIncomeFrequency("");
        transUnionRwandaConsumerCreditData.setHealthInsuranceNumber("");
        transUnionRwandaConsumerCreditData.setScheduledPaymentAmount(BigDecimal.valueOf(404971));
        transUnionRwandaConsumerCreditData.setGroupName("");
        transUnionRwandaConsumerCreditData.setMobileTelephone("250788636722");
        transUnionRwandaConsumerCreditData.setActualPaymentAmount(BigDecimal.valueOf(11380875));
        transUnionRwandaConsumerCreditData.setNationality("Rwanda");
        transUnionRwandaConsumerCreditData.setDateAccountOpened(20200926L);
        transUnionRwandaConsumerCreditData.setSalutation("MRS");
        transUnionRwandaConsumerCreditData.setInterestRateAtDisbursement(17.5);
        transUnionRwandaConsumerCreditData.setNoOfDependants(0);
        transUnionRwandaConsumerCreditData.setPhysicalAddressProvince("KIGALI");
        transUnionRwandaConsumerCreditData.setMaritalStatus("O");
        transUnionRwandaConsumerCreditData.setPostalAddressNumber("P.O.Box 000");
        transUnionRwandaConsumerCreditData.setCurrentBalanceIndicator("D");
        transUnionRwandaConsumerCreditData.setIncome(BigDecimal.ZERO);
        transUnionRwandaConsumerCreditData.setApprovalDate(20200926L);
        transUnionRwandaConsumerCreditData.setGender("F");
        transUnionRwandaConsumerCreditData.setSocialSecurityNumber("");
        transUnionRwandaConsumerCreditData.setPhysicalAddressSector("Kinunga");
        transUnionRwandaConsumerCreditData.setEmployerName("");
        transUnionRwandaConsumerCreditData.setFirstPaymentDate(20200926L);
        transUnionRwandaConsumerCreditData.setDateClosed("");
        transUnionRwandaConsumerCreditData.setAccountStatus("A");
        transUnionRwandaConsumerCreditData.setNumberOfJointLoanParticipants(0);
        transUnionRwandaConsumerCreditData.setTermsDuration(60);
        transUnionRwandaConsumerCreditData.setLastPaymentDate(20230223L);
        transUnionRwandaConsumerCreditData.setPhysicalAddressCell("Kinunga");
        transUnionRwandaConsumerCreditData.setHomeTelephone("");
        transUnionRwandaConsumerCreditData.setEmployerTown("");
        transUnionRwandaConsumerCreditData.setPhysicalAddressLine2("");
        transUnionRwandaConsumerCreditData.setPhysicalAddressLine1("");
        transUnionRwandaConsumerCreditData.setEmployerCountry("");
        transUnionRwandaConsumerCreditData.setPlaceOfBirth("Kinunga");
        transUnionRwandaConsumerCreditData.setPhysicalAddressPostalCode("");
        transUnionRwandaConsumerCreditData.setNature(33);
        transUnionRwandaConsumerCreditData.setDateOfBirth(19700210L);
        transUnionRwandaConsumerCreditData.setWorkTelephone("");
        transUnionRwandaConsumerCreditData.setAccountNumber("4004511751352");
        transUnionRwandaConsumerCreditData.setFinalPaymentDate(20251023L);
        transUnionRwandaConsumerCreditData.setGroupNumber(null);
        transUnionRwandaConsumerCreditData.setInstallmentsInArrears(0);
        transUnionRwandaConsumerCreditData.setNationalId("1119707000196801");
        transUnionRwandaConsumerCreditData.setAmountPastDue(BigDecimal.ZERO);
        transUnionRwandaConsumerCreditData.setCategory(40);

        rwandaConsumerCreditData.setConsumerCreditInformationRecord(transUnionRwandaConsumerCreditData);
        rwandaConsumerCreditData.setRecordType("IC");
        Gson gson = new GsonBuilder().create();
        String request = gson.toJson(rwandaConsumerCreditData);
        return request;
    }
}
