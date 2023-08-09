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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.LoanApprovalMatrixConstants;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanApprovalMatrix;
import org.apache.fineract.portfolio.loanaccount.domain.LoanApprovalMatrixRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecision;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecisionRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDecisionState;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDueDiligenceInfo;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDueDiligenceInfoRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCollateralManagementRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCollateralManagement;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleAssembler;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanDecisionTransitionApiJsonValidator;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanDecisionWritePlatformServiceJpaRepositoryImpl implements LoanApplicationDecisionWritePlatformService {

    private final PlatformSecurityContext context;
    private final LoanDecisionTransitionApiJsonValidator loanDecisionTransitionApiJsonValidator;
    private final LoanRepositoryWrapper loanRepositoryWrapper;
    private final LoanDecisionRepository loanDecisionRepository;
    private final ConfigurationReadPlatformService configurationReadPlatformService;
    private final ConfigurationDomainService configurationDomainService;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final LoanScheduleAssembler loanScheduleAssembler;
    private final LoanUtilService loanUtilService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanDecisionAssembler loanDecisionAssembler;
    private final LoanDueDiligenceInfoRepository loanDueDiligenceInfoRepository;
    private final NoteRepository noteRepository;
    private final LoanApprovalMatrixRepository loanApprovalMatrixRepository;
    private final LoanCollateralManagementRepository loanCollateralManagementRepository;

    @Override
    public CommandProcessingResult acceptLoanApplicationReview(final Long loanId, final JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();

        this.loanDecisionTransitionApiJsonValidator.validateApplicationReview(command.json());

        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        final LoanDecision loanDecision = this.loanDecisionRepository.findLoanDecisionByLoanId(loan.getId());

        validateReviewApplicationBusinessRule(command, loan, loanDecision);
        LoanDecision loanDecisionObj = loanDecisionAssembler.assembleFrom(command, loan, currentUser);
        LoanDecision savedObj = loanDecisionRepository.saveAndFlush(loanDecisionObj);

        Loan loanObj = loan;
        loanObj.setLoanDecisionState(LoanDecisionState.REVIEW_APPLICATION.getValue());
        this.loanRepositoryWrapper.saveAndFlush(loanObj);

        if (StringUtils.isNotBlank(loanDecisionObj.getReviewApplicationNote())) {
            final Note note = Note.loanNote(loanObj, "Review Application: " + loanDecisionObj.getReviewApplicationNote());
            this.noteRepository.save(note);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(savedObj.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .withResourceIdAsString(savedObj.getId().toString()).build();
    }

    private void validateReviewApplicationBusinessRule(JsonCommand command, Loan loan, LoanDecision loanDecision) {
        final GlobalConfigurationPropertyData extendLoanLifeCycleConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Add-More-Stages-To-A-Loan-Life-Cycle");
        final Boolean isExtendLoanLifeCycleConfig = extendLoanLifeCycleConfig.isEnabled();

        if (!isExtendLoanLifeCycleConfig) {
            throw new GeneralPlatformDomainRuleException("error.msg.Add-More-Stages-To-A-Loan-Life-Cycle.is.not.set",
                    "Add-More-Stages-To-A-Loan-Life-Cycle settings is not set. So this operation is not permitted");
        }

        if (loanDecision != null) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.account.should.not.exist.in.decision.engine",
                    "Loan Account found in decision engine. Operation [Review Application] is not allowed");
        }
        checkClientOrGroupActive(loan);

        validateLoanDisbursementDataWithMeetingDate(loan);
        validateLoanTopUp(loan);

        LocalDate loanReviewOnDate = command.localDateValueOfParameterNamed(LoanApiConstants.loanReviewOnDateParameterName);
        if (loanReviewOnDate.isBefore(loan.getSubmittedOnDate())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.review.application.date.should.be.after.submission.date",
                    "Loan Review Application date " + loanReviewOnDate + " should be after submission date " + loan.getSubmittedOnDate());
        }

        if (!loan.status().isSubmittedAndPendingApproval()) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.current.status.is.invalid",
                    "Loan Account current status is invalid. Expected" + loan.status().getCode() + " but found " + loan.status().getCode());
        }
    }

    @Override
    public CommandProcessingResult applyDueDiligence(Long loanId, JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();

        this.loanDecisionTransitionApiJsonValidator.validateDueDiligence(command.json());

        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        final LoanDecision loanDecision = this.loanDecisionRepository.findLoanDecisionByLoanId(loan.getId());

        validateDueDiligenceBusinessRule(command, loan, loanDecision);

        LoanDecision loanDecisionObj = loanDecisionAssembler.assembleDueDiligenceFrom(command, currentUser, loanDecision);
        LoanDecision savedObj = loanDecisionRepository.saveAndFlush(loanDecisionObj);

        Loan loanObj = loan;
        loanObj.setLoanDecisionState(LoanDecisionState.DUE_DILIGENCE.getValue());
        this.loanRepositoryWrapper.saveAndFlush(loanObj);

        LoanDueDiligenceInfo loanDueDiligenceInfo = loanDecisionAssembler.assembleDueDiligenceDetailsFrom(command, savedObj, loanObj);
        loanDueDiligenceInfoRepository.saveAndFlush(loanDueDiligenceInfo);

        if (StringUtils.isNotBlank(loanDecisionObj.getDueDiligenceNote())) {
            final Note note = Note.loanNote(loanObj, "Due Diligence : " + loanDecisionObj.getDueDiligenceNote());
            this.noteRepository.save(note);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(savedObj.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .withResourceIdAsString(savedObj.getId().toString()).build();
    }

    @Override
    public CommandProcessingResult acceptLoanCollateralReview(Long loanId, JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();

        this.loanDecisionTransitionApiJsonValidator.validateCollateralReview(command.json());

        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        final LoanDecision loanDecision = this.loanDecisionRepository.findLoanDecisionByLoanId(loan.getId());

        validateCollateralReviewBusinessRule(command, loan, loanDecision);

        LoanDecision loanDecisionObj = loanDecisionAssembler.assembleCollateralReviewFrom(command, currentUser, loanDecision);
        LoanDecision savedObj = loanDecisionRepository.saveAndFlush(loanDecisionObj);

        Loan loanObj = loan;
        loanObj.setLoanDecisionState(LoanDecisionState.COLLATERAL_REVIEW.getValue());
        this.loanRepositoryWrapper.saveAndFlush(loanObj);

        if (StringUtils.isNotBlank(loanDecisionObj.getDueDiligenceNote())) {
            final Note note = Note.loanNote(loanObj, "Collateral Review : " + loanDecisionObj.getDueDiligenceNote());
            this.noteRepository.save(note);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(savedObj.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .withResourceIdAsString(savedObj.getId().toString()).build();
    }

    @Override
    public CommandProcessingResult createLoanApprovalMatrix(JsonCommand command) {

        final GlobalConfigurationPropertyData extendLoanLifeCycleConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Add-More-Stages-To-A-Loan-Life-Cycle");
        final Boolean isExtendLoanLifeCycleConfig = extendLoanLifeCycleConfig.isEnabled();

        if (!isExtendLoanLifeCycleConfig) {
            throw new GeneralPlatformDomainRuleException("error.msg.Add-More-Stages-To-A-Loan-Life-Cycle.is.not.set",
                    "Add-More-Stages-To-A-Loan-Life-Cycle settings is not set. So this operation is not permitted");
        }

        this.loanDecisionTransitionApiJsonValidator.validateCreateApprovalMatrix(command.json());

        final String currency = command.stringValueOfParameterNamed(LoanApprovalMatrixConstants.currencyParameterName);
        LoanApprovalMatrix loanApprovalMatrix = this.loanApprovalMatrixRepository.findLoanApprovalMatrixByCurrency(currency);

        if (loanApprovalMatrix != null) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.approval.matrix.with.this.currency.already.exist.",
                    String.format("Loan Approval Matrix with Currency [ %s ] exist. Only One currency per Matrix is accepted", currency));
        }

        LoanApprovalMatrix loanApprovalMatrixFrom = loanDecisionAssembler.assembleLoanApprovalMatrixFrom(command);
        this.loanApprovalMatrixRepository.saveAndFlush(loanApprovalMatrixFrom);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanApprovalMatrixFrom.getId()) //
                .withResourceIdAsString(loanApprovalMatrixFrom.getId().toString()).build();

    }

    @Override
    public CommandProcessingResult deleteLoanApprovalMatrix(Long matrixId) {
        final GlobalConfigurationPropertyData extendLoanLifeCycleConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Add-More-Stages-To-A-Loan-Life-Cycle");
        final Boolean isExtendLoanLifeCycleConfig = extendLoanLifeCycleConfig.isEnabled();

        if (!isExtendLoanLifeCycleConfig) {
            throw new GeneralPlatformDomainRuleException("error.msg.Add-More-Stages-To-A-Loan-Life-Cycle.is.not.set",
                    "Add-More-Stages-To-A-Loan-Life-Cycle settings is not set. So this operation is not permitted");
        }

        LoanApprovalMatrix loanApprovalMatrix = this.loanApprovalMatrixRepository.findById(matrixId).orElseThrow();

        this.loanApprovalMatrixRepository.delete(loanApprovalMatrix);

        return new CommandProcessingResultBuilder() //
                .withEntityId(matrixId) //
                .withResourceIdAsString(matrixId.toString()).build();
    }

    @Override
    public CommandProcessingResult updateLoanApprovalMatrix(JsonCommand command, Long matrixId) {
        try {
            this.context.authenticatedUser();

            final GlobalConfigurationPropertyData extendLoanLifeCycleConfig = this.configurationReadPlatformService
                    .retrieveGlobalConfiguration("Add-More-Stages-To-A-Loan-Life-Cycle");
            final Boolean isExtendLoanLifeCycleConfig = extendLoanLifeCycleConfig.isEnabled();

            if (!isExtendLoanLifeCycleConfig) {
                throw new GeneralPlatformDomainRuleException("error.msg.Add-More-Stages-To-A-Loan-Life-Cycle.is.not.set",
                        "Add-More-Stages-To-A-Loan-Life-Cycle settings is not set. So this operation is not permitted");
            }
            this.loanDecisionTransitionApiJsonValidator.validateUpdateApprovalMatrix(command.json());

            LoanApprovalMatrix loanApprovalMatrix = this.loanApprovalMatrixRepository.findById(matrixId).orElseThrow();

            final String currency = command.stringValueOfParameterNamed(LoanApprovalMatrixConstants.currencyParameterName);

            if (!currency.equals(loanApprovalMatrix.getCurrency())) {
                LoanApprovalMatrix matrixCurrency = this.loanApprovalMatrixRepository.findLoanApprovalMatrixByCurrency(currency);

                if (matrixCurrency != null && !matrixCurrency.getId().equals(loanApprovalMatrix.getId())) {
                    throw new GeneralPlatformDomainRuleException("error.msg.loan.approval.matrix.with.this.currency.already.exist.", String
                            .format("Loan Approval Matrix with Currency [ %s ] exist. Only One currency per Matrix is accepted", currency));
                }
            }

            final Map<String, Object> changes = loanApprovalMatrix.update(command);

            if (!changes.isEmpty()) {
                this.loanApprovalMatrixRepository.saveAndFlush(loanApprovalMatrix);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withResourceIdAsString(loanApprovalMatrix.getId().toString()) //
                    .withEntityId(loanApprovalMatrix.getId()) //
                    .with(changes) //
                    .build();
        } catch (final JpaSystemException ex) {
            return CommandProcessingResult.empty();
        } catch (final PersistenceException dve) {
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult acceptIcReviewDecisionLevelOne(Long loanId, JsonCommand command) {
        final AppUser currentUser = getAppUserIfPresent();

        this.loanDecisionTransitionApiJsonValidator.validateIcReviewStage(command.json());

        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetection(loanId, true);
        final LoanDecision loanDecision = this.loanDecisionRepository.findLoanDecisionByLoanId(loan.getId());

        validateIcReviewDecisionLevelOneBusinessRule(command, loan, loanDecision);
        LoanApprovalMatrix matrixCurrency = this.loanApprovalMatrixRepository.findLoanApprovalMatrixByCurrency(loan.getCurrencyCode());

        if(matrixCurrency == null){
            throw new GeneralPlatformDomainRuleException("error.msg.loan.approval.matrix.with.this.currency.does.not.exist.", String
                    .format("Loan Approval Matrix with Currency [ %s ] doesn't exist. Approval matrix is expected to continue ", loan.getCurrencyCode()));
        }
        //Get Loan Matrix
        //Determine which cycle of this Loan Account
        //Determine the Next Level or stage to review
        //Add custom Params in Decision Table
        List<Loan> loanIndividualCounter;
        if(loan.isIndividualLoan()){
            //Validate Individual Loan Cycle . . .
            loanIndividualCounter = this.loanRepositoryWrapper.findLoanByClientId(loan.getClientId());
        }else{
            // Throw Not Support Loan Type
            throw new GeneralPlatformDomainRuleException("error.msg.invalid.loan.type.not.supported.for.Ic.Review", String
                    .format("This Loan Type [ %s ] , is not supported for IC Review Operations .", loan.getLoanType()));
        }

        Boolean isLoanFirstCycle = isLoanFirstCycle(loan, loanIndividualCounter);
        Boolean isLoanUnsecure = isLoanUnSecure(loan);

        //generate the next stage based on loan approval matrix via amounts to be disbursed
        if(isLoanFirstCycle  && isLoanUnsecure){
        //Loan is FirstCycle and Unsecure
        } else if (!isLoanFirstCycle && isLoanUnsecure) {
        // Loan is not first cycle (Second cycle or plus) and Unsecure
        } else if (isLoanFirstCycle && !isLoanUnsecure) {
        // First Cycle and secured Loan
        } else if (!isLoanFirstCycle && !isLoanUnsecure) {
        //Second Cycle or plus and secured
        }else{
            //throw not sure the Loan state
        }


        LoanDecision loanDecisionObj = loanDecisionAssembler.assembleIcReviewDecisionLevelOneFrom(command, currentUser, loanDecision);
        LoanDecision savedObj = loanDecisionRepository.saveAndFlush(loanDecisionObj);

        Loan loanObj = loan;
        loanObj.setLoanDecisionState(LoanDecisionState.IC_REVIEW_LEVEL_ONE.getValue());
        this.loanRepositoryWrapper.saveAndFlush(loanObj);

        if (StringUtils.isNotBlank(loanDecisionObj.getDueDiligenceNote())) {
            final Note note = Note.loanNote(loanObj, "IC Review-Decision Level One : " + loanDecisionObj.getIcReviewDecisionLevelOneNote());
            this.noteRepository.save(note);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(savedObj.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .withResourceIdAsString(savedObj.getId().toString()).build();
    }

    private Boolean isLoanFirstCycle(Loan loan, List<Loan> loanIndividualCounter) {
        if(CollectionUtils.isEmpty(loanIndividualCounter)){
            throw new GeneralPlatformDomainRuleException("error.msg.invalid.loan.counter.not.able.to.detect.this.client's.loan.account/s", String
                    .format("TThere is no Loan Account/s detected for client Id : [ %s ] , ", loan.getClientId()));
        } else   if (loanIndividualCounter.size() == 1) {
            // First Cycle
            return true;
        }else {
            //second and plus
           return false;
        }
    }


    private Boolean isLoanUnSecure(Loan loan) {
        List<LoanCollateralManagement> collateralManagementList = loanCollateralManagementRepository.findByLoan(loan);
        if(CollectionUtils.isEmpty(collateralManagementList)){
            return true;
        }
        return false;
    }

    private void validateDueDiligenceBusinessRule(JsonCommand command, Loan loan, LoanDecision loanDecision) {
        final GlobalConfigurationPropertyData extendLoanLifeCycleConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Add-More-Stages-To-A-Loan-Life-Cycle");
        final Boolean isExtendLoanLifeCycleConfig = extendLoanLifeCycleConfig.isEnabled();

        if (!isExtendLoanLifeCycleConfig) {
            throw new GeneralPlatformDomainRuleException("error.msg.Add-More-Stages-To-A-Loan-Life-Cycle.is.not.set",
                    "Add-More-Stages-To-A-Loan-Life-Cycle settings is not set. So this operation is not permitted");
        }

        if (loanDecision == null) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.account.should.not.found.in.decision.engine",
                    "Loan Account not found in decision engine. Operation [Due Diligence] is not allowed");
        }
        checkClientOrGroupActive(loan);

        validateLoanDisbursementDataWithMeetingDate(loan);
        validateLoanTopUp(loan);
        LocalDate dueDiligenceOn = command.localDateValueOfParameterNamed(LoanApiConstants.dueDiligenceOnDateParameterName);
        LocalDate startDate = command.localDateValueOfParameterNamed(LoanApiConstants.startDateParameterName);
        LocalDate endDate = command.localDateValueOfParameterNamed(LoanApiConstants.endDateParameterName);
        // Review Loan Application should not be before Due Diligence date
        if (dueDiligenceOn.isBefore(loanDecision.getReviewApplicationOn())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.due.diligence.date.should.be.after.review.application.date",
                    "Approve Due Diligence date" + dueDiligenceOn + " should be after Loan Review Application date "
                            + loanDecision.getReviewApplicationOn());
        }
        // Due Diligence date should not be before loan submission date
        if (dueDiligenceOn.isBefore(loan.getSubmittedOnDate())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.review.application.date.should.be.after.submission.date",
                    "Approve Due Diligence date " + dueDiligenceOn + " should be after Loan submission date " + loan.getSubmittedOnDate());
        }

        if (!loan.status().isSubmittedAndPendingApproval()) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.current.status.is.invalid",
                    "Loan Account current status is invalid. Expected" + loan.status().getCode() + " but found " + loan.status().getCode());
        }
        if (!LoanDecisionState.fromInt(loan.getLoanDecisionState()).isReviewApplication()) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.decision.state.is.invalid",
                    "Loan Account Decision state is invalid. Expected" + LoanDecisionState.REVIEW_APPLICATION.getValue() + " but found "
                            + loan.getLoanDecisionState());
        }
        if (!loan.getLoanDecisionState().equals(loanDecision.getLoanDecisionState())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.decision.state.does.not.reconcile",
                    "Loan Account Decision state Does not reconcile . Operation is terminated");
        }
        if (startDate.isAfter(endDate)) {
            throw new GeneralPlatformDomainRuleException(
                    "error.msg.loan.due.diligence.startDate.should.not.be.before.endDate.operation.terminated",
                    "Due Diligence startDate " + startDate + " should not be after endDate " + endDate);
        }
    }

    private void validateLoanTopUp(Loan loan) {
        if (loan.isTopup() && loan.getClientId() != null) {
            final Long loanIdToClose = loan.getTopupLoanDetails().getLoanIdToClose();
            final Loan loanToClose = this.loanRepositoryWrapper.findNonClosedLoanThatBelongsToClient(loanIdToClose, loan.getClientId());
            if (loanToClose == null) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.to.be.closed.with.topup.is.not.active",
                        "Loan to be closed with this topup is not active.");
            }

            final LocalDate lastUserTransactionOnLoanToClose = loanToClose.getLastUserTransactionDate();
            if (loan.getDisbursementDate().isBefore(lastUserTransactionOnLoanToClose)) {
                throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.disbursal.date.should.be.after.last.transaction.date.of.loan.to.be.closed",
                        "Disbursal date of this loan application " + loan.getDisbursementDate()
                                + " should be after last transaction date of loan to be closed " + lastUserTransactionOnLoanToClose);
            }
            BigDecimal loanOutstanding = this.loanReadPlatformService
                    .retrieveLoanForeclosureTemplate(loanIdToClose, loan.getDisbursementDate()).getAmount();
            final BigDecimal firstDisbursalAmount = loan.getFirstDisbursalAmount();
            if (loanOutstanding.compareTo(firstDisbursalAmount) > 0) {
                throw new GeneralPlatformDomainRuleException("error.msg.loan.amount.less.than.outstanding.of.loan.to.be.closed",
                        "Topup loan amount should be greater than outstanding amount of loan to be closed.");
            }
            BigDecimal netDisbursalAmount = loan.getApprovedPrincipal().subtract(loanOutstanding);
            loan.adjustNetDisbursalAmount(netDisbursalAmount);
        }
    }

    private void validateLoanDisbursementDataWithMeetingDate(Loan loan) {
        Boolean isSkipRepaymentOnFirstMonth = false;
        Integer numberOfDays = 0;
        // validate expected disbursement date against meeting date
        if (loan.isSyncDisbursementWithMeeting() && (loan.isGroupLoan() || loan.isJLGLoan())) {
            final CalendarInstance calendarInstance = this.calendarInstanceRepository.findCalendarInstaneByEntityId(loan.getId(),
                    CalendarEntityType.LOANS.getValue());
            Calendar calendar = null;
            if (calendarInstance != null) {
                calendar = calendarInstance.getCalendar();
            }
            // final Calendar calendar = calendarInstance.getCalendar();
            boolean isSkipRepaymentOnFirstMonthEnabled = this.configurationDomainService.isSkippingMeetingOnFirstDayOfMonthEnabled();
            if (isSkipRepaymentOnFirstMonthEnabled) {
                isSkipRepaymentOnFirstMonth = this.loanUtilService.isLoanRepaymentsSyncWithMeeting(loan.group(), calendar);
                if (isSkipRepaymentOnFirstMonth) {
                    numberOfDays = configurationDomainService.retreivePeroidInNumberOfDaysForSkipMeetingDate().intValue();
                }
            }
            this.loanScheduleAssembler.validateDisbursementDateWithMeetingDates(loan.getDisbursementDate(), calendar,
                    isSkipRepaymentOnFirstMonth, numberOfDays);
        }
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    private void checkClientOrGroupActive(final Loan loan) {
        final Client client = loan.client();
        if (client != null) {
            if (client.isNotActive()) {
                throw new ClientNotActiveException(client.getId());
            }
        }
        final Group group = loan.group();
        if (group != null) {
            if (group.isNotActive()) {
                throw new GroupNotActiveException(group.getId());
            }
        }
    }

    private void validateCollateralReviewBusinessRule(JsonCommand command, Loan loan, LoanDecision loanDecision) {
        final GlobalConfigurationPropertyData extendLoanLifeCycleConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Add-More-Stages-To-A-Loan-Life-Cycle");
        final Boolean isExtendLoanLifeCycleConfig = extendLoanLifeCycleConfig.isEnabled();

        if (!isExtendLoanLifeCycleConfig) {
            throw new GeneralPlatformDomainRuleException("error.msg.Add-More-Stages-To-A-Loan-Life-Cycle.is.not.set",
                    "Add-More-Stages-To-A-Loan-Life-Cycle settings is not set. So this operation is not permitted");
        }

        if (loanDecision == null) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.account.should.not.found.in.decision.engine",
                    "Loan Account not found in decision engine. Operation [Collateral Review] is not allowed");
        }
        checkClientOrGroupActive(loan);

        validateLoanDisbursementDataWithMeetingDate(loan);
        validateLoanTopUp(loan);
        LocalDate collateralReviewOn = command.localDateValueOfParameterNamed(LoanApiConstants.collateralReviewOnDateParameterName);
        // Collateral Review should not be before other stages below it like Due Diligence and Review Application
        if (collateralReviewOn.isBefore(loanDecision.getDueDiligenceOn())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.collateral.review.date.should.be.after.Due.Diligence.date",
                    "Approve Collateral Review date" + collateralReviewOn + " should be after Loan Due Diligence Approved date "
                            + loanDecision.getDueDiligenceOn());
        }
        if (collateralReviewOn.isBefore(loanDecision.getReviewApplicationOn())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.collateral.review.date.should.be.after.review.application.date",
                    "Approve Collateral Review date" + collateralReviewOn + " should be after Loan Review Application Approved date "
                            + loanDecision.getReviewApplicationOn());
        }
        // Collateral Review date should not be before loan submission date
        if (collateralReviewOn.isBefore(loan.getSubmittedOnDate())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.collateral.Review.date.should.be.after.submission.date",
                    "Approve Collateral Review date " + collateralReviewOn + " should be after Loan submission date "
                            + loan.getSubmittedOnDate());
        }

        if (!loan.status().isSubmittedAndPendingApproval()) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.current.status.is.invalid",
                    "Loan Account current status is invalid. Expected" + loan.status().getCode() + " but found " + loan.status().getCode());
        }
        if (!LoanDecisionState.fromInt(loan.getLoanDecisionState()).isDueDiligence()) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.decision.state.is.invalid",
                    "Loan Account Decision state is invalid. Expected" + LoanDecisionState.DUE_DILIGENCE.getValue() + " but found "
                            + loan.getLoanDecisionState());
        }
        if (!loan.getLoanDecisionState().equals(loanDecision.getLoanDecisionState())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.decision.state.does.not.reconcile",
                    "Loan Account Decision state Does not reconcile . Operation is terminated");
        }
    }

    private void validateIcReviewDecisionLevelOneBusinessRule(JsonCommand command, Loan loan, LoanDecision loanDecision) {
        final GlobalConfigurationPropertyData extendLoanLifeCycleConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Add-More-Stages-To-A-Loan-Life-Cycle");
        final Boolean isExtendLoanLifeCycleConfig = extendLoanLifeCycleConfig.isEnabled();

        if (!isExtendLoanLifeCycleConfig) {
            throw new GeneralPlatformDomainRuleException("error.msg.Add-More-Stages-To-A-Loan-Life-Cycle.is.not.set",
                    "Add-More-Stages-To-A-Loan-Life-Cycle settings is not set. So this operation is not permitted");
        }

        if (loanDecision == null) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.account.should.not.found.in.decision.engine",
                    "Loan Account not found in decision engine. Operation [IC Review Decision Level One] is not allowed");
        }
        checkClientOrGroupActive(loan);

        validateLoanDisbursementDataWithMeetingDate(loan);
        validateLoanTopUp(loan);
        LocalDate icReviewOn = command.localDateValueOfParameterNamed(LoanApiConstants.icReviewOnDateParameterName);
        // Ic Review Decision Level One should not be before other stages below it like Collateral Review , Due Diligence and Review Application
        if (icReviewOn.isBefore(loanDecision.getCollateralReviewOn())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.ic.review.decision.level.one.date.should.be.after.collateral.review.date",
                    "Approve IC ReviewDecision Level One on  date" + icReviewOn + " should be after Collateral Review Approved date "
                            + loanDecision.getCollateralReviewOn());
        }
        if (icReviewOn.isBefore(loanDecision.getDueDiligenceOn())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.ic.review.decision.level.one.date.should.be.after.Due.Diligence.date",
                    "Approve IC ReviewDecision Level One on date" + icReviewOn + " should be after Loan Due Diligence Approved date "
                            + loanDecision.getDueDiligenceOn());
        }
        if (icReviewOn.isBefore(loanDecision.getReviewApplicationOn())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.ic.review.decision.level.one.date.should.be.after.review.application.date",
                    "Approve IC ReviewDecision Level One on date" + icReviewOn + " should be after Loan Review Application Approved date "
                            + loanDecision.getReviewApplicationOn());
        }
        // Collateral Review date should not be before loan submission date
        if (icReviewOn.isBefore(loan.getSubmittedOnDate())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.ic.review.decision.level.one.date.should.be.after.submission.date",
                    "Approve IC ReviewDecision Level One on date " + icReviewOn + " should be after Loan submission date "
                            + loan.getSubmittedOnDate());
        }

        if (!loan.status().isSubmittedAndPendingApproval()) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.current.status.is.invalid",
                    "Loan Account current status is invalid. Expected" + loan.status().getCode() + " but found " + loan.status().getCode());
        }
        if (!LoanDecisionState.fromInt(loan.getLoanDecisionState()).isCollateralReview()) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.decision.state.is.invalid",
                    "Loan Account Decision state is invalid. Expected" + LoanDecisionState.COLLATERAL_REVIEW.getValue() + " but found "
                            + loan.getLoanDecisionState());
        }
        if (!loan.getLoanDecisionState().equals(loanDecision.getLoanDecisionState())) {
            throw new GeneralPlatformDomainRuleException("error.msg.loan.decision.state.does.not.reconcile",
                    "Loan Account Decision state Does not reconcile . Operation is terminated");
        }
    }

}
