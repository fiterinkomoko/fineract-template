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
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecision;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecisionState;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDueDiligenceInfo;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanDecisionAssembler {

    private final CodeValueRepositoryWrapper codeValueRepository;

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

    public LoanDueDiligenceInfo assembleDueDiligenceDetailsFrom(final JsonCommand command, LoanDecision savedLoanDecision, Loan loan) {

        CodeValue surveyLocation = null;
        CodeValue cohort = null;
        CodeValue program = null;
        CodeValue country = null;

        final String surveyName = command.stringValueOfParameterNamed(LoanApiConstants.surveyNameParameterName);
        LocalDate startDate = command.localDateValueOfParameterNamed(LoanApiConstants.startDateParameterName);
        LocalDate endDate = command.localDateValueOfParameterNamed(LoanApiConstants.endDateParameterName);

        final Long surveyLocationId = command.longValueOfParameterNamed(LoanApiConstants.surveyLocationParameterName);
        if (surveyLocationId != null) {
            surveyLocation = this.codeValueRepository
                    .findOneByCodeNameAndIdWithNotFoundDetection(LoanApiConstants.surveyLocationCodeParameterName, surveyLocationId);
        }

        final Long cohortId = command.longValueOfParameterNamed(LoanApiConstants.cohortParameterName);
        if (cohortId != null) {
            cohort = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(LoanApiConstants.cohortCodeParameterName,
                    cohortId);
        }

        final Long programId = command.longValueOfParameterNamed(LoanApiConstants.programParameterName);
        if (programId != null) {
            program = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(LoanApiConstants.programCodeParameterName,
                    programId);
        }

        final Long countryId = command.longValueOfParameterNamed(LoanApiConstants.countryParameterName);
        if (countryId != null) {
            country = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(LoanApiConstants.countryCodeParameterName,
                    countryId);
        }

        return LoanDueDiligenceInfo.createNew(loan, savedLoanDecision, surveyName, startDate, endDate, surveyLocation, cohort, program,
                country);
    }

    public LoanDecision assembleCollateralReviewFrom(final JsonCommand command, AppUser currentUser, LoanDecision savedLoanDecision) {

        LocalDate collateralReviewOn = command.localDateValueOfParameterNamed(LoanApiConstants.collateralReviewOnDateParameterName);

        final String noteText = command.stringValueOfParameterNamed("note");

        LoanDecision loanDecision = savedLoanDecision;
        loanDecision.setLoanDecisionState(LoanDecisionState.COLLATERAL_REVIEW.getValue());
        loanDecision.setCollateralReviewNote(noteText);
        loanDecision.setCollateralReviewBy(currentUser);
        loanDecision.setCollateralReviewOn(collateralReviewOn);
        loanDecision.setCollateralReviewSigned(Boolean.TRUE);
        loanDecision.setRejectCollateralReviewSigned(Boolean.FALSE);
        return loanDecision;
    }
}
