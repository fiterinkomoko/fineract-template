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

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.loanaccount.data.KivaLoanData;

@Data
@Entity
@Table(name = "m_kiva_loan_awaiting_approval")
public class KivaLoanAwaitingApproval extends AbstractPersistableCustom {

    private static final long serialVersionUID = 9181640245194392646L;

    @Column(name = "borrower_count")
    private Integer borrowerCount;
    @Column(name = "internal_loan_id")
    private String internalLoanId;
    @Column(name = "internal_client_id")
    private String internalClientId;
    @Column(name = "partner_id")
    private String partnerId;
    @Column(name = "partner")
    private String partner;
    @Column(name = "kiva_id")
    private String kivaId;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "name")
    private String name;
    @Column(name = "location")
    private String location;
    @Column(name = "status")
    private String status;
    @Column(name = "loan_price")
    private String loanPrice;
    @Column(name = "loan_local_price")
    private String loanLocalPrice;
    @Column(name = "loan_currency")
    private String loanCurrency;
    @Column(name = "create_time")
    private Long createTime;
    @Column(name = "ended_time")
    private Long endedTime;
    @Column(name = "refunded_time")
    private Long refundedTime;
    @Column(name = "expired_time")
    private Long expiredTime;
    @Column(name = "defaulted_time")
    private Long defaultedTime;
    @Column(name = "planned_expiration_time")
    private Long plannedExpirationTime;
    @Column(name = "planned_inactive_expire_time")
    private Long plannedInactiveExpireTime;
    @Column(name = "delinquent")
    private Boolean delinquent;
    @Column(name = "issue_feedback_time")
    private Long issueFeedbackTime;
    @Column(name = "issue_reported_by")
    private Long issueReportedBy;
    @Column(name = "flexible_fundraising_enabled")
    private String flexibleFundraisingEnabled;
    @Column(name = "funded_amount")
    private BigDecimal fundedAmount;

    public KivaLoanAwaitingApproval() {}

    public KivaLoanAwaitingApproval(KivaLoanData kivaLoanData) {
        this.borrowerCount = kivaLoanData.getBorrower_count();
        this.internalLoanId = kivaLoanData.getInternal_loan_id();
        this.internalClientId = kivaLoanData.getInternal_client_id();
        this.partnerId = kivaLoanData.getPartner_id();
        this.partner = kivaLoanData.getPartner();
        this.kivaId = kivaLoanData.getKiva_id();
        this.uuid = kivaLoanData.getUuid();
        this.name = kivaLoanData.getName();
        this.location = kivaLoanData.getLocation();
        this.status = kivaLoanData.getStatus();
        this.loanPrice = kivaLoanData.getLoan_price();
        this.loanLocalPrice = kivaLoanData.getLoan_local_price();
        this.loanCurrency = kivaLoanData.getLoan_currency();
        this.createTime = kivaLoanData.getCreate_time();
        this.endedTime = kivaLoanData.getEnded_time();
        this.refundedTime = kivaLoanData.getRefunded_time();
        this.expiredTime = kivaLoanData.getExpired_time();
        this.defaultedTime = kivaLoanData.getDefaulted_time();
        this.plannedExpirationTime = kivaLoanData.getPlanned_expiration_time();
        this.plannedInactiveExpireTime = kivaLoanData.getPlanned_inactive_expire_time();
        this.delinquent = kivaLoanData.isDelinquent();
        this.issueFeedbackTime = kivaLoanData.getIssue_feedback_time();
        this.issueReportedBy = kivaLoanData.getIssue_reported_by();
        this.flexibleFundraisingEnabled = kivaLoanData.getFlexible_fundraising_enabled();
        this.fundedAmount = kivaLoanData.getFundedAmount();
    }
}
