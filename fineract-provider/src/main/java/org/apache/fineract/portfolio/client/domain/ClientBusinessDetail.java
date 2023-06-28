package org.apache.fineract.portfolio.client.domain;

import java.math.BigDecimal;
import javax.persistence.*;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableWithUTCDateTimeCustom;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;

@Entity
@Table(name = "m_business_detail")
public class ClientBusinessDetail extends AbstractAuditableWithUTCDateTimeCustom {

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "business_type")
    private CodeValue businessType;
    @Column(name = "business_creation_date", nullable = true)
    @Temporal(TemporalType.DATE)
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
    @ManyToOne
    @JoinColumn(name = "best_month")
    private CodeValue bestMonth;
    @Column(name = "reason_for_best_month")
    private String reasonForBestMonth;
    @ManyToOne
    @JoinColumn(name = "worst_month")
    private CodeValue worstMonth;
    @Column(name = "reason_for_worst_month")
    private String reasonForWorstMonth;
    @Column(name = "number_of_purchase")
    private Long numberOfPurchase;
    @Column(name = "purchase_frequency")
    private String purchaseFrequency;
    @Column(name = "total_purchase_last_month")
    private BigDecimal totalPurchaseLastMonth;
    @Column(name = "last_purchase")
    private String lastPurchase;
    @Column(name = "last_purchase_amount")
    private BigDecimal lastPurchaseAmount;
    @Column(name = "business_asset")
    private BigDecimal businessAsset;
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

    public static ClientBusinessDetail createNew(final JsonCommand command, final Client client, final CodeValue businessType,
            AppUser currentUser) {
        final String externalId = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);
        return new ClientBusinessDetail(client, businessType, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, externalId);
    }

    public ClientBusinessDetail(Client client, CodeValue businessType, LocalDate businessCreationDate, BigDecimal startingCapital,
            CodeValue sourceOfCapital, Long totalEmployee, BigDecimal businessRevenue, BigDecimal averageMonthlyRevenue,
            CodeValue bestMonth, String reasonForBestMonth, CodeValue worstMonth, String reasonForWorstMonth, Long numberOfPurchase,
            String purchaseFrequency, BigDecimal totalPurchaseLastMonth, String lastPurchase, BigDecimal lastPurchaseAmount,
            BigDecimal businessAsset, BigDecimal amountAtCash, BigDecimal amountAtSaving, BigDecimal amountAtInventory,
            BigDecimal fixedAssetCost, BigDecimal totalInTax, BigDecimal totalInTransport, BigDecimal totalInRent,
            BigDecimal totalInCommunication, String otherExpense, BigDecimal otherExpenseAmount, BigDecimal totalUtility,
            BigDecimal totalWorkerSalary, BigDecimal totalWage, String externalId) {
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
        this.lastPurchase = lastPurchase;
        this.lastPurchaseAmount = lastPurchaseAmount;
        this.businessAsset = businessAsset;
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
    }
}
