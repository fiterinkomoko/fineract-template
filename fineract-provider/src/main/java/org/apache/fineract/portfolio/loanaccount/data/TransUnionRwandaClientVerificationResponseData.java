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

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "getProduct123Response", namespace = "http://ws.rw.crbws.transunion.ke.co/")
public class TransUnionRwandaClientVerificationResponseData {

    private List<CollateralData> collateralList;
    private List<EmploymentData> employmentList;
    private HeaderData header;
    private PersonalProfileData personalProfile;
    private List<RecentEnquiryData> recentEnquiryList;
    private String responseCode;
    private ScoreOutputData scoreOutput;

    @XmlElement(name = "collateralList")
    public List<CollateralData> getCollateralList() {
        return collateralList;
    }

    public void setCollateralList(List<CollateralData> collateralList) {
        this.collateralList = collateralList;
    }

    @XmlElement(name = "employmentList")
    public List<EmploymentData> getEmploymentList() {
        return employmentList;
    }

    public void setEmploymentList(List<EmploymentData> employmentList) {
        this.employmentList = employmentList;
    }

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

    @XmlElement(name = "recentEnquiryList")
    public List<RecentEnquiryData> getRecentEnquiryList() {
        return recentEnquiryList;
    }

    public void setRecentEnquiryList(List<RecentEnquiryData> recentEnquiryList) {
        this.recentEnquiryList = recentEnquiryList;
    }

    @XmlElement(name = "responseCode")
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @XmlElement(name = "scoreOutput")
    public ScoreOutputData getScoreOutput() {
        return scoreOutput;
    }

    public void setScoreOutput(ScoreOutputData scoreOutput) {
        this.scoreOutput = scoreOutput;
    }

}
