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

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "m_loan_decision")
public class LoanDecision extends AbstractAuditableCustom {

    @OneToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;
    @Column(name = "loan_decision_state")
    private Integer loanDecisionState;
    @Column(name = "review_application_note")
    private String reviewApplicationNote;
    @Column(name = "is_review_application_signed")
    private Boolean reviewApplicationSigned;
    @Column(name = "is_reject_review_application")
    private Boolean rejectReviewApplicationSigned;
    @Column(name = "review_application_on")
    private LocalDate reviewApplicationOn;
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "review_application_by")
    private AppUser reviewApplicationBy;

    public LoanDecision() {}

    public static LoanDecision reviewApplication(Loan loan, Integer loanDecisionState, String reviewApplicationNote,
            Boolean reviewApplicationSigned, Boolean rejectReviewApplicationSigned, LocalDate reviewApplicationOn,
            AppUser reviewApplicationBy) {
        return new LoanDecision(loan, loanDecisionState, reviewApplicationNote, reviewApplicationSigned, rejectReviewApplicationSigned,
                reviewApplicationOn, reviewApplicationBy);
    }

    public LoanDecision(Loan loan, Integer loanDecisionState, String reviewApplicationNote, Boolean reviewApplicationSigned,
            Boolean rejectReviewApplicationSigned, LocalDate reviewApplicationOn, AppUser reviewApplicationBy) {
        this.loan = loan;
        this.loanDecisionState = loanDecisionState;
        this.reviewApplicationNote = reviewApplicationNote;
        this.reviewApplicationSigned = reviewApplicationSigned;
        this.rejectReviewApplicationSigned = rejectReviewApplicationSigned;
        this.reviewApplicationOn = reviewApplicationOn;
        this.reviewApplicationBy = reviewApplicationBy;
    }

    public Loan getLoan() {
        return loan;
    }

    public Integer getLoanDecisionState() {
        return loanDecisionState;
    }

    public String getReviewApplicationNote() {
        return reviewApplicationNote;
    }

    public Boolean getReviewApplicationSigned() {
        return reviewApplicationSigned;
    }

    public Boolean getRejectReviewApplicationSigned() {
        return rejectReviewApplicationSigned;
    }

    public LocalDate getReviewApplicationOn() {
        return reviewApplicationOn;
    }

    public AppUser getReviewApplicationBy() {
        return reviewApplicationBy;
    }
}
