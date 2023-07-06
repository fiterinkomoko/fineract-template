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
package org.apache.fineract.portfolio.client.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;

@Entity
@Table(name = "m_business_detail")
public class ClientBusinessDetail extends AbstractAuditableCustom {

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "business_type_id")
    private CodeValue businessType;
    @Column(name = "business_creation_date")
    private LocalDate businessCreationDate;
    @Column(name = "starting_capital")
    private BigDecimal startingCapital;
    @ManyToOne
    @JoinColumn(name = "source_of_capital")
    private CodeValue sourceOfCapital;
    @Column(name = "total_employee")
    private Long totalEmployee;
    @Column(name = "business_revenue")
    private BigDecimal businessRevenue;
    @Column(name = "average_monthly_revenue")
    private BigDecimal averageMonthlyRevenue;

    @Column(name = "best_month")
    private Integer bestMonth;
    @Column(name = "reason_for_best_month")
    private String reasonForBestMonth;

    @Column(name = "worst_month")
    private Integer worstMonth;
    @Column(name = "reason_for_worst_month")
    private String reasonForWorstMonth;
    @Column(name = "number_of_purchase")
    private Long numberOfPurchase;
    @Column(name = "purchase_frequency")
    private String purchaseFrequency;
    @Column(name = "total_purchase_last_month")
    private BigDecimal totalPurchaseLastMonth;
    @Column(name = "last_purchase")
    private Integer whenLastPurchase;
    @Column(name = "last_purchase_amount")
    private BigDecimal lastPurchaseAmount;
    @Column(name = "business_asset_amount")
    private BigDecimal businessAssetAmount;
    @Column(name = "amount_at_cash")
    private BigDecimal amountAtCash;
    @Column(name = "amount_at_saving")
    private BigDecimal amountAtSaving;
    @Column(name = "amount_at_inventory")
    private BigDecimal amountAtInventory;
    @Column(name = "fixed_asset_cost")
    private BigDecimal fixedAssetCost;
    @Column(name = "total_in_tax")
    private BigDecimal totalInTax;
    @Column(name = "total_in_transport")
    private BigDecimal totalInTransport;
    @Column(name = "total_in_rent")
    private BigDecimal totalInRent;
    @Column(name = "total_in_communication")
    private BigDecimal totalInCommunication;
    @Column(name = "other_expense")
    private String otherExpense;
    @Column(name = "other_expense_amount")
    private BigDecimal otherExpenseAmount;
    @Column(name = "total_utility")
    private BigDecimal totalUtility;
    @Column(name = "total_worker_salary")
    private BigDecimal totalWorkerSalary;
    @Column(name = "total_wage")
    private BigDecimal totalWage;
    @Column(name = "external_id")
    private String externalId;
    @Column(name = "society")
    private BigDecimal society;

    public ClientBusinessDetail() {}

    public static ClientBusinessDetail createNew(final Client client, final CodeValue businessType, final CodeValue sourceOfCapital,
            LocalDate businessCreationDate, BigDecimal startingCapital, Long totalEmployee, BigDecimal businessRevenue,
            BigDecimal averageMonthlyRevenue, Integer bestMonth, String reasonForBestMonth, Integer worstMonth, String reasonForWorstMonth,
            Long numberOfPurchase, String purchaseFrequency, BigDecimal totalPurchaseLastMonth, Integer lastPurchase,
            BigDecimal lastPurchaseAmount, BigDecimal businessAssetAmount, BigDecimal amountAtCash, BigDecimal amountAtSaving,
            BigDecimal amountAtInventory, BigDecimal fixedAssetCost, BigDecimal totalInTax, BigDecimal totalInTransport,
            BigDecimal totalInRent, BigDecimal totalInCommunication, String otherExpense, BigDecimal otherExpenseAmount,
            BigDecimal totalUtility, BigDecimal totalWorkerSalary, BigDecimal totalWage, String externalId, BigDecimal society) {

        return new ClientBusinessDetail(client, businessType, businessCreationDate, startingCapital, sourceOfCapital, totalEmployee,
                businessRevenue, averageMonthlyRevenue, bestMonth, reasonForBestMonth, worstMonth, reasonForWorstMonth, numberOfPurchase,
                purchaseFrequency, totalPurchaseLastMonth, lastPurchase, lastPurchaseAmount, businessAssetAmount, amountAtCash,
                amountAtSaving, amountAtInventory, fixedAssetCost, totalInTax, totalInTransport, totalInRent, totalInCommunication,
                otherExpense, otherExpenseAmount, totalUtility, totalWorkerSalary, totalWage, externalId, society);
    }

    public ClientBusinessDetail(Client client, CodeValue businessType, LocalDate businessCreationDate, BigDecimal startingCapital,
            CodeValue sourceOfCapital, Long totalEmployee, BigDecimal businessRevenue, BigDecimal averageMonthlyRevenue, Integer bestMonth,
            String reasonForBestMonth, Integer worstMonth, String reasonForWorstMonth, Long numberOfPurchase, String purchaseFrequency,
            BigDecimal totalPurchaseLastMonth, Integer lastPurchase, BigDecimal lastPurchaseAmount, BigDecimal businessAssetAmount,
            BigDecimal amountAtCash, BigDecimal amountAtSaving, BigDecimal amountAtInventory, BigDecimal fixedAssetCost,
            BigDecimal totalInTax, BigDecimal totalInTransport, BigDecimal totalInRent, BigDecimal totalInCommunication,
            String otherExpense, BigDecimal otherExpenseAmount, BigDecimal totalUtility, BigDecimal totalWorkerSalary, BigDecimal totalWage,
            String externalId, BigDecimal society) {
        this.client = client;
        this.businessType = businessType;
        this.businessCreationDate = businessCreationDate;
        this.startingCapital = startingCapital;
        this.sourceOfCapital = sourceOfCapital;
        this.totalEmployee = totalEmployee;
        this.businessRevenue = businessRevenue;
        this.averageMonthlyRevenue = averageMonthlyRevenue;
        this.bestMonth = bestMonth;
        this.reasonForBestMonth = reasonForBestMonth;
        this.worstMonth = worstMonth;
        this.reasonForWorstMonth = reasonForWorstMonth;
        this.numberOfPurchase = numberOfPurchase;
        this.purchaseFrequency = purchaseFrequency;
        this.totalPurchaseLastMonth = totalPurchaseLastMonth;
        this.whenLastPurchase = lastPurchase;
        this.lastPurchaseAmount = lastPurchaseAmount;
        this.businessAssetAmount = businessAssetAmount;
        this.amountAtCash = amountAtCash;
        this.amountAtSaving = amountAtSaving;
        this.amountAtInventory = amountAtInventory;
        this.fixedAssetCost = fixedAssetCost;
        this.totalInTax = totalInTax;
        this.totalInTransport = totalInTransport;
        this.totalInRent = totalInRent;
        this.totalInCommunication = totalInCommunication;
        this.otherExpense = otherExpense;
        this.otherExpenseAmount = otherExpenseAmount;
        this.totalUtility = totalUtility;
        this.totalWorkerSalary = totalWorkerSalary;
        this.totalWage = totalWage;
        this.externalId = externalId;
        this.society = society;
    }
}
