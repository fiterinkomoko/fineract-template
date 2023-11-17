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

import lombok.Data;

@Data
public class CrbKenyaMetropolRequestData {

    private Integer report_type;
    private String identity_number;
    private String identity_type;
    private Integer id;
    private Integer clientId;
    private Integer loanId;
    private String citizenShip;
    private String clan;
    private String dateOfBirth;
    private String dateOfDeath;
    private String dateOfIssue;
    private String ethnicGroup;
    private String family;
    private String firstName;
    private String gender;
    private String identityNumber;
    private String identityType;
    private String lastName;
    private String occupation;
    private String otherName;
    private String placeOfBirth;
    private String placeOfDeath;
    private String placeOfLive;
    private String refOffice;
    private String serialNumber;
    private String trxId;
    private String createdOn;

    public CrbKenyaMetropolRequestData(Integer report_type, String identity_number, String identity_type) {
        this.report_type = report_type;
        this.identity_number = identity_number;
        this.identity_type = identity_type;
    }

    public CrbKenyaMetropolRequestData(Integer id, Integer clientId, Integer loanId, String citizenShip, String clan, String dateOfBirth,
            String dateOfDeath, String dateOfIssue, String ethnicGroup, String family, String firstName, String gender,
            String identityNumber, String identityType, String lastName, String occupation, String otherName, String placeOfBirth,
            String placeOfDeath, String placeOfLive, String refOffice, String serialNumber, String trxId, String createdOn) {
        this.id = id;
        this.clientId = clientId;
        this.loanId = loanId;
        this.citizenShip = citizenShip;
        this.clan = clan;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.dateOfIssue = dateOfIssue;
        this.ethnicGroup = ethnicGroup;
        this.family = family;
        this.firstName = firstName;
        this.gender = gender;
        this.identityNumber = identityNumber;
        this.identityType = identityType;
        this.lastName = lastName;
        this.occupation = occupation;
        this.otherName = otherName;
        this.placeOfBirth = placeOfBirth;
        this.placeOfDeath = placeOfDeath;
        this.placeOfLive = placeOfLive;
        this.refOffice = refOffice;
        this.serialNumber = serialNumber;
        this.trxId = trxId;
        this.createdOn = createdOn;
    }
}
