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
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class LoanCashFlowData {

    private Long id;
    private String monthType;
    private Long loanId;
    private BigDecimal expTotalPurchases;
    private BigDecimal expTax;
    private BigDecimal expTransport;
    private BigDecimal expRent;
    private BigDecimal expCommunication;
    private BigDecimal otherExpenseAmount;
    private BigDecimal expUtility;
    private BigDecimal expWorkerSalary;
    private BigDecimal expWages;
    private BigDecimal expPurchaseLastMonth1;
    private BigDecimal expRentMonthly;
    private BigDecimal expUtilitiesMonthly;
    private BigDecimal incomeAverageMonthlyRevenue;
    private BigDecimal incomeAmountInCash;
    private BigDecimal incomeAmountInSavings;
    private BigDecimal incomeAmountInInventory;
    private BigDecimal incomeBusinessAsset;
    private BigDecimal incomeFixedAssetsCost;
    private BigDecimal incomeGeneratingActivity;

    public LoanCashFlowData(Long id, String monthType, Long loanId, BigDecimal expTotalPurchases, BigDecimal expTax,
            BigDecimal expTransport, BigDecimal expRent, BigDecimal expCommunication, BigDecimal otherExpenseAmount, BigDecimal expUtility,
            BigDecimal expWorkerSalary, BigDecimal expWages, BigDecimal expPurchaseLastMonth1, BigDecimal expRentMonthly,
            BigDecimal expUtilitiesMonthly, BigDecimal incomeAverageMonthlyRevenue, BigDecimal incomeAmountInCash,
            BigDecimal incomeAmountInSavings, BigDecimal incomeAmountInInventory, BigDecimal incomeBusinessAsset,
            BigDecimal incomeFixedAssetsCost, BigDecimal incomeGeneratingActivity) {
        this.id = id;
        this.monthType = monthType;
        this.loanId = loanId;
        this.expTotalPurchases = expTotalPurchases;
        this.expTax = expTax;
        this.expTransport = expTransport;
        this.expRent = expRent;
        this.expCommunication = expCommunication;
        this.otherExpenseAmount = otherExpenseAmount;
        this.expUtility = expUtility;
        this.expWorkerSalary = expWorkerSalary;
        this.expWages = expWages;
        this.expPurchaseLastMonth1 = expPurchaseLastMonth1;
        this.expRentMonthly = expRentMonthly;
        this.expUtilitiesMonthly = expUtilitiesMonthly;
        this.incomeAverageMonthlyRevenue = incomeAverageMonthlyRevenue;
        this.incomeAmountInCash = incomeAmountInCash;
        this.incomeAmountInSavings = incomeAmountInSavings;
        this.incomeAmountInInventory = incomeAmountInInventory;
        this.incomeBusinessAsset = incomeBusinessAsset;
        this.incomeFixedAssetsCost = incomeFixedAssetsCost;
        this.incomeGeneratingActivity = incomeGeneratingActivity;
    }
}
