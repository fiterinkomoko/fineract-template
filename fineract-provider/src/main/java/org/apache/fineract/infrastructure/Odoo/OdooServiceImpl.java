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

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.Collections;
import java.util.Arrays;


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


    @Autowired
    public OdooServiceImpl() {
    }

    @Override
    public Integer loginToOddo() {
        try {
            final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
            final XmlRpcClient client = new XmlRpcClient();
            common_config
                    .setServerURL(new URL(String.format("%s/xmlrpc/2/common", url)));

            Object uid = (Object) client.execute(common_config, "authenticate",
                    Arrays.asList(odooDB, username, password, Collections.emptyMap()));
            if (!uid.equals(false))
            {
                LOG.info("Login successful" + uid);
                return (Integer) uid;
            }else{
                LOG.error("Login failed");
                return 0;
            }
        } catch (Exception e) {
            LOG.error("message", e);
        }
        return 0;
    }

}
