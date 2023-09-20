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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.security.SecureRandom;
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
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KivaLoanServiceImpl implements KivaLoanService {

    private static final Logger LOG = LoggerFactory.getLogger(KivaLoanServiceImpl.class);

    private static final SecureRandom RANDOM = new SecureRandom();

    private final LoanRepository loanRepository;
    @Autowired
    private Environment env;

    @Override
    @CronTarget(jobName = JobName.POST_LOAN_ACCOUNTS_TO_KIVA)
    public void postLoanAccountsToKiva() {
        List<Loan> loanList = loanRepository.findLoanAccountsToBePostedToKiva();
        LOG.info("Posting this Loan Account To Kiva And Size = = > " + loanList.size());
        // Authenticate to KIVA
        String accessToken = authenticateToKiva();
        LOG.info("Access Token = = > " + accessToken);
    }

    private String authenticateToKiva() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://auth-stage.dk1.kiva.org/oauth/token").newBuilder();
        String url = urlBuilder.build().toString();
        // String requestBody = "grant_type=client_credentials&scope=create:loan_draft
        // read:loans&audience=https://partner-api-stage.dk1.kiva.org&client_id=ovMOzQdBV3j94wEZuVo152leYJqgp6kyr&client_secret=wFgTfrmoG8MbsHRQ9AKE7ltPVPfRUDyblDH3Cp_cuYMpHeMe2ETo1EyBBN_7c_qnnM";
        StringBuilder requestBody = new StringBuilder();

        requestBody.append("grant_type=" + this.env.getProperty("fineract.integrations.kiva.grantType&"));
        requestBody.append("scope=" + this.env.getProperty("fineract.integrations.kiva.scope&"));
        requestBody.append("audience=" + this.env.getProperty("fineract.integrations.kiva.audience&"));
        requestBody.append("client_id=" + this.env.getProperty("fineract.integrations.kiva.clientId&"));
        requestBody.append("client_secret=" + this.env.getProperty("fineract.integrations.kiva.clientSecret"));

        OkHttpClient client = new OkHttpClient();
        Response response = null;
        String body = requestBody.toString();
        System.out.println("Body = = > " + body);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), body);

        Request request = new Request.Builder().url(url).header("Content-Type", "application/x-www-form-urlencoded").post(formBody).build();

        List<Throwable> exceptions = new ArrayList<>();

        try {
            response = client.newCall(request).execute();
            String resObject = response.body().string();
            if (response.isSuccessful()) {

                JsonObject jsonResponse = JsonParser.parseString(resObject).getAsJsonObject();
                String accessToken = jsonResponse.get("access_token").getAsString();

                log.info("Request to KIVA is Successfully with status code:=>" + accessToken);

                return accessToken;
            } else {
                log.error("Request to KIVA failed with Message:", resObject);

                handleAPIIntegrityIssues(resObject);

            }
        } catch (Exception e) {
            log.error("Authentication to KIVA has failed", e);
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

}
