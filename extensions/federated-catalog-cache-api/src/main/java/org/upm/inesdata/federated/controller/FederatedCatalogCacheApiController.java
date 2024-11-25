package org.upm.inesdata.federated.controller;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;
import org.jetbrains.annotations.NotNull;
import org.upm.inesdata.complexpolicy.mapper.PolicyMapper;
import org.upm.inesdata.complexpolicy.model.UiPolicy;
import org.upm.inesdata.federated.model.DspContractOffer;
import org.upm.inesdata.federated.model.UiContractOffer;
import org.upm.inesdata.federated.utils.JsonLdUtils;
import org.upm.inesdata.federated.utils.Prop;
import org.upm.inesdata.spi.federated.FederatedCatalogCacheService;

import static jakarta.json.stream.JsonCollectors.toJsonArray;
import static org.eclipse.edc.spi.query.QuerySpec.EDC_QUERY_SPEC_TYPE;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

/**
 * Controller class for the Federated Catalog Cache API. This class implements the {@link FederatedCatalogCacheApi}
 * interface and provides the API endpoints to interact with the federated catalog cache.
 */
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/federatedcatalog")
public class FederatedCatalogCacheApiController implements FederatedCatalogCacheApi {
    private final TypeTransformerRegistry transformerRegistry;
    private final FederatedCatalogCacheService service;
    private final JsonObjectValidatorRegistry validator;
    private final Monitor monitor;
    private final PolicyMapper policyMapper;

    /**
     * Constructs a FederatedCatalogCacheApiController with the specified dependencies.
     *
     * @param service             the service used to access the federated catalog cache.
     * @param transformerRegistry the registry for type transformers.
     * @param validator           the registry for JSON object validators.
     * @param monitor             the monitor used for logging and monitoring.
     * @param policyMapper        the mapper used for policies
     */
    public FederatedCatalogCacheApiController(FederatedCatalogCacheService service,
                                              TypeTransformerRegistry transformerRegistry, JsonObjectValidatorRegistry validator, Monitor monitor, PolicyMapper policyMapper) {
        this.transformerRegistry = transformerRegistry;
        this.service = service;
        this.validator = validator;
        this.monitor = monitor;
        this.policyMapper = policyMapper;
    }

    /**
     * (non-javadoc)
     *
     * @see FederatedCatalogCacheApi#getFederatedCatalog(JsonObject)
     */
    @Override
    @POST
    @Path("/request")
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
        .peek(r -> r.onFailure(f -> monitor.warning(f.getFailureDetail()))).filter(Result::succeeded)
        .map(Result::getContent)
        .map(this::modifyPolicyResponse)
        .collect(toJsonArray());
    }

    private JsonObject modifyPolicyResponse(JsonObject originalResponse) {
        if (originalResponse.get(Prop.Dcat.DATASET) != null) {
            JsonArray datasets = originalResponse.getJsonArray(Prop.Dcat.DATASET);
            JsonArrayBuilder datasetArrayBuilder = Json.createArrayBuilder();

            for (JsonValue datasetElement : datasets) {
                JsonObject dataset = datasetElement.asJsonObject();

                if (dataset.getJsonArray(Prop.Odrl.HAS_POLICY) != null) {
                    JsonArray existingPolicies = dataset.getJsonArray(Prop.Odrl.HAS_POLICY);

                    var contractOffers = JsonLdUtils.listOfObjects(dataset, Prop.Odrl.HAS_POLICY).stream()
                            .map(this::buildContractOffer)
                            .map(this::buildUiContractOffer)
                            .toList();

                    JsonArrayBuilder uiContractOffersJsonBuilder = Json.createArrayBuilder();
                    contractOffers.forEach(offer -> uiContractOffersJsonBuilder.add(
                            transformerRegistry.transform(offer, JsonObject.class).getContent()));
                    JsonArray uiContractOffersJson = uiContractOffersJsonBuilder.build();

                    JsonArrayBuilder newHasPolicyArrayBuilder = Json.createArrayBuilder();

                    for (int i = 0; i < uiContractOffersJson.size(); i++) {
                        JsonObject complexPolicyElement = uiContractOffersJson.getJsonObject(i);
                        JsonValue offerElement = i < existingPolicies.size() ? existingPolicies.get(i) : null;

                        JsonObject newHasPolicyElement = Json.createObjectBuilder()
                                .add(Prop.Edc.CTX + "complexPolicy", complexPolicyElement)
                                .add(Prop.Edc.CTX + "offer", offerElement)
                                .build();

                        newHasPolicyArrayBuilder.add(newHasPolicyElement);
                    }

                    JsonArray newHasPolicyArray = newHasPolicyArrayBuilder.build();

                    JsonObject modifiedDatasetJson = Json.createObjectBuilder(dataset)
                            .remove(Prop.Odrl.HAS_POLICY)
                            .add(Prop.Odrl.HAS_POLICY, newHasPolicyArray)
                            .build();

                    datasetArrayBuilder.add(modifiedDatasetJson);
                } else {
                    datasetArrayBuilder.add(dataset);
                }
            }

            JsonArray datasetArrayModified = datasetArrayBuilder.build();

            return Json.createObjectBuilder(originalResponse)
                    .remove(Prop.Dcat.DATASET)
                    .add(Prop.Dcat.DATASET, datasetArrayModified)
                    .build();
        } else {
            return originalResponse;
        }
    }

    @NotNull
    private DspContractOffer buildContractOffer(JsonObject json) {
        return new DspContractOffer(JsonLdUtils.id(json), json);
    }

    private UiPolicy buildUiPolicy(DspContractOffer contractOffer) {
        JsonArrayBuilder typeArrayBuilder = Json.createArrayBuilder();
        typeArrayBuilder.add(Prop.Odrl.CTX + "Set");

        JsonArray typeArray = typeArrayBuilder.build();

        JsonObject modifiedJson = Json.createObjectBuilder(contractOffer.getPolicyJsonLd())
                .remove(Prop.TYPE)
                .add(Prop.TYPE, typeArray)
                .build();

        var policy = policyMapper.buildPolicy(modifiedJson);
        return policyMapper.buildUiPolicy(policy);
    }

    private UiContractOffer buildUiContractOffer(DspContractOffer contractOffer) {
        var uiContractOffer = new UiContractOffer();
        uiContractOffer.setContractOfferId(contractOffer.getContractOfferId());
        uiContractOffer.setPolicy(buildUiPolicy(contractOffer));
        return uiContractOffer;
    }

}
