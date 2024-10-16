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
package org.apache.fineract.accounting.provisioning.service;

import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepository;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.provisioning.data.LoanProductProvisioningEntryData;
import org.apache.fineract.accounting.provisioning.data.ProvisioningEntryData;
import org.apache.fineract.accounting.provisioning.domain.LoanProductProvisioningEntry;
import org.apache.fineract.accounting.provisioning.domain.ProvisioningEntry;
import org.apache.fineract.accounting.provisioning.domain.ProvisioningEntryRepository;
import org.apache.fineract.accounting.provisioning.exception.NoProvisioningCriteriaDefinitionFound;
import org.apache.fineract.accounting.provisioning.exception.ProvisioningEntryAlreadyCreatedException;
import org.apache.fineract.accounting.provisioning.exception.ProvisioningEntryNotfoundException;
import org.apache.fineract.accounting.provisioning.exception.ProvisioningJournalEntriesCannotbeCreatedException;
import org.apache.fineract.accounting.provisioning.serialization.ProvisioningEntriesDefinitionJsonDeserializer;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.domain.FineractContext;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.hooks.event.HookEvent;
import org.apache.fineract.infrastructure.hooks.event.HookEventSource;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.organisation.provisioning.data.ProvisioningCriteriaData;
import org.apache.fineract.organisation.provisioning.domain.ProvisioningCategory;
import org.apache.fineract.organisation.provisioning.domain.ProvisioningCategoryRepository;
import org.apache.fineract.organisation.provisioning.service.ProvisioningCriteriaReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvisioningEntriesWritePlatformServiceJpaRepositoryImpl implements ProvisioningEntriesWritePlatformService {

    private final ProvisioningEntriesReadPlatformService provisioningEntriesReadPlatformService;
    private final ProvisioningCriteriaReadPlatformService provisioningCriteriaReadPlatformService;
    private final LoanProductRepository loanProductRepository;
    private final GLAccountRepository glAccountRepository;
    private final OfficeRepositoryWrapper officeRepositoryWrapper;
    private final ProvisioningCategoryRepository provisioningCategoryRepository;
    private final PlatformSecurityContext platformSecurityContext;
    private final ProvisioningEntryRepository provisioningEntryRepository;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final ProvisioningEntriesDefinitionJsonDeserializer fromApiJsonDeserializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final ApplicationEventPublisher eventPublisher;
    private final LoanRepository loanRepository;

    @Override
    public CommandProcessingResult createProvisioningJournalEntries(Long provisioningEntryId, JsonCommand command) {
        ProvisioningEntry requestedEntry = this.provisioningEntryRepository.findById(provisioningEntryId)
                .orElseThrow(() -> new ProvisioningEntryNotfoundException(provisioningEntryId));

        ProvisioningEntryData exisProvisioningEntryData = this.provisioningEntriesReadPlatformService
                .retrieveExistingProvisioningIdDateWithJournals();
        revertAndAddJournalEntries(exisProvisioningEntryData, requestedEntry);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(requestedEntry.getId()).build();
    }

    private void revertAndAddJournalEntries(ProvisioningEntryData existingEntryData, ProvisioningEntry requestedEntry) {
        if (existingEntryData != null) {
            validateForCreateJournalEntry(existingEntryData, requestedEntry);
            this.journalEntryWritePlatformService.revertProvisioningJournalEntries(requestedEntry.getCreatedDate(),
                    existingEntryData.getId(), PortfolioProductType.PROVISIONING.getValue());
        }
        if (requestedEntry.getLoanProductProvisioningEntries() == null || requestedEntry.getLoanProductProvisioningEntries().size() == 0) {
            requestedEntry.setJournalEntryCreated(Boolean.FALSE);
        } else {
            requestedEntry.setJournalEntryCreated(Boolean.TRUE);
        }

        this.provisioningEntryRepository.saveAndFlush(requestedEntry);
        this.journalEntryWritePlatformService.createProvisioningJournalEntries(requestedEntry);
    }

    private void validateForCreateJournalEntry(ProvisioningEntryData existingEntry, ProvisioningEntry requested) {
        LocalDate existingDate = existingEntry.getCreatedDate();
        LocalDate requestedDate = requested.getCreatedDate();
        if (existingDate.isAfter(requestedDate) || existingDate.compareTo(requestedDate) == 0 ? Boolean.TRUE : Boolean.FALSE) {
            throw new ProvisioningJournalEntriesCannotbeCreatedException(existingEntry.getCreatedDate(), requestedDate);
        }
    }

    private boolean isJournalEntriesRequired(JsonCommand command) {
        boolean bool = false;
        if (this.fromApiJsonHelper.parameterExists("createjournalentries", command.parsedJson())) {
            JsonObject jsonObject = command.parsedJson().getAsJsonObject();
            bool = jsonObject.get("createjournalentries").getAsBoolean();
        }
        return bool;
    }

    private LocalDate parseDate(JsonCommand command) {
        return this.fromApiJsonHelper.extractLocalDateNamed("date", command.parsedJson());
    }

    @Override
    @CronTarget(jobName = JobName.GENERATE_LOANLOSS_PROVISIONING)
    public void generateLoanLossProvisioningAmount() {
        LocalDate currentDate = DateUtils.getBusinessLocalDate();
        boolean addJournalEntries = true;
        try {
            Collection<ProvisioningCriteriaData> criteriaCollection = this.provisioningCriteriaReadPlatformService
                    .retrieveAllProvisioningCriterias();
            if (criteriaCollection == null || criteriaCollection.size() == 0) {
                return;
                // FIXME: Do we need to throw
                // NoProvisioningCriteriaDefinitionFound()?
            }
            ProvisioningEntry requestedEntry = createProvsioningEntry(currentDate, addJournalEntries);
            postWebHook(requestedEntry);
        } catch (ProvisioningEntryAlreadyCreatedException peace) {
            log.error("Provisioning Entry already created", peace);
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            log.error("Problem occurred in generateLoanLossProvisioningAmount function", dve);
        }
    }

    @Override
    public CommandProcessingResult createProvisioningEntries(JsonCommand command) {
        this.fromApiJsonDeserializer.validateForCreate(command.json());
        LocalDate createdDate = parseDate(command);
        boolean addJournalEntries = isJournalEntriesRequired(command);
        try {
            Collection<ProvisioningCriteriaData> criteriaCollection = this.provisioningCriteriaReadPlatformService
                    .retrieveAllProvisioningCriterias();
            if (criteriaCollection == null || criteriaCollection.size() == 0) {
                throw new NoProvisioningCriteriaDefinitionFound();
            }
            ProvisioningEntry requestedEntry = createProvsioningEntry(createdDate, addJournalEntries);
            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(requestedEntry.getId()).build();
        } catch (final JpaSystemException | DataIntegrityViolationException e) {
            return CommandProcessingResult.empty();
        }
    }

    private ProvisioningEntry createProvsioningEntry(LocalDate date, boolean addJournalEntries) {
        ProvisioningEntry existingEntry = this.provisioningEntryRepository.findByProvisioningEntryDate(date);
        if (existingEntry != null) {
            throw new ProvisioningEntryAlreadyCreatedException(existingEntry.getId(), existingEntry.getCreatedDate());
        }
        AppUser currentUser = this.platformSecurityContext.authenticatedUser();
        AppUser lastModifiedBy = null;
        LocalDate lastModifiedDate = null;
        Set<LoanProductProvisioningEntry> nullEntries = null;
        ProvisioningEntry requestedEntry = new ProvisioningEntry(currentUser, date, lastModifiedBy, lastModifiedDate, nullEntries);
        Collection<LoanProductProvisioningEntry> entries = generateLoanProvisioningEntry(requestedEntry, date);
        requestedEntry.setProvisioningEntries(entries);
        if (addJournalEntries) {
            ProvisioningEntryData exisProvisioningEntryData = this.provisioningEntriesReadPlatformService
                    .retrieveExistingProvisioningIdDateWithJournals();
            revertAndAddJournalEntries(exisProvisioningEntryData, requestedEntry);
        } else {
            this.provisioningEntryRepository.saveAndFlush(requestedEntry);
        }
        return requestedEntry;
    }

    @Override
    public CommandProcessingResult reCreateProvisioningEntries(Long provisioningEntryId, JsonCommand command) {
        ProvisioningEntry requestedEntry = this.provisioningEntryRepository.findById(provisioningEntryId)
                .orElseThrow(() -> new ProvisioningEntryNotfoundException(provisioningEntryId));
        requestedEntry.getLoanProductProvisioningEntries().clear();
        this.provisioningEntryRepository.saveAndFlush(requestedEntry);
        Collection<LoanProductProvisioningEntry> entries = generateLoanProvisioningEntry(requestedEntry, requestedEntry.getCreatedDate());
        requestedEntry.setProvisioningEntries(entries);
        this.provisioningEntryRepository.saveAndFlush(requestedEntry);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(requestedEntry.getId()).build();
    }

    private Collection<LoanProductProvisioningEntry> generateLoanProvisioningEntry(ProvisioningEntry parent, LocalDate date) {
        Collection<LoanProductProvisioningEntryData> entries = this.provisioningEntriesReadPlatformService
                .retrieveLoanProductsProvisioningData(date);
        Map<Integer, LoanProductProvisioningEntry> provisioningEntries = new HashMap<>();
        for (LoanProductProvisioningEntryData data : entries) {
            LoanProduct loanProduct = this.loanProductRepository.findById(data.getProductId()).orElseThrow();
            Office office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(data.getOfficeId());
            ProvisioningCategory provisioningCategory = provisioningCategoryRepository.findById(data.getCategoryId()).orElse(null);
            GLAccount liabilityAccount = glAccountRepository.findById(data.getLiablityAccount()).orElseThrow();
            GLAccount expenseAccount = glAccountRepository.findById(data.getExpenseAccount()).orElseThrow();
            MonetaryCurrency currency = loanProduct.getPrincipalAmount().getCurrency();
            Money money = Money.of(currency, data.getBalance());
            Money amountToReserve = money.percentageOf(data.getPercentage(), MoneyHelper.getRoundingMode());
            Long criteraId = data.getCriteriaId();
            LoanProductProvisioningEntry entry = new LoanProductProvisioningEntry(loanProduct, office, data.getCurrencyCode(),
                    provisioningCategory, data.getOverdueInDays(), amountToReserve.getAmount(), liabilityAccount, expenseAccount,
                    criteraId);
            Loan loan = this.loanRepository.getReferenceById(data.getLoanId());
            entry.setProvisioningEntry(parent);
            if (!provisioningEntries.containsKey(entry.partialHashCode())) {
                entry.addLoan(loan);
                provisioningEntries.put(entry.partialHashCode(), entry);
            } else {
                LoanProductProvisioningEntry entry1 = provisioningEntries.get(entry.partialHashCode());
                entry1.addLoan(loan);
                entry1.addReservedAmount(entry.getReservedAmount());
            }
        }
        return provisioningEntries.values();
    }

    public void postWebHook(ProvisioningEntry requestedEntry) {
        AppUser currentUser = this.platformSecurityContext.authenticatedUser();
        // Build the payload
        JsonObject payload = new JsonObject();
        provisioningPayLoad(requestedEntry, payload, currentUser);

        FineractContext context = ThreadLocalContextUtil.getContext();
        // Create the HookEvent
        HookEvent hookEvent = new HookEvent(new HookEventSource("PROVISIONENTRIES", "CREATE"), payload.toString(), currentUser, context);
        // Publish the event
        eventPublisher.publishEvent(hookEvent);
    }

    private static void provisioningPayLoad(ProvisioningEntry requestedEntry, JsonObject payload, AppUser currentUser) {
        payload.addProperty("createdByName", currentUser.getUsername());

        JsonObject request = new JsonObject();
        request.addProperty("createjournalentries", true);
        request.addProperty("locale", "en");
        request.addProperty("dateFormat", "dd MMMM yyyy");
        request.addProperty("date", requestedEntry.getCreatedDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        payload.add("request", request);

        payload.addProperty("createdBy", requestedEntry.getCreatedBy().getId());
        payload.addProperty("entityName", "PROVISIONENTRIES");

        JsonObject response = new JsonObject();
        response.addProperty("resourceId", requestedEntry.getId());
        payload.add("response", response);

        payload.addProperty("createdByFullName", currentUser.getDisplayName());
        payload.addProperty("actionName", "CREATE");
    }
}
