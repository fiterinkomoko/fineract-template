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

@XmlRootElement(name = "personalProfile")
public class PersonalProfileData {

    private String crn;
    private String dateOfBirth;
    private String fullName;
    private String gender;
    private String healthInsuranceNo;
    private String maritalStatus;
    private String nationalID;
    private String otherNames;
    private String salutation;
    private String surname;

    @XmlElement(name = "crn")
    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    @XmlElement(name = "dateOfBirth")
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @XmlElement(name = "fullName")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @XmlElement(name = "gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @XmlElement(name = "healthInsuranceNo")
    public String getHealthInsuranceNo() {
        return healthInsuranceNo;
    }

    public void setHealthInsuranceNo(String healthInsuranceNo) {
        this.healthInsuranceNo = healthInsuranceNo;
    }

    @XmlElement(name = "maritalStatus")
    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @XmlElement(name = "nationalID")
    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    @XmlElement(name = "otherNames")
    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    @XmlElement(name = "salutation")
    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    @XmlElement(name = "surname")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return "PersonalProfileData{" + "crn='" + crn + '\'' + ", dateOfBirth='" + dateOfBirth + '\'' + ", fullName='" + fullName + '\''
                + ", gender='" + gender + '\'' + ", healthInsuranceNo='" + healthInsuranceNo + '\'' + ", maritalStatus='" + maritalStatus
                + '\'' + ", nationalID='" + nationalID + '\'' + ", otherNames='" + otherNames + '\'' + ", salutation='" + salutation + '\''
                + ", surname='" + surname + '\'' + '}';
    }
}
