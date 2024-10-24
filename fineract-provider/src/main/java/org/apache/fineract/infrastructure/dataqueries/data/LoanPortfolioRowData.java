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
package org.apache.fineract.infrastructure.dataqueries.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class LoanPortfolioRowData {

        @JsonProperty("Loan Number")
        private String loanNumber;

        @JsonProperty("Loan Officer")
        private String loanOfficer;

        @JsonProperty("Client Name")
        private String clientName;

        @JsonProperty("Client UUID")
        private String clientUUID;

        @JsonProperty("Loan Product")
        private String loanProduct;

        @JsonProperty("Purpose")
        private String purpose;

        @JsonProperty("Department")
        private String department;

        @JsonProperty("Strata")
        private String strata;

        @JsonProperty("KIVA Loan ID")
        private String kivaLoanId;

        @JsonProperty("Funder")
        private String funder;

        @JsonProperty("Client ID")
        private String clientId;

        @JsonProperty("Date of Birth")
        private LocalDate dateOfBirth;

        @JsonProperty("KIVA Client ID")
        private String kivaClientId;

        @JsonProperty("Cycle")
        private long cycle;

        @JsonProperty("Cohort")
        private String cohort;

        @JsonProperty("Gender")
        private String gender;

        @JsonProperty("Province")
        private String province;

        @JsonProperty("Sector")
        private String sector;

        @JsonProperty("Cell")
        private String cell;

        @JsonProperty("District")
        private String district;

        @JsonProperty("Nationality")
        private String nationality;

        @JsonProperty("Telephone")
        private String telephone;

        @JsonProperty("Mobile No")
        private String mobileNo;

        @JsonProperty("Submission Date ")
        private LocalDate submissionDate;

        @JsonProperty("Approval Date")
        private LocalDate approvalDate;

        @JsonProperty("Disbursement Date")
        private LocalDate disbursementDate;

        @JsonProperty("Applied Amount")
        private BigDecimal appliedAmount;

        @JsonProperty("Approved Amount")
        private BigDecimal approvedAmount;

        @JsonProperty("Disbursed Amount")
        private BigDecimal disbursedAmount;

        @JsonProperty("Difference")
        private BigDecimal difference;

        @JsonProperty("Currency Type")
        private String currencyType;

        @JsonProperty("Re-payment Term")
        private String repaymentTerm;

        @JsonProperty("Loan Type")
        private String loanType;

        @JsonProperty("Terms Duration")
        private short termsDuration;

        @JsonProperty("Actual Payment Amount")
        private BigDecimal actualPaymentAmount;

        @JsonProperty("Principal Paid")
        private BigDecimal principalPaid;

        @JsonProperty("Interest Paid")
        private BigDecimal interestPaid;

        @JsonProperty("Insurance fee Paid")
        private BigDecimal insuranceFeePaid;

        @JsonProperty("Total Late Fees Paid")
        private BigDecimal totalLateFeesPaid;

        @JsonProperty("Excess Amount Paid")
        private BigDecimal excessAmountPaid;

        @JsonProperty("Current Balance")
        private BigDecimal currentBalance;

        @JsonProperty("Principal Balance")
        private BigDecimal principalBalance;

        @JsonProperty("Interest Balance")
        private BigDecimal interestBalance;

        @JsonProperty("Fees Balance")
        private int feesBalance;

        @JsonProperty("Amount Past Due")
        private BigDecimal amountPastDue;

        @JsonProperty("Principal Past Due")
        private BigDecimal principalPastDue;

        @JsonProperty("Interest Past Due")
        private BigDecimal interestPastDue;

        @JsonProperty("Fees Past Due")
        private BigDecimal feesPastDue;

        @JsonProperty("Scheduled Principal Amount")
        private BigDecimal scheduledPrincipalAmount;

        @JsonProperty("Scheduled Interest Amount")
        private BigDecimal scheduledInterestAmount;

        @JsonProperty("Scheduled Fees Amount")
        private BigDecimal scheduledFeesAmount;

        @JsonProperty("Scheduled Payment Amount")
        private BigDecimal scheduledPaymentAmount;

        @JsonProperty("Last Payment Amount")
        private BigDecimal lastPaymentAmount;

        @JsonProperty("Last Principal Amount")
        private BigDecimal lastPrincipalAmount;

        @JsonProperty("Last Interest Amount")
        private BigDecimal lastInterestAmount;

        @JsonProperty("Last Fees Amount")
        private BigDecimal lastFeesAmount;

        @JsonProperty("Last Late Fees Amount")
        private BigDecimal lastLateFeesAmount;

        @JsonProperty("Last Excess Amount")
        private BigDecimal lastExcessAmount;

        @JsonProperty("Days in Arrears")
        private int daysInArrears;

        @JsonProperty("Installment in Arrears")
        private long installmentInArrears;

        @JsonProperty("Last Payment Date")
        private LocalDate lastPaymentDate;

        @JsonProperty("Next Payment Due")
        private LocalDate nextPaymentDue;

        @JsonProperty("Final Payment Date")
        private LocalDate finalPaymentDate;

        @JsonProperty("Date Closed")
        private LocalDate dateClosed;

        @JsonProperty("Loan Status")
        private String loanStatus;

        @JsonProperty("Business Description")
        private String businessDescription;

        @JsonProperty("Industry/Sector of Activity")
        private String industrySectorOfActivity;

        @JsonProperty("Business Sub-Sector")
        private String businessSubSector;


}
