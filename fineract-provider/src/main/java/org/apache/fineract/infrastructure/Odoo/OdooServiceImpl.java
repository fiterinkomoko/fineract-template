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
package org.apache.fineract.infrastructure.Odoo;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepository;
import org.apache.fineract.infrastructure.Odoo.exception.OdooFailedException;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionNotPostedToOdooInstanceData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({ "unchecked", "rawtypes", "cast" })
public class OdooServiceImpl implements OdooService {

    private static final Logger LOG = LoggerFactory.getLogger(OdooServiceImpl.class);
    public static final String FORM_URL_CONTENT_TYPE = "Content-Type";

    @Value("${fineract.integrations.odoo.db}")
    private String odooDB;

    @Value("${fineract.integrations.odoo.username}")
    private String username;

    @Value("${fineract.integrations.odoo.password}")
    private String password;

    @Value("${fineract.integrations.odoo.url}")
    private String url;
    private ClientRepositoryWrapper clientRepository;
    private ConfigurationDomainService configurationDomainService;

    private final JournalEntryRepository journalEntryRepository;
    private final LoanReadPlatformService loanReadPlatformService;

    @Autowired
    public OdooServiceImpl(ClientRepositoryWrapper clientRepository, ConfigurationDomainService configurationDomainService,
            JournalEntryRepository journalEntryRepository, LoanReadPlatformService loanReadPlatformService) {
        this.clientRepository = clientRepository;
        this.configurationDomainService = configurationDomainService;
        this.journalEntryRepository = journalEntryRepository;
        this.loanReadPlatformService = loanReadPlatformService;
    }

