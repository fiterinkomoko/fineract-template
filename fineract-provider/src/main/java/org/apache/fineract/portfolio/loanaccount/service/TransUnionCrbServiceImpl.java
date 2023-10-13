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
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformServiceImpl;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.client.domain.ClientRecruitmentSurveyRepository;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionAuthenticationData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
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
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String FORM_URL_CONTENT_TYPE = "Content-Type";
    public static final String LOAN_STATUS = "payingBack";
    public static final Integer INITIAL_LOAN_LIMIT = 100;
    public static final Long ACTIVITY_ID = 110L;
    public static final Integer DESCRIPTION_LANGUAGE_ID = 1;

    private final LoanRepository loanRepository;
    private final DocumentReadPlatformServiceImpl documentReadPlatformService;
    private final ClientRecruitmentSurveyRepository clientRecruitmentSurveyRepository;
    private final KivaLoanAwaitingApprovalReadPlatformService kivaLoanAwaitingApprovalReadPlatformService;
    @Autowired
    private Environment env;

    @Override
    @CronTarget(jobName = JobName.POST_RWANDA_CONSUMER_CREDIT_TO_TRANSUNION_CRB)
    public void ConsumerCreditDataUploadToTransUnion() throws JobExecutionException {
        LOG.info("Starting Consumer Credit Data Upload To TransUnion CRB");
        String token = authenticateToTransUnionRestApi();
        LOG.info("CRB Token == > " + token);

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

}
