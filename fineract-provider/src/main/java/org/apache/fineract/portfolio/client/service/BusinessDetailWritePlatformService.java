package org.apache.fineract.portfolio.client.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface BusinessDetailWritePlatformService {

    CommandProcessingResult addBusinessDetail(Long clientId, JsonCommand command);
}
