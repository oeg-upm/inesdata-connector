package org.upm.inesdata.federated.controller;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.AbstractResult;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;
import org.upm.inesdata.spi.federated.FederatedCatalogCacheService;

import java.util.Objects;

import static jakarta.json.stream.JsonCollectors.toJsonArray;
import static org.eclipse.edc.spi.query.QuerySpec.EDC_QUERY_SPEC_TYPE;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/federatedcatalog")
public class FederatedCatalogCacheApiController
    implements FederatedCatalogCacheApi {
  private final TypeTransformerRegistry transformerRegistry;
  private final FederatedCatalogCacheService service;
  private final JsonObjectValidatorRegistry validator;
  private final Monitor monitor;


  public FederatedCatalogCacheApiController(FederatedCatalogCacheService service, TypeTransformerRegistry transformerRegistry,
      JsonObjectValidatorRegistry validator, Monitor monitor) {
    this.transformerRegistry = transformerRegistry;
    this.service = service;
    this.validator = validator;
    this.monitor = monitor;
  }

  @Override
  @POST
  @Path("/requestPagination")
  public JsonArray getFederatedCatalog(JsonObject querySpecJson) {
    QuerySpec querySpec;
    if (querySpecJson == null) {
      querySpec = QuerySpec.Builder.newInstance().build();
    } else {
      validator.validate(EDC_QUERY_SPEC_TYPE, querySpecJson).orElseThrow(ValidationFailureException::new);

      querySpec = transformerRegistry.transform(querySpecJson, QuerySpec.class)
          .orElseThrow(InvalidRequestException::new);
    }
    return service.searchPagination(querySpec).orElseThrow(exceptionMapper(QuerySpec.class, null)).stream()
        .map(it -> transformerRegistry.transform(it, JsonObject.class))
        .peek(r -> r.onFailure(f -> monitor.warning(f.getFailureDetail())))
        .filter(Result::succeeded)
        .map(Result::getContent)
        .collect(toJsonArray());
  }
}
