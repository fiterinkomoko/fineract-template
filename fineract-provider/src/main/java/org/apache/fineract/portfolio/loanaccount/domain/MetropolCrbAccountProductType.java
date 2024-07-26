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
package org.apache.fineract.portfolio.loanaccount.domain;

/**
 * Enum representation of account types .
 */
public enum MetropolCrbAccountProductType {

    UNKNOWN(1, "accountType.unknown"), CURRENT_ACCOUNT(2, "accountType.currentAccount"), LOAN_ACCOUNT(3,
            "accountType.loanAccount"), CREDIT_CARD(4, "accountType.creditCard"), LINE_OF_CREDIT(5,
                    "accountType.lineOfCredit"), REVOLVING_CREDIT(6, "accountType.revolvingCredit"), OVERDRAFT(7,
                            "accountType.overdraft"), CREDIT_CARD_ALT(8, "accountType.creditCardAlt"), // Assuming this
                                                                                                       // is another
                                                                                                       // type of Credit
                                                                                                       // Card
    BUSINESS_WORKING_CAPITAL(9, "accountType.businessWorkingCapital"), BUSINESS_EXPANSION_LOAN(10,
            "accountType.businessExpansionLoan"), MORTGAGE(11, "accountType.mortgage"), ASSET_FINANCE_LOAN(12,
                    "accountType.assetFinanceLoan"), TRADE_FINANCE_FACILITY(13, "accountType.tradeFinanceFacility"), PERSONAL_LOAN(14,
                            "accountType.personalLoan"), MOBILE_BANKING_LOAN(18,
                                    "accountType.mobileBankingLoan"), OTHER(19, "accountType.other");

    private final int code;
    private final String description;

    MetropolCrbAccountProductType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name().toLowerCase();
    }

    public static MetropolCrbAccountProductType fromCode(Integer code) {
        MetropolCrbAccountProductType result = null;
        switch (code) {
            case 1:
                result = UNKNOWN;
            break;
            case 2:
                result = CURRENT_ACCOUNT;
            break;
            case 3:
                result = LOAN_ACCOUNT;
            break;
            case 4:
                result = CREDIT_CARD;
            break;
            case 5:
                result = LINE_OF_CREDIT;
            break;
            case 6:
                result = REVOLVING_CREDIT;
            break;
            case 7:
                result = OVERDRAFT;
            break;
            case 8:
                result = CREDIT_CARD_ALT;
            break;
            case 9:
                result = BUSINESS_WORKING_CAPITAL;
            break;
            case 10:
                result = BUSINESS_EXPANSION_LOAN;
            break;
            case 11:
                result = MORTGAGE;
            break;
            case 12:
                result = ASSET_FINANCE_LOAN;
            break;
            case 13:
                result = TRADE_FINANCE_FACILITY;
            break;
            case 14:
                result = PERSONAL_LOAN;
            break;
            case 18:
                result = MOBILE_BANKING_LOAN;
            break;
            case 19:
                result = OTHER;
            break;
            default:
        }
        return result;
    }
}
