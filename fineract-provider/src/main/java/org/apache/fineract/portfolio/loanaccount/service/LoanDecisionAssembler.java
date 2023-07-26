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
package org.apache.fineract.portfolio.loanaccount.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecision;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecisionState;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanDecisionAssembler {

    public LoanDecision assembleFrom(final JsonCommand command, Loan loanId, AppUser currentUser) {

        LocalDate loanReviewOnDate = command.localDateValueOfParameterNamed(LoanApiConstants.loanReviewOnDateParameterName);
        final String noteText = command.stringValueOfParameterNamed("note");
        LoanDecision loanDecision = LoanDecision.reviewApplication(loanId, LoanDecisionState.REVIEW_APPLICATION.getValue(), noteText,
                Boolean.TRUE, Boolean.FALSE, loanReviewOnDate, currentUser);
        return loanDecision;
    }

    public LoanDecision assembleDueDiligenceFrom(final JsonCommand command, AppUser currentUser, LoanDecision savedLoanDecision) {

        LocalDate dueDiligenceOn = command.localDateValueOfParameterNamed(LoanApiConstants.dueDiligenceOnDateParameterName);

        final String noteText = command.stringValueOfParameterNamed("note");

        LoanDecision loanDecision = savedLoanDecision;
        loanDecision.setLoanDecisionState(LoanDecisionState.DUE_DILIGENCE.getValue());
        loanDecision.setDueDiligenceNote(noteText);
        loanDecision.setDueDiligenceBy(currentUser);
        loanDecision.setDueDiligenceOn(dueDiligenceOn);
        loanDecision.setDueDiligenceSigned(Boolean.TRUE);
        loanDecision.setRejectDueDiligence(Boolean.FALSE);
        return loanDecision;
    }
}
