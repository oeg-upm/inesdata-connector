package org.upm.inesdata.countelements.controller;

import jakarta.json.JsonObject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;
import org.upm.inesdata.spi.countelements.service.CountElementsService;

import java.util.Objects;

import static org.eclipse.edc.spi.query.QuerySpec.EDC_QUERY_SPEC_TYPE;

@Produces({MediaType.APPLICATION_JSON})
@Consumes({ MediaType.APPLICATION_JSON })
@Path("/pagination")
public class CountElementsApiController implements CountElementsApi {

    private final CountElementsService service;
    private final JsonObjectValidatorRegistry validator;
    private final TypeTransformerRegistry transformerRegistry;

    public CountElementsApiController(CountElementsService service, TypeTransformerRegistry transformerRegistry, JsonObjectValidatorRegistry validator) {
        this.service = service;
        this.transformerRegistry = transformerRegistry;
        this.validator = validator;
    }

    @POST
    @Path("/count")
    @Override
    public long countElements(@QueryParam("type") String entityType, JsonObject querySpecJson) {
        if (!Objects.equals(entityType, "asset") && !Objects.equals(entityType, "policyDefinition")
                && !Objects.equals(entityType, "contractDefinition")
                && !Objects.equals(entityType, "contractAgreement")
                && !Objects.equals(entityType, "transferProcess")
                && !Objects.equals(entityType, "federatedCatalog")) {
            throw new BadRequestException("Entity type provided is not valid");
        }

        QuerySpec querySpec;
        if (querySpecJson == null) {
            querySpec = QuerySpec.Builder.newInstance().build();
        } else {
            validator.validate(EDC_QUERY_SPEC_TYPE, querySpecJson).orElseThrow(ValidationFailureException::new);

            querySpec = transformerRegistry.transform(querySpecJson, QuerySpec.class)
                    .orElseThrow(InvalidRequestException::new);
        }

        var count = service.countElements(entityType, querySpec);

//        JsonObject result =  transformerRegistry.transform(count, JsonObject.class)
//                .orElseThrow(f -> new EdcException(f.getFailureDetail()));

        return  count.getCount();
    }
}
