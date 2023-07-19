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
 * Enum representation of loan decision states.
 */
public enum LoanDecisionState {

    INVALID(0, "loanStatusType.invalid"), REVIEW_APPLICATION(1000, "loanDecisionStateType.submitted.and.review.Pending"), DUE_DILIGENCE(
            1200, "loanDecisionStateType.submitted.and.review.due.diligence.Pending"),

    ;

    private final Integer value;
    private final String code;

    public static LoanDecisionState fromInt(final Integer statusValue) {

        LoanDecisionState enumeration = LoanDecisionState.INVALID;
        switch (statusValue) {
            case 100:
                enumeration = LoanDecisionState.REVIEW_APPLICATION;
            break;
            case 1200:
                enumeration = LoanDecisionState.DUE_DILIGENCE;
            break;
        }
        return enumeration;
    }

    LoanDecisionState(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public boolean hasStateOf(final LoanDecisionState state) {
        return this.value.equals(state.getValue());
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
}
