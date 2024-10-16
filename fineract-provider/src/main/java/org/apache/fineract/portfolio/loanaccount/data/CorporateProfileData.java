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

@XmlRootElement(name = "corporateProfile")
public class CorporateProfileData {

    private String crn;
    private String companyName;
    private String companyRegNo;
    private String companyRegDate;

    @XmlElement(name = "crn")
    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    @XmlElement(name = "companyName")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @XmlElement(name = "companyRegNo")
    public String getCompanyRegNo() {
        return companyRegNo;
    }

    public void setCompanyRegNo(String companyRegNo) {
        this.companyRegNo = companyRegNo;
    }

    @XmlElement(name = "companyRegDate")
    public String getCompanyRegDate() {
        return companyRegDate;
    }

    public void setCompanyRegDate(String companyRegDate) {
        this.companyRegDate = companyRegDate;
    }

    @Override
    public String toString() {
        return "CorporateProfileData{" + "crn='" + crn + '\'' + ", companyName='" + companyName + '\'' + ", companyRegNo='" + companyRegNo
                + '\'' + '}';
    }
}
