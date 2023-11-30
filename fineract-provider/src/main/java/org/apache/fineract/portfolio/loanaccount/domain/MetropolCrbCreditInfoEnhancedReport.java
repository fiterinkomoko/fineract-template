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
@Table(name = "m_metropol_crb_credit_info_enhanced_report")
public class MetropolCrbCreditInfoEnhancedReport extends AbstractAuditableWithUTCDateTimeCustom {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client clientId;
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = true)
    private Loan loanId;
    @Column(name = "report_type")
    private String reportType;
    @Column(name = "api_code")
    private String apiCode;
    @Column(name = "api_code_description")
    private String apiCodeDescription;
    @Column(name = "application_ref_no")
    private String applicationRefNo;
    @Column(name = "credit_score")
    private String creditScore;
    @Column(name = "delinquency_code")
    private String delinquencyCode;
    @Column(name = "has_error")
    private Boolean hasError;
    @Column(name = "has_fraud")
    private Boolean hasFraud;
    @Column(name = "identity_number")
    private String identityNumber;
    @Column(name = "identity_type")
    private String identityType;
    @Column(name = "is_guarantor")
    private Boolean isGuarantor;
    @Column(name = "trx_id")
    private String trxId;

}
