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


import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;



@Service
@SuppressWarnings({ "unchecked", "rawtypes", "cast" })
public class OdooServiceImpl implements OdooService {

    private static final Logger LOG = LoggerFactory.getLogger(OdooServiceImpl.class);

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

    @Autowired
    public OdooServiceImpl(ClientRepositoryWrapper clientRepository, ConfigurationDomainService configurationDomainService) {
        this.clientRepository = clientRepository;
        this.configurationDomainService = configurationDomainService;
    }

    @Override
    public Integer loginToOddo() {
        try {
            final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
            final XmlRpcClient client = new XmlRpcClient();
            common_config.setServerURL(new URL(String.format("%s/xmlrpc/2/common", url)));

            Object uid = (Object) client.execute(common_config, "authenticate",
                    Arrays.asList(odooDB, username, password, Collections.emptyMap()));
            if (!uid.equals(false)) {
                LOG.info("Odoo Authentication successful uid" + uid);
                return (Integer) uid;
            } else {
                LOG.error("Odoo Authentication failed");
                return 0;
            }
        } catch (Exception e) {
            LOG.error("Odoo Authentication failure message", e);
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
                            Arrays.asList(odooDB, uid,
                                    password, "res.partner", "create",
                                    Arrays.asList(map)));
                    if(id != null){
                        LOG.info("Odoo Client created with id " + id);
                    }
                    return id;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                    partners = Arrays.asList((Object[]) models.execute("execute_kw",
                            Arrays.asList(odooDB, uid, password, "res.partner", "search_read",
                                    Arrays.asList(Arrays.asList(Arrays.asList("fineract_customer_id", "=", clientId.intValue()))),map)));
                    Integer partnerId = null;
                    if (partners != null && partners.size() > 0) {
                        HashMap partner = (HashMap) partners.get(0);
                        partnerId = (Integer) partner.get("id");
                    }
                    return partnerId;
                }
            }
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private XmlRpcClient getCommonConfig() {
        XmlRpcClient models;
        try {
            models = new XmlRpcClient() {
                {
                    setConfig(new XmlRpcClientConfigImpl() {

                        {
                            setServerURL(
                                    new URL(String.format("%s/xmlrpc/2/object", url)));
                        }
                    });
                }
            };
            return models;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    @Override
    @CronTarget(jobName = JobName.POST_CUSTOMERS_TO_ODDO)
    public void postClientsAndSavingsAccountToOddo() throws JobExecutionException {
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

            if (errors.size() > 0) { throw new JobExecutionException(errors); }
        }
    }

}
