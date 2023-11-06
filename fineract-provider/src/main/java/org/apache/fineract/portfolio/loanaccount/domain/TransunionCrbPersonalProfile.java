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
package org.apache.fineract.portfolio.loanaccount.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.loanaccount.data.PersonalProfileData;

@Data
@Entity
@Table(name = "m_transunion_crb_personal_profile")
public class TransunionCrbPersonalProfile extends AbstractPersistableCustom {

    private static final long serialVersionUID = 9181640245194392646L;
    @ManyToOne
    @JoinColumn(name = "header_id", nullable = true)
    private TransunionCrbHeader headerId;
    @Column(name = "crn")
    private String crn;
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "gender")
    private String gender;
    @Column(name = "health_insurance_no")
    private String healthInsuranceNo;
    @Column(name = "marital_status")
    private String maritalStatus;
    @Column(name = "national_id")
    private String nationalID;
    @Column(name = "other_names")
    private String otherNames;
    @Column(name = "salutation")
    private String salutation;
    @Column(name = "surname")
    private String surname;

    public TransunionCrbPersonalProfile() {}

    public TransunionCrbPersonalProfile(TransunionCrbHeader headerId, PersonalProfileData personalProfileData) {
        this.headerId = headerId;
        this.crn = personalProfileData.getCrn();
        this.dateOfBirth = personalProfileData.getDateOfBirth();
        this.fullName = personalProfileData.getFullName();
        this.gender = personalProfileData.getGender();
        this.healthInsuranceNo = personalProfileData.getHealthInsuranceNo();
        this.maritalStatus = personalProfileData.getMaritalStatus();
        this.nationalID = personalProfileData.getNationalID();
        this.otherNames = personalProfileData.getOtherNames();
        this.salutation = personalProfileData.getSalutation();
        this.surname = personalProfileData.getSurname();
    }
}
