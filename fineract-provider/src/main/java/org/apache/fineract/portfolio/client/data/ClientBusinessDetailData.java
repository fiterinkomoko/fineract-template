package org.apache.fineract.portfolio.client.data;

import java.math.BigDecimal;
import java.util.Collection;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.joda.time.LocalDate;

public class ClientBusinessDetailData {

    private Long clientId;
    private Collection<CodeValueData> businessType;
    private LocalDate businessCreationDate;
    private BigDecimal startingCapital;
    private Collection<CodeValueData> sourceOfCapital;
    private Long totalEmployee;
    private BigDecimal businessRevenue;
    private BigDecimal averageMonthlyRevenue;
    private Collection<CodeValueData> bestMonth;
    private String reasonForBestMonth;
    private Collection<CodeValueData> worstMonth;
    private String reasonForWorstMonth;
    private Long numberOfPurchase;
    private String purchaseFrequency;
    private BigDecimal totalPurchaseLastMonth;
    private String lastPurchase;
    private BigDecimal lastPurchaseAmount;
    private BigDecimal businessAsset;
    private BigDecimal amountAtCash;
    private BigDecimal amountAtSaving;
    private BigDecimal amountAtInventory;
    private BigDecimal fixedAssetCost;
    private BigDecimal totalInTax;
    private BigDecimal totalInTransport;
    private BigDecimal totalInRent;
    private BigDecimal totalInCommunication;
    private String otherExpense;
    private BigDecimal otherExpenseAmount;
    private BigDecimal totalUtility;
    private BigDecimal totalWorkerSalary;
    private BigDecimal totalWage;
    private String externalId;
}
