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
package org.apache.fineract.portfolio.loanaccount.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "getProduct123Response", namespace = "http://ws.rw.crbws.transunion.ke.co/")
public class TransUnionRwandaConsumerVerificationResponseData {

    private HeaderData header;
    private PersonalProfileData personalProfile;
    private Integer responseCode;
    private ScoreOutputData scoreOutput;
    private SummaryData summaryData;
    private List<AccountListData> accountListData;
    private List<PhoneListData> phoneListData;
    private List<RecentEnquiryListData> recentEnquiryListData;
    private List<PhysicalAddressListData> physicalAddressListDataList;

    @XmlElement(name = "header")
    public HeaderData getHeader() {
        return header;
    }

    public void setHeader(HeaderData header) {
        this.header = header;
    }

    @XmlElement(name = "personalProfile")
    public PersonalProfileData getPersonalProfile() {
        return personalProfile;
    }

    public void setPersonalProfile(PersonalProfileData personalProfile) {
        this.personalProfile = personalProfile;
    }

    @XmlElement(name = "responseCode")
    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    @XmlElement(name = "scoreOutput")
    public ScoreOutputData getScoreOutput() {
        return scoreOutput;
    }

    public void setScoreOutput(ScoreOutputData scoreOutput) {
        this.scoreOutput = scoreOutput;
    }

    @XmlElement(name = "summary")
    public SummaryData getSummaryData() {
        return summaryData;
    }

    public void setSummaryData(SummaryData summaryData) {
        this.summaryData = summaryData;
    }
    @XmlElement(name = "accountList")
    public List<AccountListData> getAccountListData() {
        return accountListData;
    }

    public void setAccountListData(List<AccountListData> accountListData) {
        this.accountListData = accountListData;
    }
    @XmlElement(name = "phoneListData")
    public List<PhoneListData> getPhoneListData() {
        return phoneListData;
    }

    public void setPhoneListData(List<PhoneListData> phoneListData) {
        this.phoneListData = phoneListData;
    }
    @XmlElement(name = "recentEnquiryListData")
    public List<RecentEnquiryListData> getRecentEnquiryListData() {
        return recentEnquiryListData;
    }

    public void setRecentEnquiryListData(List<RecentEnquiryListData> recentEnquiryListData) {
        this.recentEnquiryListData = recentEnquiryListData;
    }
    @XmlElement(name = "physicalAddressListDataList")
    public List<PhysicalAddressListData> getPhysicalAddressListDataList() {
        return physicalAddressListDataList;
    }

    public void setPhysicalAddressListDataList(List<PhysicalAddressListData> physicalAddressListDataList) {
        this.physicalAddressListDataList = physicalAddressListDataList;
    }

    @Override
    public String toString() {
        return "TransUnionRwandaConsumerVerificationResponseData{" +
                "header=" + header +
                ", personalProfile=" + personalProfile +
                ", responseCode=" + responseCode +
                ", scoreOutput=" + scoreOutput +
                ", summaryData=" + summaryData +
                ", accountListData=" + accountListData +
                ", phoneListData=" + phoneListData +
                '}';
    }
}
