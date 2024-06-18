package org.upm.inesdata.countelements.controller;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.upm.inesdata.spi.countelements.service.CountElementsService;

import java.util.Objects;

@Produces({MediaType.APPLICATION_JSON})
@Path("/pagination")
public class CountElementsApiController implements CountElementsApi {

    private final CountElementsService service;
    private final TypeTransformerRegistry transformerRegistry;

    public CountElementsApiController(CountElementsService service, TypeTransformerRegistry transformerRegistry) {
        this.service = service;
        this.transformerRegistry = transformerRegistry;
    }

    @GET
    @Path("/count")
    @Override
    public long countElements(@QueryParam("type") String entityType) {
        if (!Objects.equals(entityType, "asset") && !Objects.equals(entityType, "policyDefinition")
                && !Objects.equals(entityType, "contractDefinition")
                && !Objects.equals(entityType, "contractAgreement")
                && !Objects.equals(entityType, "transferProcess")) {
            throw new BadRequestException("Entity type provided is not valid");
        }

        var count = service.countElements(entityType);

//        JsonObject result =  transformerRegistry.transform(count, JsonObject.class)
//                .orElseThrow(f -> new EdcException(f.getFailureDetail()));

        return  count.getCount();
    }
}
