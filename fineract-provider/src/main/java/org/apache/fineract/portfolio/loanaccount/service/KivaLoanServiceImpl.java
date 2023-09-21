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
import java.sql.Date;
import java.time.LocalDate;
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
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.data.KivaLoanAccount;
import org.apache.fineract.portfolio.loanaccount.data.KivaLoanAccountSchedule;
import org.apache.fineract.portfolio.loanaccount.data.LoanDetailToKivaData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.serialization.KivaDateSerializerApi;
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
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final Long THEME_TYPE_ID = 263L;
    public static final Long ACTIVITY_ID = 110L;
    public static final Integer DESCRIPTION_LANGUAGE_ID = 1;

    private final LoanRepository loanRepository;
    @Autowired
    private Environment env;

    @Override
    @CronTarget(jobName = JobName.POST_LOAN_ACCOUNTS_TO_KIVA)
    public void postLoanAccountsToKiva() {
        // Authenticate to KIVA
        String accessToken = authenticateToKiva();

        List<KivaLoanAccountSchedule> kivaLoanAccountSchedules = new ArrayList<>();
        List<KivaLoanAccount> kivaLoanAccounts = new ArrayList<>();
        List<Boolean> notPictured = new ArrayList<>();
        notPictured.add(Boolean.TRUE);

        List<Loan> loanList = loanRepository.findLoanAccountsToBePostedToKiva();

        LOG.info("Posting this Loan Account To Kiva And Size = = > " + loanList.size());

        for (Loan loan : loanList) {
            String loanToKiva = loanPayloadToKivaMapper(kivaLoanAccountSchedules, kivaLoanAccounts, notPictured, loan);
            LOG.info("Loan Account To be Sent to Kiva : =GSON = >  " + loanToKiva);

        }
    }

    private String loanPayloadToKivaMapper(List<KivaLoanAccountSchedule> kivaLoanAccountSchedules, List<KivaLoanAccount> kivaLoanAccounts,
            List<Boolean> notPictured, Loan loan) {
        Client client = loan.getClient();
        String gender = (client.gender() != null) ? client.gender().label() : "";

        KivaLoanAccount loanAccount = new KivaLoanAccount(loan.getNetDisbursalAmount(), client.getId().toString(), client.getFirstname(),
                gender, client.getLastname(), loan.getId().toString());
        kivaLoanAccounts.add(loanAccount);

        for (LoanRepaymentScheduleInstallment scheduleInstallment : loan.getRepaymentScheduleInstallments()) {
            KivaLoanAccountSchedule schedule = new KivaLoanAccountSchedule(Date.valueOf(scheduleInstallment.getDueDate()),
                    scheduleInstallment.getInterestOutstanding(loan.getCurrency()).getAmount(),
                    scheduleInstallment.getPrincipal(loan.getCurrency()).getAmount());
            kivaLoanAccountSchedules.add(schedule);
        }

        // build final object
        LoanDetailToKivaData loanDetailToKivaData = new LoanDetailToKivaData(ACTIVITY_ID, Boolean.TRUE, loan.getCurrencyCode(),
                "Loan From Fineract", DESCRIPTION_LANGUAGE_ID, Date.valueOf(loan.getDisbursementDate()), "",
                "https://res.cloudinary.com/dile4yok6/image/upload/v1636108796/image-5_usok73.png", client.getId().toString(),
                generateInternalLoanId(loan.getDisbursementDate(), loan.getId()), "", "", THEME_TYPE_ID, kivaLoanAccounts,
                kivaLoanAccountSchedules, notPictured);

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new KivaDateSerializerApi()).create();

        return gson.toJson(loanDetailToKivaData);
    }

    private String authenticateToKiva() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(getConfigProperty("fineract.integrations.kiva.oAuthUrl")).newBuilder();
        String url = urlBuilder.build().toString();

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("grant_type=" + getConfigProperty("fineract.integrations.kiva.grantType"));
        requestBody.append("&scope=" + getConfigProperty("fineract.integrations.kiva.scope"));
        requestBody.append("&audience=" + getConfigProperty("fineract.integrations.kiva.audience"));
        requestBody.append("&client_id=" + getConfigProperty("fineract.integrations.kiva.clientId"));
        requestBody.append("&client_secret=" + getConfigProperty("fineract.integrations.kiva.clientSecret"));

        OkHttpClient client = new OkHttpClient();
        Response response = null;

        RequestBody formBody = RequestBody.create(MediaType.parse(FORM_URL_ENCODED), requestBody.toString());

        Request request = new Request.Builder().url(url).header("Content-Type", FORM_URL_ENCODED).post(formBody).build();

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

    private String getConfigProperty(String propertyName) {
        return this.env.getProperty(propertyName);
    }

    private String generateInternalLoanId(LocalDate disbursementDate, Long loanId) {
        Integer year = disbursementDate.getYear();
        return "LOAN/" + loanId + "/" + year;
    }

}
