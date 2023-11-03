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

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaClientVerificationData;
import org.apache.fineract.portfolio.loanaccount.service.TransUnionCrbConsumerVerificationReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransUnionCrbVerificationWritePlatformServiceImpl implements TransUnionCrbVerificationWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(TransUnionCrbVerificationWritePlatformServiceImpl.class);
    public static final String FORM_URL_CONTENT_TYPE = "Content-Type";
    private final TransUnionCrbConsumerVerificationReadPlatformService transUnionCrbClientVerificationReadPlatformService;
    private final ClientRepositoryWrapper clientRepositoryWrapper;

    @Autowired
    private Environment env;

    @Override
    public CommandProcessingResult clientVerificationToTransUnionRwanda(Long clientId, JsonCommand command) {

        Client clientObj = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
        if (clientObj.getLegalForm().equals(LegalForm.PERSON.getValue())) {
            LOG.info("Client is a person :: >> " + clientId);
        } else {
            LOG.info("Client is a company :: >> " + clientId);
        }

        LOG.info("Verifying clients to TransUnion Rwanda :: >> " + clientId);
        final TransUnionRwandaClientVerificationData getProduct123 = this.transUnionCrbClientVerificationReadPlatformService
                .retrieveClientToBeVerifiedToTransUnion(clientId);
        LOG.info("Verifying clients to TransUnion Rwanda :: >> " + getProduct123.toString());

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getConfigProperty("fineract.integrations.transUnion.crb.soap.verifyClient"))
                .newBuilder();
        String url = urlBuilder.build().toString();

        OkHttpClient client = new OkHttpClient();
        Response response = null;

        RequestBody formBody = RequestBody.create(MediaType.parse(FORM_URL_CONTENT_TYPE), convertClientDataToJAXBRequest(getProduct123));

        Request request = new Request.Builder().url(url)
                .header("Authorization", "Basic " + base64EncodeCredentials("RWFq7NE3vz", "WxB4sZQXDyUaxL"))
                .header("Content-Type", "application/xml ").header("Content-Type", "text/xml ").post(formBody).build();
        try {
            response = client.newCall(request).execute();
            String resObject = response.body().string();

            if (response.isSuccessful()) {
                LOG.info("Response from TransUnion Rwanda :: >> " + resObject);
            } else {
                LOG.info("Response from TransUnion Rwanda :: >> " + resObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.info("Response from TransUnion Rwanda :: >> " + e.getMessage());
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(clientId).build();
    }

    private String getConfigProperty(String propertyName) {
        return this.env.getProperty(propertyName);
    }

    public String base64EncodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        byte[] credentialsBytes = credentials.getBytes(StandardCharsets.UTF_8);

        return Base64.getEncoder().encodeToString(credentialsBytes);
    }

    public String convertClientDataToJAXBRequest(TransUnionRwandaClientVerificationData getProduct123) {

        getProduct123.setUsername("WS_AEC");
        getProduct123.setPassword("1AFmtwa$*1mq");
        getProduct123.setCode("1570");
        getProduct123.setReportSector(1);
        getProduct123.setReportReason(2);
        getProduct123.setInfinityCode("rw123456789");

        try {
            JAXBContext context = JAXBContext.newInstance(TransUnionRwandaClientVerificationData.class);

            Marshaller marshaller = context.createMarshaller();

            StringWriter stringWriter = new StringWriter();

            stringWriter.write(
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.rw.crbws.transunion.ke.co/\">");
            stringWriter.write("<soapenv:Header/>");
            stringWriter.write("<soapenv:Body>");

            marshaller.marshal(getProduct123, stringWriter);

            stringWriter.write("</soapenv:Body>");
            stringWriter.write("</soapenv:Envelope>");

            String soapRequest = stringWriter.toString();
            soapRequest = soapRequest.replaceFirst("<\\?xml [^>]*\\?>", "");

            LOG.info("Request for verification :: >> " + soapRequest);
            return soapRequest;
        } catch (JAXBException e) {
            e.printStackTrace();
            LOG.info("Response from TransUnion Rwanda :: >> " + e.getMessage());
        }
        return null;
    }

}
