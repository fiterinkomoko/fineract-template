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
package org.apache.fineract.portfolio.common.domain;

public enum KivaLoanDepartmentThemeTypeMapper {

    MICRO_ENTERPRISE(228, "Micro-Enterprise"), SME(98, "SME"), VULNERABLE_POPULATION(29,
            "Vulnerable-Populations"), VULNERABLE_POPULATION_REFUGEE_KENYA(246,
                    "Vulnerable-Populations(Refugees-Kenya)"), INVALID(0, "invalid");

    private final Integer value;
    private final String code;

    KivaLoanDepartmentThemeTypeMapper(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static Integer toInt(final String theme) {
        if (theme != null) {
            switch (theme) {
                case "Micro-Enterprise":
                    return KivaLoanDepartmentThemeTypeMapper.MICRO_ENTERPRISE.getValue();
                case "SME":
                    return KivaLoanDepartmentThemeTypeMapper.SME.getValue();
                case "Vulnerable-Populations":
                    return KivaLoanDepartmentThemeTypeMapper.VULNERABLE_POPULATION.getValue();
                case "Vulnerable-Populations(Refugees-Kenya)":
                    return KivaLoanDepartmentThemeTypeMapper.VULNERABLE_POPULATION_REFUGEE_KENYA.getValue();
                default:
                    return KivaLoanDepartmentThemeTypeMapper.INVALID.getValue();
            }
        }
        return KivaLoanDepartmentThemeTypeMapper.INVALID.getValue();
    }

}
