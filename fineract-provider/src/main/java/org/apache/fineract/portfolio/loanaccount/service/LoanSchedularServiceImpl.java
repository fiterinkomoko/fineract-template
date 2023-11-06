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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.domain.FineractContext;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.notification.service.ActiveMqNotificationDomainServiceImpl;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.exception.OfficeNotFoundException;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanMessageRepaymentReminderData;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueReminderSettingsData;
import org.apache.fineract.portfolio.loanaccount.data.LoanRepaymentReminderSettingsData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanOverdueReminder;
import org.apache.fineract.portfolio.loanaccount.domain.LoanOverdueReminderRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanOverdueReminderSettingsRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanReminderStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentReminder;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentReminderRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentReminderSettingsRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanOverdueReminderData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanRepaymentReminderData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanSchedularServiceImpl implements LoanSchedularService {

    private static final Logger LOG = LoggerFactory.getLogger(LoanSchedularServiceImpl.class);

    private static final SecureRandom RANDOM = new SecureRandom();

    private final int queueSize = 1;

    private final ConfigurationDomainService configurationDomainService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanWritePlatformService loanWritePlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final ApplicationContext applicationContext;
    private final LoanRepository loanRepository;
    private final LoanRepaymentReminderSettingsRepository loanRepaymentReminderSettingsRepository;
    private final LoanOverdueReminderSettingsRepository loanOverdueReminderSettingsRepository;
    private final LoanRepaymentReminderRepository loanRepaymentReminderRepository;
    private final LoanOverdueReminderRepository loanOverdueReminderRepository;
    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    @Autowired
    private ActiveMqNotificationDomainServiceImpl activeMqNotificationDomainService;
    @Autowired
    private Environment env;

    @Override
    @CronTarget(jobName = JobName.APPLY_CHARGE_TO_OVERDUE_LOAN_INSTALLMENT)
    public void applyChargeForOverdueLoans(Map<String, String> jobParameters) throws JobExecutionException {

        final Queue<List<Long>> queue = new ArrayDeque<>();
        final ApplicationContext applicationContext;
        final int threadPoolSize = Integer.parseInt(jobParameters.get("thread-pool-size"));
        final int batchSize = Integer.parseInt(jobParameters.get("batch-size"));
        final int pageSize = batchSize * threadPoolSize;
        Long maxLoanIdInList = 0L;
        final Long penaltyWaitPeriodValue = this.configurationDomainService.retrievePenaltyWaitPeriod();
        final Boolean backdatePenalties = this.configurationDomainService.isBackdatePenaltiesEnabled();
        final List<Long> overdueLoanIds = this.loanReadPlatformService
                .retrieveAllLoanIdsWithOverdueInstallments(penaltyWaitPeriodValue, backdatePenalties, maxLoanIdInList, pageSize);

        final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        if (overdueLoanIds != null && !overdueLoanIds.isEmpty()) {
            queue.add(overdueLoanIds.stream().toList());
            if (!CollectionUtils.isEmpty(queue)) {
                do {
                    int totalFilteredRecords = overdueLoanIds.size();
                    LOG.info("Starting Apply penalty to overdue loans- total records - {}", totalFilteredRecords);
                    List<Long> queueElement = queue.element();
                    maxLoanIdInList = queueElement.get(queueElement.size() - 1);
                    applyChargeForOverdueLoans(queue.remove(), queue, threadPoolSize, executorService, pageSize, maxLoanIdInList, penaltyWaitPeriodValue, backdatePenalties);
                } while (!CollectionUtils.isEmpty(queue));
            }
            // shutdown the executor when done
            executorService.shutdownNow();
        }
    }

    private void applyChargeForOverdueLoans(List<Long> overdueLoanIds, Queue<List<Long>> queue, int threadPoolSize, ExecutorService executorService, int pageSize, Long maxLoanIdInList, Long penaltyWaitPeriodValue, Boolean backdatePenalties) {
        List<Callable<Void>> posters = new ArrayList<>();
        int fromIndex = 0;
        int size = overdueLoanIds.size();
        int batchSize = (int) Math.ceil((double) size / threadPoolSize);
        if (batchSize == 0) {
            return;
        }
        int toIndex = (batchSize > size - 1) ? size : batchSize;
        while (toIndex < size && overdueLoanIds.get(toIndex - 1).equals(overdueLoanIds.get(toIndex))) {
            toIndex++;
        }
        boolean lastBatch = false;
        int loopCount = size / batchSize + 1;

        FineractContext context = ThreadLocalContextUtil.getContext();

        Callable<Void> fetchData = () -> {
            ThreadLocalContextUtil.init(context);
            Long maxId = maxLoanIdInList;
            if (!queue.isEmpty()) {
                maxId = Math.max(maxLoanIdInList, queue.element().get(queue.element().size() - 1));
            }
            while (queue.size() <= queueSize) {
                LOG.info("Fetching while threads are running!");
                List<Long> loanIds = this.loanReadPlatformService
                        .retrieveAllLoanIdsWithOverdueInstallments(penaltyWaitPeriodValue, backdatePenalties, maxLoanIdInList, pageSize);

                if (loanIds.isEmpty()) {
                    break;
                }
                maxId = loanIds.get(loanIds.size() - 1);
                queue.add(loanIds);
            }
            return null;
        };
        posters.add(fetchData);

        for (long i = 0; i < loopCount; i++) {
            List<Long> subList = safeSubList(overdueLoanIds, fromIndex, toIndex);
            ApplyChargeToOverdueLoansPoster poster = (ApplyChargeToOverdueLoansPoster) applicationContext
                    .getBean("applyChargeToOverdueLoansPoster");
            poster.setLoanIds(subList);
            poster.setLoanWritePlatformService(loanWritePlatformService);
            poster.setLoanReadPlatformService(loanReadPlatformService);
            poster.setConfigurationDomainService(configurationDomainService);
            poster.setContext(ThreadLocalContextUtil.getContext());

            posters.add(poster);
            if (lastBatch) {
                break;
            }
            if (toIndex + batchSize > size - 1) {
                lastBatch = true;
            }
            fromIndex = fromIndex + (toIndex - fromIndex);
            toIndex = (toIndex + batchSize > size - 1) ? size : toIndex + batchSize;
            while (toIndex < size && overdueLoanIds.get(toIndex - 1).equals(overdueLoanIds.get(toIndex))) {
                toIndex++;
            }
        }
        try {
            List<Future<Void>> responses = executorService.invokeAll(posters);
            Long maxId = maxLoanIdInList;
            if (!queue.isEmpty()) {
                maxId = Math.max(maxLoanIdInList, queue.element().get(queue.element().size() - 1));
            }
            while (queue.size() <= queueSize) {
                LOG.info("Fetching while threads are running!..:: this is not supposed to run........");
                overdueLoanIds = this.loanReadPlatformService
                        .retrieveAllLoanIdsWithOverdueInstallments(penaltyWaitPeriodValue, backdatePenalties, maxId, pageSize);

                if (overdueLoanIds.isEmpty()) {
                    break;
                }
                maxId = overdueLoanIds.get(overdueLoanIds.size() - 1);
                LOG.info("Add to the Queue");
                queue.add(overdueLoanIds);
            }
            checkTaskCompletion(responses);
            LOG.info("Queue size {}", queue.size());
        } catch (InterruptedException e1) {
            LOG.error("Interrupted while AddPenalty", e1);
        }
    }

    private void checkTaskCompletion(List<Future<Void>> responses) {
        try {
            for (Future<Void> f : responses) {
                f.get();
            }
            boolean allThreadsExecuted;
            int noOfThreadsExecuted = 0;
            for (Future<Void> future : responses) {
                if (future.isDone()) {
                    noOfThreadsExecuted++;
                }
            }
            allThreadsExecuted = noOfThreadsExecuted == responses.size();
            if (!allThreadsExecuted) {
                LOG.error("All threads could not execute.");
            }
        } catch (InterruptedException e1) {
            LOG.error("Interrupted while interest posting entries", e1);
        } catch (ExecutionException e2) {
            LOG.error("Execution exception while interest posting entries", e2);
        }
    }

    @Override
    @CronTarget(jobName = JobName.RECALCULATE_INTEREST_FOR_LOAN)
    @SuppressFBWarnings(value = {
            "DMI_RANDOM_USED_ONLY_ONCE" }, justification = "False positive for random object created and used only once")
    public void recalculateInterest() throws JobExecutionException {
        Integer maxNumberOfRetries = ThreadLocalContextUtil.getTenant().getConnection().getMaxRetriesOnDeadlock();
        Integer maxIntervalBetweenRetries = ThreadLocalContextUtil.getTenant().getConnection().getMaxIntervalBetweenRetries();
        Collection<Long> loanIds = this.loanReadPlatformService.fetchLoansForInterestRecalculation();
        int i = 0;
        if (!loanIds.isEmpty()) {
            List<Throwable> errors = new ArrayList<>();
            for (Long loanId : loanIds) {
                log.info("recalculateInterest: Loan ID = {}", loanId);
                Integer numberOfRetries = 0;
                while (numberOfRetries <= maxNumberOfRetries) {
                    try {
                        this.loanWritePlatformService.recalculateInterest(loanId);
                        numberOfRetries = maxNumberOfRetries + 1;
                    } catch (CannotAcquireLockException | ObjectOptimisticLockingFailureException exception) {
                        log.info("Recalulate interest job has been retried {} time(s)", numberOfRetries);
                        // Fail if the transaction has been retried for
                        // maxNumberOfRetries
                        if (numberOfRetries >= maxNumberOfRetries) {
                            log.error("Recalulate interest job has been retried for the max allowed attempts of {} and will be rolled back",
                                    numberOfRetries);
                            errors.add(exception);
                            break;
                        }
                        // Else sleep for a random time (between 1 to 10
                        // seconds) and continue
                        try {
                            int randomNum = RANDOM.nextInt(maxIntervalBetweenRetries + 1);
                            Thread.sleep(1000 + (randomNum * 1000));
                            numberOfRetries = numberOfRetries + 1;
                        } catch (InterruptedException e) {
                            log.error("Interest recalculation for loans retry failed due to InterruptedException", e);
                            errors.add(e);
                            break;
                        }
                    } catch (Exception e) {
                        log.error("Interest recalculation for loans failed for account {}", loanId, e);
                        numberOfRetries = maxNumberOfRetries + 1;
                        errors.add(e);
                    }
                    i++;
                }
                log.info("recalculateInterest: Loans count {}", i);
            }
            if (!errors.isEmpty()) {
                throw new JobExecutionException(errors);
            }
        }

    }

    @Override
    @CronTarget(jobName = JobName.RECALCULATE_INTEREST_FOR_LOAN)
    public void recalculateInterest(Map<String, String> jobParameters) {
        // gets the officeId
        final String officeId = jobParameters.get("officeId");
        log.info("recalculateInterest: officeId={}", officeId);
        Long officeIdLong = Long.valueOf(officeId);

        // gets the Office object
        final OfficeData office = this.officeReadPlatformService.retrieveOffice(officeIdLong);
        if (office == null) {
            throw new OfficeNotFoundException(officeIdLong);
        }
        final int threadPoolSize = Integer.parseInt(jobParameters.get("thread-pool-size"));
        final int batchSize = Integer.parseInt(jobParameters.get("batch-size"));

        recalculateInterest(office, threadPoolSize, batchSize);
    }

    private void recalculateInterest(OfficeData office, int threadPoolSize, int batchSize) {
        final int pageSize = batchSize * threadPoolSize;

        // initialise the executor service with fetched configurations
        final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        Long maxLoanIdInList = 0L;
        final String officeHierarchy = office.getHierarchy() + "%";

        // get the loanIds from service
        List<Long> loanIds = Collections.synchronizedList(
                this.loanReadPlatformService.fetchLoansForInterestRecalculation(pageSize, maxLoanIdInList, officeHierarchy));

        // gets the loanIds data set iteratively and call addAccuruals for that
        // paginated dataset
        do {
            int totalFilteredRecords = loanIds.size();
            log.info("Starting accrual - total filtered records - {}", totalFilteredRecords);
            recalculateInterest(loanIds, threadPoolSize, batchSize, executorService);
            maxLoanIdInList += pageSize + 1;
            loanIds = Collections.synchronizedList(
                    this.loanReadPlatformService.fetchLoansForInterestRecalculation(pageSize, maxLoanIdInList, officeHierarchy));
        } while (!CollectionUtils.isEmpty(loanIds));

        // shutdown the executor when done
        executorService.shutdownNow();
    }

    private void recalculateInterest(List<Long> loanIds, int threadPoolSize, int batchSize, final ExecutorService executorService) {

        List<Callable<Void>> posters = new ArrayList<>();
        int fromIndex = 0;
        // get the size of current paginated dataset
        int size = loanIds.size();
        // calculate the batch size
        double toGetCeilValue = size / threadPoolSize;
        batchSize = (int) Math.ceil(toGetCeilValue);

        if (batchSize == 0) {
            return;
        }

        int toIndex = (batchSize > size - 1) ? size : batchSize;
        while (toIndex < size && loanIds.get(toIndex - 1).equals(loanIds.get(toIndex))) {
            toIndex++;
        }
        boolean lastBatch = false;
        int loopCount = size / batchSize + 1;

        for (long i = 0; i < loopCount; i++) {
            List<Long> subList = safeSubList(loanIds, fromIndex, toIndex);
            RecalculateInterestPoster poster = (RecalculateInterestPoster) this.applicationContext.getBean("recalculateInterestPoster");
            poster.setLoanIds(subList);
            poster.setLoanWritePlatformService(loanWritePlatformService);
            posters.add(poster);
            if (lastBatch) {
                break;
            }
            if (toIndex + batchSize > size - 1) {
                lastBatch = true;
            }
            fromIndex = fromIndex + (toIndex - fromIndex);
            toIndex = (toIndex + batchSize > size - 1) ? size : toIndex + batchSize;
            while (toIndex < size && loanIds.get(toIndex - 1).equals(loanIds.get(toIndex))) {
                toIndex++;
            }
        }

        try {
            List<Future<Void>> responses = executorService.invokeAll(posters);
            checkCompletion(responses);
        } catch (InterruptedException e1) {
            log.error("Interrupted while recalculateInterest", e1);
        }
    }

    // break the lists into sub lists
    private <T> List<T> safeSubList(List<T> list, int fromIndex, int toIndex) {
        int size = list.size();
        if (fromIndex >= size || toIndex <= 0 || fromIndex >= toIndex) {
            return Collections.emptyList();
        }

        fromIndex = Math.max(0, fromIndex);
        toIndex = Math.min(size, toIndex);

        return list.subList(fromIndex, toIndex);
    }

    // checks the execution of task by each thread in the executor service
    private void checkCompletion(List<Future<Void>> responses) {
        try {
            for (Future<Void> f : responses) {
                f.get();
            }
            boolean allThreadsExecuted = false;
            int noOfThreadsExecuted = 0;
            for (Future<Void> future : responses) {
                if (future.isDone()) {
                    noOfThreadsExecuted++;
                }
            }
            allThreadsExecuted = noOfThreadsExecuted == responses.size();
            if (!allThreadsExecuted) {
                log.error("All threads could not execute.");
            }
        } catch (InterruptedException e1) {
            log.error("Interrupted while posting IR entries", e1);
        } catch (ExecutionException e2) {
            log.error("Execution exception while posting IR entries", e2);
        }
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.PROCESS_LOAN_REPAYMENT_REMINDER)
    public void processLoanRepaymentReminder() {
        final AppUser currentUser = getAppUserIfPresent();
        String batchId = java.util.UUID.randomUUID().toString();

        final List<LoanRepaymentReminderSettingsData> settingsData = loanRepaymentReminderSettingsRepository
                .findLoanRepaymentReminderSettings();

        if (!CollectionUtils.isEmpty(settingsData)) {
            for (LoanRepaymentReminderSettingsData data : settingsData) {

                final List<LoanRepaymentReminderData> repaymentReminders = loanReadPlatformService
                        .findLoanRepaymentReminderData(data.getDays());

                if (!CollectionUtils.isEmpty(repaymentReminders)) {
                    for (LoanRepaymentReminderData repaymentReminderData : repaymentReminders) {

                        LoanRepaymentReminder loanRepaymentReminder = new LoanRepaymentReminder(data.getId(), repaymentReminderData,
                                LoanReminderStatus.PENDING.name(), batchId);
                        loanRepaymentReminder.setCreatedDate(DateUtils.getLocalDateTimeOfTenant());
                        loanRepaymentReminder.setLastModifiedDate(DateUtils.getLocalDateTimeOfTenant());
                        loanRepaymentReminder.setCreatedBy(currentUser.getId());

                        loanRepaymentReminderRepository.save(loanRepaymentReminder);
                    }
                    loanRepaymentReminderSettingsRepository.updateLoanRepaymentReminderSettingsBatchId(batchId, data.getId());
                }
            }

        } else {
            LOG.info("Proccess Loan Repayment Reminders not found");
        }

    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.POST_LOAN_REPAYMENT_REMINDER)
    public void postLoanRepaymentReminder() {
        final AppUser currentUser = getAppUserIfPresent();
        Long officeId = currentUser.getOffice() == null ? null : currentUser.getOffice().getId();
        final List<LoanRepaymentReminderSettingsData> settingsData = loanRepaymentReminderSettingsRepository
                .findLoanRepaymentReminderSettings();

        if (!CollectionUtils.isEmpty(settingsData)) {
            for (LoanRepaymentReminderSettingsData settings : settingsData) {
                final List<LoanRepaymentReminder> reminders = loanRepaymentReminderRepository
                        .getLoanRepaymentReminderByBatchId(settings.getBatch());

                if (!CollectionUtils.isEmpty(reminders)) {
                    for (LoanRepaymentReminder data : reminders) {
                        LoanMessageRepaymentReminderData repaymentReminderData = new LoanMessageRepaymentReminderData(data);
                        activeMqNotificationDomainService.buildNotification("ALL_FUNCTION", "LoanRepaymentReminder", data.getId(),
                                this.fromApiJsonHelper.toJson(repaymentReminderData), "PENDING", context.authenticatedUser().getId(),
                                officeId, this.env.getProperty("fineract.activemq.loanRepaymentReminderQueue"));
                    }

                } else {
                    LOG.info("Post Loan Repayment Reminders not found");
                }
            }
        }
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.PROCESS_LOAN_OVERDUE_REMINDER)
    public void processLoanOverdueReminder() {
        final AppUser currentUser = getAppUserIfPresent();
        String batchId = java.util.UUID.randomUUID().toString();

        final List<LoanOverdueReminderSettingsData> settingsData = loanOverdueReminderSettingsRepository.findLoanOverdueReminderSettings();

        if (!CollectionUtils.isEmpty(settingsData)) {
            for (LoanOverdueReminderSettingsData data : settingsData) {

                final List<LoanOverdueReminderData> overdueReminderData = loanReadPlatformService
                        .findLoanOverdueReminderData(data.getDays());

                if (!CollectionUtils.isEmpty(overdueReminderData)) {
                    for (LoanOverdueReminderData loanOverdueReminderData : overdueReminderData) {

                        LoanOverdueReminder loanOverdueReminder = new LoanOverdueReminder(data.getId(), loanOverdueReminderData,
                                LoanReminderStatus.PENDING.name(), batchId);
                        loanOverdueReminder.setCreatedDate(DateUtils.getLocalDateTimeOfTenant());
                        loanOverdueReminder.setLastModifiedDate(DateUtils.getLocalDateTimeOfTenant());
                        loanOverdueReminder.setCreatedBy(currentUser.getId());

                        loanOverdueReminderRepository.save(loanOverdueReminder);
                    }
                    loanOverdueReminderSettingsRepository.updateLoanOverdueReminderSettingsBatchId(batchId, data.getId());
                }
            }

        } else {
            LOG.info("Proccess Loan Overdue Reminders not found");
        }

    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.POST_LOAN_OVERDUE_REMINDER)
    public void postLoanOverdueReminder() {
        final AppUser currentUser = getAppUserIfPresent();
        Long officeId = currentUser.getOffice() == null ? null : currentUser.getOffice().getId();
        final List<LoanOverdueReminderSettingsData> settingsData = loanOverdueReminderSettingsRepository.findLoanOverdueReminderSettings();

        if (!CollectionUtils.isEmpty(settingsData)) {
            for (LoanOverdueReminderSettingsData settings : settingsData) {
                final List<LoanOverdueReminder> reminders = loanOverdueReminderRepository
                        .getLoanOverdueReminderByBatchId(settings.getBatch());

                if (!CollectionUtils.isEmpty(reminders)) {
                    for (LoanOverdueReminder data : reminders) {
                        LoanMessageRepaymentReminderData repaymentReminderData = new LoanMessageRepaymentReminderData(data);
                        activeMqNotificationDomainService.buildNotification("ALL_FUNCTION", "LoanOverdueReminder", data.getId(),
                                this.fromApiJsonHelper.toJson(repaymentReminderData), "PENDING", context.authenticatedUser().getId(),
                                officeId, this.env.getProperty("fineract.activemq.loanOverdueConfirmationQueue"));
                    }

                } else {
                    LOG.info("Post Loan Overdue Reminders not found");
                }
            }
        }
        LOG.info("Post Loan Overdue Reminders not found");
    }

}
