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
    private Long loanId;
    private String cashFlowType;
    private String particularType;
    private String name;
    private BigDecimal previousMonth2;
    private BigDecimal previousMonth1;
    private BigDecimal month0;

    public LoanCashFlowData(Long id, Long loanId, String cashFlowType, String particularType, String name, BigDecimal previousMonth2,
            BigDecimal previousMonth1, BigDecimal month0) {
        this.id = id;
        this.loanId = loanId;
        this.cashFlowType = cashFlowType;
        this.particularType = particularType;
        this.name = name;
        this.previousMonth2 = previousMonth2;
        this.previousMonth1 = previousMonth1;
        this.month0 = month0;
    }
}
