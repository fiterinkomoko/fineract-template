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

import java.util.List;
import lombok.Data;

@Data
public class MetropolCrbCreditInfoEnchancedData {

    private Integer id;
    private Integer clientId;
    private Integer loanId;
    private String reportType;
    private String apiCode;
    private String apiCodeDescription;
    private String applicationRefNo;
    private String creditScore;
    private String delinquencyCode;
    private Boolean hasError;
    private Boolean hasFraud;
    private String identityNumber;
    private String identityType;
    private Boolean isGuarantor;
    private String trxId;
    private List<MetropolAccountInfoData> accountInfoDataList;
    private Integer lenderBankAccountNpa;
    private Integer lenderBankAccountPerforming;
    private Integer lenderBankAccountPerformingNpaHistory;
    private Integer lenderOtherAccountNpa;
    private Integer lenderOtherAccountPerforming;
    private Integer lenderOtherAccountPerformingNpaHistory;
    private Integer bChecquesLast12Months;
    private Integer bChecquesLast3Months;
    private Integer bChecquesLast6Months;
    private Integer creditApLast12Months;
    private Integer creditApLast3Months;
    private Integer creditApLast6Months;
    private Integer enquiriesApLast12Months;
    private Integer enquiriesApLast3Months;
    private Integer enquiriesApLast6Months;
    private String ppiAnalysisMonth;
    private Double ppiAnalysisPpi;
    private String ppiAnalysisPpiRank;
    private String verifiedNameFirstName;
    private String verifiedNameOthername;
    private String verifiedNameSurname;

    public MetropolCrbCreditInfoEnchancedData(Integer id, Integer clientId, Integer loanId, String reportType, String apiCode,
            String apiCodeDescription, String applicationRefNo, String creditScore, String delinquencyCode, Boolean hasError,
            Boolean hasFraud, String identityNumber, String identityType, Boolean isGuarantor, String trxId, Integer lenderBankAccountNpa,
            Integer lenderBankAccountPerforming, Integer lenderBankAccountPerformingNpaHistory, Integer lenderOtherAccountNpa,
            Integer lenderOtherAccountPerforming, Integer lenderOtherAccountPerformingNpaHistory, Integer bChecquesLast12Months,
            Integer bChecquesLast3Months, Integer bChecquesLast6Months, Integer creditApLast12Months, Integer creditApLast3Months,
            Integer creditApLast6Months, Integer enquiriesApLast12Months, Integer enquiriesApLast3Months, Integer enquiriesApLast6Months,
            String ppiAnalysisMonth, Double ppiAnalysisPpi, String ppiAnalysisPpiRank, String verifiedNameFirstName,
            String verifiedNameOthername, String verifiedNameSurname) {
        this.id = id;
        this.clientId = clientId;
        this.loanId = loanId;
        this.reportType = reportType;
        this.apiCode = apiCode;
        this.apiCodeDescription = apiCodeDescription;
        this.applicationRefNo = applicationRefNo;
        this.creditScore = creditScore;
        this.delinquencyCode = delinquencyCode;
        this.hasError = hasError;
        this.hasFraud = hasFraud;
        this.identityNumber = identityNumber;
        this.identityType = identityType;
        this.isGuarantor = isGuarantor;
        this.trxId = trxId;
        this.lenderBankAccountNpa = lenderBankAccountNpa;
        this.lenderBankAccountPerforming = lenderBankAccountPerforming;
        this.lenderBankAccountPerformingNpaHistory = lenderBankAccountPerformingNpaHistory;
        this.lenderOtherAccountNpa = lenderOtherAccountNpa;
        this.lenderOtherAccountPerforming = lenderOtherAccountPerforming;
        this.lenderOtherAccountPerformingNpaHistory = lenderOtherAccountPerformingNpaHistory;
        this.bChecquesLast12Months = bChecquesLast12Months;
        this.bChecquesLast3Months = bChecquesLast3Months;
        this.bChecquesLast6Months = bChecquesLast6Months;
        this.creditApLast12Months = creditApLast12Months;
        this.creditApLast3Months = creditApLast3Months;
        this.creditApLast6Months = creditApLast6Months;
        this.enquiriesApLast12Months = enquiriesApLast12Months;
        this.enquiriesApLast3Months = enquiriesApLast3Months;
        this.enquiriesApLast6Months = enquiriesApLast6Months;
        this.ppiAnalysisMonth = ppiAnalysisMonth;
        this.ppiAnalysisPpi = ppiAnalysisPpi;
        this.ppiAnalysisPpiRank = ppiAnalysisPpiRank;
        this.verifiedNameFirstName = verifiedNameFirstName;
        this.verifiedNameOthername = verifiedNameOthername;
        this.verifiedNameSurname = verifiedNameSurname;
    }
}
