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
package org.apache.fineract.portfolio.client.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.database.DatabaseSpecificSQLGenerator;
import org.apache.fineract.infrastructure.dataqueries.service.EntityDatatableChecksReadService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.address.service.AddressReadPlatformService;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientBusinessDetailData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.domain.MonthEnum;
import org.apache.fineract.portfolio.collateralmanagement.domain.ClientCollateralManagementRepositoryWrapper;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessDetailReadPlatformServiceImpl implements BusinessDetailReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final SavingsProductReadPlatformService savingsProductReadPlatformService;
    // data mappers
    private final PaginationHelper paginationHelper;
    private final DatabaseSpecificSQLGenerator sqlGenerator;

    private final CodeValueRepositoryWrapper codeValueRepository;
    private final AddressReadPlatformService addressReadPlatformService;
    private final ClientFamilyMembersReadPlatformService clientFamilyMembersReadPlatformService;
    private final ConfigurationReadPlatformService configurationReadPlatformService;
    private final EntityDatatableChecksReadService entityDatatableChecksReadService;
    private final ColumnValidator columnValidator;
    private final ClientCollateralManagementRepositoryWrapper clientCollateralManagementRepositoryWrapper;
    private final ClientBusinessOwnerReadPlatformService clientBusinessOwnerReadPlatformService;
    private final ClientReadPlatformService clientReadPlatformService;

    @Override
    public ClientBusinessDetailData retrieveTemplate(Long clientId) {
        this.context.authenticatedUser();

        final List<CodeValueData> businessTypeOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.BUSINESS_TYPE_OPTIONS));
        final List<CodeValueData> sourceOfCapitalOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.SOURCE_OF_CAPITAL_OPTIONS));
        final List<EnumOptionData> monthEnumOptions = ClientEnumerations.monthEnum(MonthEnum.values());
        final ClientData clientAccount = this.clientReadPlatformService.retrieveOne(clientId);
        return ClientBusinessDetailData.template(businessTypeOptions, sourceOfCapitalOptions, monthEnumOptions, monthEnumOptions,
                clientAccount);

    }

    @Override
    public ClientBusinessDetailData retrieveTemplate() {
        this.context.authenticatedUser();

        final List<CodeValueData> businessTypeOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.BUSINESS_TYPE_OPTIONS));
        final List<CodeValueData> sourceOfCapitalOptions = new ArrayList<>(
                this.codeValueReadPlatformService.retrieveCodeValuesByCode(ClientApiConstants.SOURCE_OF_CAPITAL_OPTIONS));
        final List<EnumOptionData> monthEnumOptions = ClientEnumerations.monthEnum(MonthEnum.values());
        return ClientBusinessDetailData.template(businessTypeOptions, sourceOfCapitalOptions, monthEnumOptions, monthEnumOptions, null);
    }

}
