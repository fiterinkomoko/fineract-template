package org.apache.fineract.portfolio.client.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.portfolio.client.data.ClientBusinessDetailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clients/{clientId}/businessDetail")
@Component
@Scope("singleton")
@Tag(name = "Client Business Detail", description = "This API is responsible on creating Business Details for Client")
public class ClientBusinessDetailApiResource {

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ToApiJsonSerializer<ClientBusinessDetailData> toApiJsonSerializer;

    @Autowired
    public ClientBusinessDetailApiResource(PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            ToApiJsonSerializer<ClientBusinessDetailData> toApiJsonSerializer) {
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String addBusinessDetail(@PathParam("clientId") final long clientId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().addBusinessDetail(clientId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}
