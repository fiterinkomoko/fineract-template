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
package org.apache.fineract.accounting.provisioning.data;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class LoanProductProvisioningEntryData {

    private final Long historyId;
    private final Long officeId;
    private final String officeName;
    private final String currencyCode;
    private final Long productId;
    private final String productName;
    private final Long categoryId;
    private final String categoryName;
    private final Long overdueInDays;
    private final BigDecimal percentage;
    private final BigDecimal balance;
    private final BigDecimal amountreserved;
    private final Long liablityAccount;
    private final String liabilityAccountCode;
    private final String liabilityAccountName;
    private final Long expenseAccount;
    private final String expenseAccountCode;
    private final String expenseAccountName;
    private final Long criteriaId;
    private final Long loanId;

    private final List<LoanData> loans;

    public LoanProductProvisioningEntryData(final Long historyId, final Long officeId, final String currencyCode, final Long productId,
            final Long categoryId, final Long overdueInDays, final BigDecimal percentage, final BigDecimal balance, Long liablityAccount,
            Long expenseAccount, final Long criteriaId, final Long loanId) {
        this.historyId = historyId;
        this.officeId = officeId;
        this.currencyCode = currencyCode;
        this.productId = productId;
        this.categoryId = categoryId;
        this.overdueInDays = overdueInDays;
        this.percentage = percentage;
        this.balance = balance;
        this.liablityAccount = liablityAccount;
        this.expenseAccount = expenseAccount;
        this.amountreserved = null;
        this.officeName = null;
        this.productName = null;
        this.categoryName = null;
        this.liabilityAccountCode = null;
        this.liabilityAccountName = null;
        this.expenseAccountCode = null;
        this.expenseAccountName = null;
        this.criteriaId = criteriaId;
        this.loanId = loanId;
        this.loans = null;

    }

    public LoanProductProvisioningEntryData(final Long historyId, final Long officeId, final String officeName, final String currencyCode,
            final Long productId, final String productName, final Long categoryId, final String categoryName, final Long overdueInDays,
            final BigDecimal amountreserved, Long liablityAccount, String liabilityAccountglCode, String liabilityAccountName,
            Long expenseAccount, String expenseAccountglCode, String expenseAccountName, final Long criteriaId, final List<LoanData> loans) {
        this.historyId = historyId;
        this.officeId = officeId;
        this.currencyCode = currencyCode;
        this.productId = productId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.overdueInDays = overdueInDays;
        this.percentage = null;
        this.balance = null;
        this.liablityAccount = liablityAccount;
        this.expenseAccount = expenseAccount;
        this.officeName = officeName;
        this.productName = productName;
        this.amountreserved = amountreserved;
        this.liabilityAccountCode = liabilityAccountglCode;
        this.liabilityAccountName = liabilityAccountName;
        this.expenseAccountCode = expenseAccountglCode;
        this.expenseAccountName = expenseAccountName;
        this.criteriaId = criteriaId;
        this.loanId = null;
        this.loans = loans;
    }
}
