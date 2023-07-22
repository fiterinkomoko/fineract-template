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

import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecision;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecisionRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanDecisionStateUtilService {

    private final ConfigurationReadPlatformService configurationReadPlatformService;
    private final LoanDecisionRepository loanDecisionRepository;

    public void validateLoanReviewApplicationStateIsFiredBeforeApproval(Loan loan) {
        final Boolean isExtendLoanLifeCycleConfig = isExtendLoanLifeCycleConfig();

        final LoanDecision loanDecision = this.loanDecisionRepository.findLoanDecisionByLoanId(loan.getId());
        if (isExtendLoanLifeCycleConfig && loanDecision == null) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.account.should.extend.decision.engine.to.review.Loan.application",
                    "Loan Account is not permitted for Approval since new workflow [Add-More-Stages-To-A-Loan-Life-Cycle] is activated and next status is [Review Application]");
        }
        if (isExtendLoanLifeCycleConfig && loanDecision != null && !loanDecision.getReviewApplicationSigned()) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.account.should.extend.decision.engine.required.state.is.review.application",
                    "Loan Account is not permitted for Approval . [Review Application ] to proceed ");
        }
    }

    public void validateLoanReviewApplicationStateIsFiredBeforeDisbursal(Loan loan) {
        final Boolean isExtendLoanLifeCycleConfig = isExtendLoanLifeCycleConfig();

        final LoanDecision loanDecision = this.loanDecisionRepository.findLoanDecisionByLoanId(loan.getId());
        if (isExtendLoanLifeCycleConfig && loanDecision == null) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.account.should.extend.decision.engine.to.review.Loan.application",
                    "Loan Account is not permitted for Disbursement since new workflow [Add-More-Stages-To-A-Loan-Life-Cycle] is activated and next stage is [Review Application]");
        }
        if (isExtendLoanLifeCycleConfig && loanDecision != null && !loanDecision.getReviewApplicationSigned()) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.account.should.extend.decision.engine.required.state.is.review.application",
                    "Loan Account is not permitted for Disbursement . [Review Application ] to proceed ");
        }
    }

    @NotNull
    private Boolean isExtendLoanLifeCycleConfig() {
        final GlobalConfigurationPropertyData extendLoanLifeCycleConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Add-More-Stages-To-A-Loan-Life-Cycle");
        final Boolean isExtendLoanLifeCycleConfig = extendLoanLifeCycleConfig.isEnabled();
        return isExtendLoanLifeCycleConfig;
    }

}
