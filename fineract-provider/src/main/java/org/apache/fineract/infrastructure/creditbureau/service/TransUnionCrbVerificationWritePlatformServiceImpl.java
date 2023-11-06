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

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import org.apache.fineract.portfolio.loanaccount.data.HeaderData;
import org.apache.fineract.portfolio.loanaccount.data.PersonalProfileData;
import org.apache.fineract.portfolio.loanaccount.data.ScoreOutputData;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaClientVerificationData;
import org.apache.fineract.portfolio.loanaccount.data.TransUnionRwandaClientVerificationResponseData;
import org.apache.fineract.portfolio.loanaccount.domain.TransunionCrbHeader;
import org.apache.fineract.portfolio.loanaccount.domain.TransunionCrbHeaderRepository;
import org.apache.fineract.portfolio.loanaccount.domain.TransunionCrbPersonalProfile;
import org.apache.fineract.portfolio.loanaccount.domain.TransunionCrbPersonalProfileRepository;
import org.apache.fineract.portfolio.loanaccount.domain.TransunionCrbScoreOutput;
import org.apache.fineract.portfolio.loanaccount.domain.TransunionCrbScoreOutputRepository;
import org.apache.fineract.portfolio.loanaccount.service.TransUnionCrbConsumerVerificationReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Service
@RequiredArgsConstructor
public class TransUnionCrbVerificationWritePlatformServiceImpl implements TransUnionCrbVerificationWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(TransUnionCrbVerificationWritePlatformServiceImpl.class);
    public static final String FORM_URL_CONTENT_TYPE = "Content-Type";
    private final TransUnionCrbConsumerVerificationReadPlatformService transUnionCrbClientVerificationReadPlatformService;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final TransunionCrbHeaderRepository transunionCrbHeaderRepository;
    private final TransunionCrbPersonalProfileRepository transunionCrbPersonalProfileRepository;
    private final TransunionCrbScoreOutputRepository transunionCrbScoreOutputRepository;

    @Autowired
    private Environment env;

    @Override
    public CommandProcessingResult clientVerificationToTransUnionRwanda(Long clientId, JsonCommand command) {

        Client clientObj = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);

        TransUnionRwandaClientVerificationData getProduct123 = null;

        if (clientObj.getLegalForm().equals(LegalForm.PERSON.getValue())) {
            getProduct123 = this.transUnionCrbClientVerificationReadPlatformService.retrieveConsumerToBeVerifiedToTransUnion(clientId);
        } else {
            getProduct123 = this.transUnionCrbClientVerificationReadPlatformService.retrieveCorporateToBeVerifiedToTransUnion(clientId);
        }

        LOG.info("Verifying clients to TransUnion Rwanda Request :: >> " + getProduct123.toString());

        HttpUrl.Builder urlBuilder = HttpUrl.parse(getConfigProperty("fineract.integrations.transUnion.crb.soap.verifyClient"))
                .newBuilder();
        String url = urlBuilder.build().toString();

        OkHttpClient client = new OkHttpClient();
        Response response = null;

        RequestBody formBody = RequestBody.create(MediaType.parse(FORM_URL_CONTENT_TYPE), convertClientDataToJAXBRequest(getProduct123));

        Request request = new Request.Builder().url(url).header("Authorization",
                "Basic " + base64EncodeCredentials(getConfigProperty("fineract.integrations.transUnion.crb.soap.transportLevel.username"),
                        getConfigProperty("fineract.integrations.transUnion.crb.soap.transportLevel.password")))
                .header("Content-Type", "application/xml ").header("Content-Type", "text/xml ").post(formBody).build();
        try {
            response = client.newCall(request).execute();
            String resObject = response.body().string();

            if (response.isSuccessful()) {
                LOG.info("Response from TransUnion Rwanda :: >> " + resObject);
                saveConsumerVerificattionReport(clientObj, resObject);
            } else {
                LOG.error("Response from TransUnion Rwanda  Consumer credit Verification :: >> " + resObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.info("Failed to Verify Consumer credit  :: >> " + e.getMessage());
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(clientId).build();
    }

    private void saveConsumerVerificattionReport(Client clientObj, String resObject) {
        TransUnionRwandaClientVerificationResponseData clientVerificationResponseData = convertXmlToPojoObj(resObject);
        if (clientVerificationResponseData != null) {
            HeaderData headerData = clientVerificationResponseData.getHeader();
            PersonalProfileData personalProfileData = clientVerificationResponseData.getPersonalProfile();
            ScoreOutputData scoreOutputData = clientVerificationResponseData.getScoreOutput();
            if (headerData != null) {
                TransunionCrbHeader transunionCrbHeader = new TransunionCrbHeader(clientObj, headerData);
                transunionCrbHeaderRepository.saveAndFlush(transunionCrbHeader);
                if (personalProfileData != null) {
                    TransunionCrbPersonalProfile transunionCrbPersonalProfile = new TransunionCrbPersonalProfile(transunionCrbHeader,
                            personalProfileData);
                    transunionCrbPersonalProfileRepository.saveAndFlush(transunionCrbPersonalProfile);
                }
                if (scoreOutputData != null) {
                    TransunionCrbScoreOutput scoreOutput = new TransunionCrbScoreOutput(transunionCrbHeader, scoreOutputData);
                    transunionCrbScoreOutputRepository.saveAndFlush(scoreOutput);
                }
            }

        }
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

        addExtraDetailsToProduct123(getProduct123);

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

            String request = stringWriter.toString();
            request = request.replaceFirst("<\\?xml [^>]*\\?>", "");

            LOG.info(" unmarshal Consumer verification Response to Pojo :: >> " + request);
            return request;
        } catch (JAXBException e) {
            e.printStackTrace();
            LOG.error("Failure to unmarshal consumer credit :: >> " + e.getMessage());
        }
        return null;
    }

    private void addExtraDetailsToProduct123(TransUnionRwandaClientVerificationData getProduct123) {
        getProduct123.setUsername(getConfigProperty("fineract.integrations.transUnion.crb.soap.messageLevel.username"));
        getProduct123.setPassword(getConfigProperty("fineract.integrations.transUnion.crb.soap.messageLevel.password"));
        getProduct123.setCode("1570");
        getProduct123.setReportSector(1);
        getProduct123.setReportReason(2);
        getProduct123.setInfinityCode(getConfigProperty("fineract.integrations.transUnion.crb.rest.infinityCode"));
    }

    public TransUnionRwandaClientVerificationResponseData convertXmlToPojoObj(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));

            NodeList product123ResponseNodes = document.getElementsByTagName("ns2:getProduct123Response");
            if (product123ResponseNodes.getLength() > 0) {
                Element getProduct123ResponseElement = (Element) product123ResponseNodes.item(0);

                TransUnionRwandaClientVerificationResponseData product123Response = new TransUnionRwandaClientVerificationResponseData();

                product123Response.setHeader(extractHeader(getProduct123ResponseElement));
                product123Response.setPersonalProfile(extractPersonalProfile(getProduct123ResponseElement));
                product123Response.setScoreOutput(extractScoreOutputData(getProduct123ResponseElement));
                LOG.info("Response from TransUnion Rwanda  product123Response:: >> " + product123Response.toString());
                return product123Response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.info("Response from TransUnion Rwanda  Extraction:: >> " + e.getMessage());
        }

        return null;
    }

    private HeaderData extractHeader(Element getProduct123ResponseElement) {
        HeaderData header = new HeaderData();
        Element headerElement = (Element) getProduct123ResponseElement.getElementsByTagName("header").item(0);

        header.setCrbName(headerElement.getElementsByTagName("crbName").item(0).getTextContent());
        header.setPdfId(headerElement.getElementsByTagName("pdfId").item(0).getTextContent());
        header.setProductDisplayName(headerElement.getElementsByTagName("productDisplayName").item(0).getTextContent());
        header.setReportDate(headerElement.getElementsByTagName("reportDate").item(0).getTextContent());
        header.setReportType(headerElement.getElementsByTagName("reportType").item(0).getTextContent());
        header.setRequestNo(headerElement.getElementsByTagName("requestNo").item(0).getTextContent());
        header.setRequester(headerElement.getElementsByTagName("requester").item(0).getTextContent());

        return header;
    }

    private PersonalProfileData extractPersonalProfile(Element getProduct123ResponseElement) {
        PersonalProfileData personalProfileData = new PersonalProfileData();
        Element headerElement = (Element) getProduct123ResponseElement.getElementsByTagName("personalProfile").item(0);

        personalProfileData.setCrn(headerElement.getElementsByTagName("crn").item(0).getTextContent());
        personalProfileData.setDateOfBirth(headerElement.getElementsByTagName("dateOfBirth").item(0).getTextContent());
        personalProfileData.setFullName(headerElement.getElementsByTagName("fullName").item(0).getTextContent());
        personalProfileData.setGender(headerElement.getElementsByTagName("gender").item(0).getTextContent());
        personalProfileData.setHealthInsuranceNo(headerElement.getElementsByTagName("healthInsuranceNo").item(0).getTextContent());
        personalProfileData.setMaritalStatus(headerElement.getElementsByTagName("maritalStatus").item(0).getTextContent());
        personalProfileData.setNationalID(headerElement.getElementsByTagName("nationalID").item(0).getTextContent());
        personalProfileData.setOtherNames(headerElement.getElementsByTagName("otherNames").item(0).getTextContent());
        personalProfileData.setSalutation(headerElement.getElementsByTagName("salutation").item(0).getTextContent());
        personalProfileData.setSurname(headerElement.getElementsByTagName("surname").item(0).getTextContent());

        return personalProfileData;
    }

    private ScoreOutputData extractScoreOutputData(Element getProduct123ResponseElement) {
        ScoreOutputData scoreOutputData = new ScoreOutputData();
        Element headerElement = (Element) getProduct123ResponseElement.getElementsByTagName("scoreOutput").item(0);

        scoreOutputData.setGrade(headerElement.getElementsByTagName("grade").item(0).getTextContent());
        scoreOutputData.setPositiveScore(headerElement.getElementsByTagName("positiveScore").item(0).getTextContent());
        scoreOutputData.setProbability(headerElement.getElementsByTagName("probability").item(0).getTextContent());
        scoreOutputData.setReasonCodeAARC1(headerElement.getElementsByTagName("reasonCodeAARC1").item(0).getTextContent());
        scoreOutputData.setReasonCodeAARC2(headerElement.getElementsByTagName("reasonCodeAARC2").item(0).getTextContent());
        scoreOutputData.setReasonCodeAARC3(headerElement.getElementsByTagName("reasonCodeAARC3").item(0).getTextContent());
        scoreOutputData.setReasonCodeAARC4(headerElement.getElementsByTagName("reasonCodeAARC4").item(0).getTextContent());

        return scoreOutputData;
    }

}