    @Override
    public Integer loginToOddo() {
        try {
            final XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
            final XmlRpcClient client = new XmlRpcClient();
            commonConfig.setServerURL(new URL(String.format("%s/xmlrpc/2/common", url)));

            Object uid = (Object) client.execute(commonConfig, "authenticate",
                    Arrays.asList(odooDB, username, password, Collections.emptyMap()));
            if (!uid.equals(false)) {
                LOG.info("Odoo Authentication successful uid" + uid);
                return (Integer) uid;
            } else {
                LOG.error("Odoo Authentication failed");
                return 0;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return 0;
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "cast" })
    @Override
    public Integer createCustomerToOddo(Client client) {
        try {
            final Integer uid = loginToOddo();
            if (uid > 0) {
                XmlRpcClient models = getCommonConfig();
                // Create client
                Integer partnerId = getPartner(client.getId(), uid, models);
                if (partnerId == null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", client.getDisplayName());
                    map.put("mobile", client.getMobileNo() != null ? client.getMobileNo() : false);
                    map.put("customer_rank", 1);
                    map.put("fineract_customer_id", client.getId().toString() != null ? client.getId().toString() : false);
                    map.put("is_company", LegalForm.fromInt(client.getLegalForm().intValue()).isEntity() ? true : false);

                    final Integer id = (Integer) models.execute("execute_kw",
                            Arrays.asList(odooDB, uid, password, "res.partner", "create", Arrays.asList(map)));
                    if (id != null) {
                        LOG.info("Odoo Client created with id " + id);
                    }
                    return id;
                }
            }
        } catch (XmlRpcException e) {
            throw new OdooFailedException(e);
        }
        return null;
    }

    private Integer getPartner(Long clientId, Integer uid, XmlRpcClient models) {

        try {
            if (uid > 0) {
                List partners;
                Map<String, Object> map = new HashMap<>();
                map.put("fields", Arrays.asList("id", "name", "email"));
                map.put("limit", 5);

                if (clientId != null) {
                    partners = Arrays.asList(
                            (Object[]) models.execute("execute_kw", Arrays.asList(odooDB, uid, password, "res.partner", "search_read",
                                    Arrays.asList(Arrays.asList(Arrays.asList("fineract_customer_id", "=", clientId.intValue()))), map)));
                    Integer partnerId = null;
                    if (partners != null && partners.size() > 0) {
                        HashMap partner = (HashMap) partners.get(0);
                        partnerId = (Integer) partner.get("id");
                    }
                    return partnerId;
                }
            }
        } catch (XmlRpcException e) {
            throw new OdooFailedException(e);
        }
        return null;
    }

    private XmlRpcClient getCommonConfig() {
        XmlRpcClient models;
        models = new XmlRpcClient() {

            {
                setConfig(new XmlRpcClientConfigImpl() {

                    {
                        try {
                            setServerURL(new URL(String.format("%s/xmlrpc/2/object", url)));
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        return models;
    }

    @Override
    @CronTarget(jobName = JobName.POST_CUSTOMERS_TO_ODDO)
    public void postClientsToOddo() throws JobExecutionException {
        Boolean isOdooEnabled = this.configurationDomainService.isOdooIntegrationEnabled();
        if (isOdooEnabled) {
            List<Client> clients = this.clientRepository.getClientByIsOdooPosted(false);

            List<Throwable> errors = new ArrayList<>();

            if (clients != null && clients.size() > 0) {
                for (Client client : clients) {
                    try {
                        Integer id = createCustomerToOddo(client);
                        if (id != null) {
                            client.setOdooCustomerPosted(true);
                            client.setOdooCustomerId(id);
                            this.clientRepository.saveAndFlush(client);
                        }
                    } catch (Exception e) {
                        Throwable realCause = e;
                        if (e.getCause() != null) {
                            realCause = e.getCause();
                        }
                        LOG.error("Error occurred while posting client to Odoo with id " + client.getId() + " message "
                                + realCause.getMessage());
                        errors.add(realCause);
                    }
                }
            }

            if (errors.size() > 0) {
                throw new JobExecutionException(errors);
            }
        }
    }

    @Override
    public Boolean updateCustomerToOddo(Client client) {
        try {
            final Integer uid = loginToOddo();
            if (uid > 0) {
                XmlRpcClient models = getCommonConfig();
                Integer partnerId = getPartner(client.getId(), uid, models);
                // Update client
                if (partnerId != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", client.getDisplayName());
                    map.put("mobile", client.getMobileNo() != null ? client.getMobileNo() : false);

                    Boolean status = (Boolean) models.execute("execute_kw",
                            Arrays.asList(odooDB, uid, password, "res.partner", "write", Arrays.asList(Arrays.asList(partnerId), map)));

                    LOG.info("Odoo Client updated with id " + partnerId);
                    return status;
                }
            }
        } catch (XmlRpcException e) {
            throw new OdooFailedException(e);
        }
        return false;
    }

    @Override
    @CronTarget(jobName = JobName.POST_UPDATED_DETAILS_OF_CUSTOMER_TO_ODDO)
    public void postCustomerUpdatedDetailsToOddo() throws JobExecutionException {
        Boolean isOdooEnabled = this.configurationDomainService.isOdooIntegrationEnabled();
        if (isOdooEnabled) {
            List<Client> clients = this.clientRepository.getClientUpdatedDetailsNotPostedToOdoo(true);
            List<Throwable> errors = new ArrayList<>();

            if (clients != null && clients.size() > 0) {
                for (Client client : clients) {
                    try {
                        Boolean status = updateCustomerToOddo(client);
                        updateClientWithOdooUpdateStatus(status, client);
                    } catch (Exception e) {
                        Throwable realCause = e;
                        if (e.getCause() != null) {
                            realCause = e.getCause();
                        }
                        LOG.error("Error occurred while updating client to Odoo with id " + client.getId() + " message "
                                + realCause.getMessage());
                        errors.add(realCause);
                    }
                }
            }
            if (errors.size() > 0) {
                throw new JobExecutionException(errors);
            }
        }
    }

    public void updateClientWithOdooUpdateStatus(boolean status, Client client) {
        if (status) {
            client.setUpdatedToOdoo(true);
            this.clientRepository.saveAndFlush(client);
        }
    }

    @Override
    public String createJournalEntryToOddo(List<JournalEntry> list, Long loanTransactionId, Long transactionType) throws IOException {

        final Integer uid = loginToOddo();
        if (uid > 0) {
            XmlRpcClient models = getCommonConfig();

            AccountingEntry journalEntry = null;
            List<AccountingEntry> accounting_entries = new ArrayList<>();

            JournalEntryToOdooData journalEntryToOdooData = new JournalEntryToOdooData();
            JournalData journalData = new JournalData();

            for (JournalEntry entry : list) {

                Integer accountId = getGlAccounts(entry, uid, models);
                Client client = entry.getClient();
                Integer partnerId = getPartner(client.getId(), uid, models);

                journalEntry = new AccountingEntry(entry, accountId, partnerId);
                accounting_entries.add(journalEntry);
                if (partnerId == null || accountId == null) {
                    throw new GeneralPlatformDomainRuleException(
                            "error.posting.journal.entries.to.odoo.has.failed.due.to.missing.client.or.gl.account",
                            "Error occurred while creating Journal Entry to Odoo with Loan Transaction Id  " + loanTransactionId
                                    + " and Type " + transactionType
                                    + " Error: Account or Partner not found. The Client is not posted or GL account is not available");
                }
            }

            // Create journal entry
            journalEntryToOdooData.setUsername(username);
            journalEntryToOdooData.setPassword(password);
            journalEntryToOdooData.setCbs_journal_entry_id(loanTransactionId.toString());

            journalData.setRef("Journal Entry made by CBS ");
            journalData.setTransaction_type_name(LoanTransactionType.fromInt(transactionType.intValue()).getCode());
            journalData.setTransaction_type_unique_id(transactionType.toString());

            journalEntryToOdooData.setJournal(journalData);
            journalEntryToOdooData.setAccounting_entries(accounting_entries);
            LOG.info("Journal Entry to Odoo " + journalEntryToOdooData);
            String jsonPayload = convertRequestPayloadToJson(journalEntryToOdooData);

            JsonObject res = sendRequest(jsonPayload);
            return getStringField(res, "journal_entry_no");
        }
        return null;
    }

    private JsonObject sendRequest(String payload) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(MediaType.parse(FORM_URL_CONTENT_TYPE), payload);
        Request request = new Request.Builder().url(url + "/cbs/dev/journal_entry").post(requestBody)
                .addHeader("Content-Type", "application/json").build();

        Response response = httpClient.newCall(request).execute();

        if (response.isSuccessful()) {

            String resObject = response.body().string();
            LOG.info("Response on Odoo Journal Entry Posting: " + resObject);
            return JsonParser.parseString(resObject).getAsJsonObject();
        } else {
            throw new GeneralPlatformDomainRuleException("error.msg.journal.entry.posting.to.odoo.failed",
                    " Failed to post Journal Entries to Odoo: " + response.code() + ":" + response.message());
        }

    }

    @Override
    @CronTarget(jobName = JobName.POST_JOURNAL_ENTRY_TO_ODDO)
    public void postJournalEntryToOddo() throws JobExecutionException {
        Boolean isOdooEnabled = this.configurationDomainService.isOdooIntegrationEnabled();
        List<Throwable> errors = new ArrayList<>();
        if (isOdooEnabled) {
            // get loan accounts with transactions not posted to Odoo
            List<LoanTransactionNotPostedToOdooInstanceData> loanTransactionNotPostedToOdooInstanceData = loanReadPlatformService
                    .retrieveLoanTransactionWhoseJournalEntriesAreNotPostedToOdoo();
            LOG.info("Loan Transaction Not Posted to Odoo " + loanTransactionNotPostedToOdooInstanceData.toString());
            if (!CollectionUtils.isEmpty(loanTransactionNotPostedToOdooInstanceData)) {
                for (LoanTransactionNotPostedToOdooInstanceData transaction : loanTransactionNotPostedToOdooInstanceData) {
                    LOG.info("Loan Transaction Not Posted to Odoo " + transaction.toString());
                    List<JournalEntry> JE = this.journalEntryRepository.findJournalEntriesByIsOddoPosted(false,
                            transaction.getLoanTransactionId());
                    postJournalEntries(errors, JE, transaction.getLoanTransactionId(), transaction.getTransactionType());
                }
            }

            if (errors.size() > 0) {
                throw new JobExecutionException(errors);
            }
        }
    }

    private void postJournalEntries(List<Throwable> errors, List<JournalEntry> journalEntryDebitCredit, Long loanTransactionId,
            Long transactionType) {
        if (!CollectionUtils.isEmpty(journalEntryDebitCredit)) {
            try {

                if (journalEntryDebitCredit.size() > 1) {
                    String id = createJournalEntryToOddo(journalEntryDebitCredit, loanTransactionId, transactionType);
                    if (id != null) {
                        for (JournalEntry je : journalEntryDebitCredit) {
                            je.setOddoPosted(true);
                            je.setOdooJournalId(id);
                            this.journalEntryRepository.saveAndFlush(je);
                        }
                    }
                }
            } catch (Exception e) {
                Throwable realCause = e;
                if (e.getCause() != null) {
                    realCause = e.getCause();
                }
                LOG.error("Error occurred while updating Journals to Odoo with Loan Transaction Id  " + loanTransactionId + " and Type "
                        + transactionType + realCause.getMessage());
                errors.add(realCause);
            }
        }
    }

    private Integer getGlAccounts(JournalEntry entry, Integer uid, XmlRpcClient models) {
        try {
            if (uid > 0) {
                final List glAccount = Arrays.asList((Object[]) models.execute("execute_kw",
                        Arrays.asList(odooDB, uid, password, "account.account", "search_read",
                                Arrays.asList(Arrays.asList(Arrays.asList("code", "=", extractGlCode(entry.getGlAccount().getGlCode())))),
                                Map.of("fields", Arrays.asList("id"), "limit", 5))));
                Integer id = null;
                if (glAccount != null && glAccount.size() > 0) {
                    HashMap account = (HashMap) glAccount.get(0);
                    id = (Integer) account.get("id");
                }

                return id;
            }
        } catch (XmlRpcException e) {
            throw new OdooFailedException(e);
        }
        return null;
    }

    /*
     * This will help extract the GL Code from the concatenated GL code with id posted to fineract during data
     * migration. The Code on Odoo is not unique so we concatenate the GL code with the GL id {code_id} so we need to
     * extract the GLCode from the code again if we want the integration with Odoo from Fineract work as expected. We
     * accommodated the aspect of the GL code not concatenated with the id if this is created in fineract direct
     */
    private String extractGlCode(String glCode) {
        if (glCode.contains("-")) {
            List<String> parts = Splitter.on(Pattern.compile("-", Pattern.LITERAL)).splitToList(glCode);
            return parts.get(0);
        } else {
            return glCode;
        }
    }

    private String convertRequestPayloadToJson(JournalEntryToOdooData journalEntryToOdooData) {
        Gson gson = new GsonBuilder().create();
        String request = gson.toJson(journalEntryToOdooData);
        LOG.info("Actual (Journal Entries) Payload to be sent to Odoo API - - >" + request);
        return request;
    }

    public String getStringField(JsonObject jsonObject, String fieldName) {
        if (jsonObject != null && jsonObject.has(fieldName) && jsonObject.get(fieldName).isJsonPrimitive()
                && jsonObject.get(fieldName).getAsJsonPrimitive().isString()) {
            return jsonObject.get(fieldName).getAsString();
        }
        return null;
    }

}
