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

import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientBusinessDetailDataValidator;
import org.apache.fineract.portfolio.client.domain.*;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessDetailWritePlatformServiceImpl implements BusinessDetailWritePlatformService {

    private final PlatformSecurityContext context;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final ClientBusinessDetailRepository clientBusinessDetailRepository;
    private final ClientBusinessDetailDataValidator fromApiJsonDeserializer;
    private final ConfigurationReadPlatformService configurationReadPlatformService;

    @Transactional
    @Override
    public CommandProcessingResult addBusinessDetail(Long clientId, JsonCommand command) {
        final AppUser currentUser = this.context.authenticatedUser();

        this.fromApiJsonDeserializer.validateForCreate(clientId, command.json());

        final GlobalConfigurationPropertyData businessDetailConfig = this.configurationReadPlatformService
                .retrieveGlobalConfiguration("Enable-businessDetail");

        final Boolean isClientBusinessDetailsEnable = businessDetailConfig.isEnabled();

        if (!isClientBusinessDetailsEnable) {
            // This operation is not permitted here
        }

        final Client client = clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);

        ClientBusinessDetail businessDetail = createBusinessDetail(command, client, currentUser);
        clientBusinessDetailRepository.save(businessDetail);

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(businessDetail.getId()).build();
    }

    private ClientBusinessDetail createBusinessDetail(final JsonCommand command, Client client, AppUser currentUser) {

        CodeValue businessType = null;
        final Long businessTypeId = command.longValueOfParameterNamed(ClientApiConstants.businessType);
        if (businessTypeId != null) {
            businessType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(ClientApiConstants.businessType,
                    businessTypeId);
        }

        ClientBusinessDetail clientBusinessDetail = ClientBusinessDetail.createNew(command, client, businessType, currentUser);
        return clientBusinessDetail;
    }
}
