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
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.loanaccount.data.CrbKenyaMetropolRequestData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
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

    @Autowired
    private Environment env;

    @Override
    public CommandProcessingResult loanVerificationToMetropolKenya(Long loanId, JsonCommand command) {
        Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);

        Client clientObj = this.clientRepositoryWrapper.findOneWithNotFoundDetection(loan.getClientId());
        if (clientObj.getLegalForm().equals(LegalForm.PERSON.getValue())) {
            try {
                String dummyId = "660000066";
                CrbKenyaMetropolRequestData requestData = new CrbKenyaMetropolRequestData(1, dummyId, "001");
                String jsonPayload = convertRequestPayloadToJson(requestData);

                String timestamp = DateUtils.generateTimestamp();
                String hash = generateHash(jsonPayload, timestamp);

                sendRequest(getConfigProperty("fineract.integrations.metropol.crb.rest.clientVerify"), jsonPayload, timestamp, hash);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(clientObj.getId()).build();
    }

    private void sendRequest(String urlStr, String payload, String timestamp, String hash) {
        OkHttpClient client = new OkHttpClient();

        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse(FORM_URL_CONTENT_TYPE), payload);
            Request request = new Request.Builder().url(urlStr).post(requestBody)
                    .addHeader("X-METROPOL-REST-API-KEY", getConfigProperty("fineract.integrations.metropol.crb.rest.publicKey"))
                    .addHeader("X-METROPOL-REST-API-HASH", hash).addHeader("X-METROPOL-REST-API-TIMESTAMP", timestamp)
                    .addHeader("Content-Type", "application/json").build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    System.out.println(response.body().string());
                } else {
                    System.out.println("Error in request: " + response.code() + " - " + response.message());
                }
            }
        } catch (IOException e) {
            System.out.println("Error sending request: " + e.getMessage());
        }
    }

    private String generateHash(String payload, String timestamp) throws NoSuchAlgorithmException {
        String concatenatedString = getConfigProperty("fineract.integrations.metropol.crb.rest.privateKey") + payload
                + getConfigProperty("fineract.integrations.metropol.crb.rest.publicKey") + timestamp;
        System.out.println("Concatenated string: " + concatenatedString);

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
}
