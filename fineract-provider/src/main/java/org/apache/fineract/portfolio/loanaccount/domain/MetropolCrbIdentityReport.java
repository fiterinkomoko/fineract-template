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
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableWithUTCDateTimeCustom;
import org.apache.fineract.portfolio.client.domain.Client;

@Data
@Entity
@Table(name = "m_metropol_crb_identity_report")
public class MetropolCrbIdentityReport extends AbstractAuditableWithUTCDateTimeCustom {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client clientId;
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = true)
    private Loan loanId;
    @Column(name = "citizenship")
    private String citizenship;
    @Column(name = "clan")
    private String clan;
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    @Column(name = "date_of_death")
    private String dateOfDeath;
    @Column(name = "date_of_issue")
    private String dateOfIssue;
    @Column(name = "ethnic_group")
    private String ethnicGroup;
    @Column(name = "family")
    private String family;
    @Column(name = "fingerprint")
    private String fingerprint;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "gender")
    private String gender;
    @Column(name = "identity_number")
    private String identityNumber;
    @Column(name = "identity_type")
    private String identityType;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "occupation")
    private String occupation;
    @Column(name = "other_name")
    private String otherName;
    @Column(name = "photo")
    private String photo;
    @Column(name = "place_of_birth")
    private String placeOfBirth;
    @Column(name = "place_of_death")
    private String placeOfDeath;
    @Column(name = "place_of_live")
    private String placeOfLive;
    @Column(name = "reg_office")
    private String regOffice;
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "signature")
    private String signature;
    @Column(name = "surname")
    private String surname;
    @Column(name = "trx_id")
    private String trxId;
}
