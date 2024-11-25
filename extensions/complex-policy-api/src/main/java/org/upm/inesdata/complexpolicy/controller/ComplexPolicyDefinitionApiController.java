package org.upm.inesdata.complexpolicy.controller;

import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.edc.api.model.IdResponse;
import org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;
import org.upm.inesdata.complexpolicy.mapper.PolicyMapper;
import org.upm.inesdata.complexpolicy.model.PolicyDefinitionCreateDto;
import org.upm.inesdata.complexpolicy.model.PolicyDefinitionDto;
import org.upm.inesdata.complexpolicy.model.UiPolicyExpression;

import java.util.Comparator;
import java.util.List;

import static java.lang.String.format;
import static org.eclipse.edc.spi.query.QuerySpec.EDC_QUERY_SPEC_TYPE;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

@Consumes({"application/json"})
@Produces({"application/json"})
@Path("/v3/complexpolicydefinitions")
public class ComplexPolicyDefinitionApiController implements ComplexPolicyDefinitionApi {
    private final TypeTransformerRegistry transformerRegistry;
    private final PolicyDefinitionService service;
    private final Monitor monitor;
    private final JsonObjectValidatorRegistry validator;
    private final PolicyMapper policyMapper;

    public ComplexPolicyDefinitionApiController(TypeTransformerRegistry transformerRegistry,
        PolicyDefinitionService service, Monitor monitor, JsonObjectValidatorRegistry validator,
        PolicyMapper policyMapper) {
        this.transformerRegistry = transformerRegistry;
        this.service = service;
        this.monitor = monitor;
        this.validator = validator;
        this.policyMapper = policyMapper;
    }

    @POST
    public JsonObject createPolicyDefinitionV3(PolicyDefinitionCreateDto request) {
        /*var expressions = transformerRegistry.transform(request, PolicyDefinitionCreateDto.class)
            .orElseThrow(InvalidRequestException::new);*/

        var policyDefinition = buildPolicyDefinition(request.getPolicyDefinitionId(), request.getExpression());
        var createdDefinition = service.create(policyDefinition)
            .onSuccess(d -> monitor.debug(format("Policy Definition created %s", d.getId())))
            .orElseThrow(exceptionMapper(PolicyDefinitionCreateDto.class, request.getPolicyDefinitionId()));

        var responseDto = IdResponse.Builder.newInstance()
            .id(createdDefinition.getId())
            .createdAt(createdDefinition.getCreatedAt())
            .build();

        return transformerRegistry.transform(responseDto, JsonObject.class)
            .orElseThrow(f -> new EdcException("Error creating response body: " + f.getFailureDetail()));
    }


    public PolicyDefinition buildPolicyDefinition(String id, UiPolicyExpression uiPolicyExpression) {
        var policy = policyMapper.buildPolicy(uiPolicyExpression);
        return PolicyDefinition.Builder.newInstance()
            .id(id)
            .policy(policy)
            .build();
    }



    @POST
    @Path("request")
    public List<PolicyDefinitionDto> getPolicyDefinitions(JsonObject querySpecJson) {
        QuerySpec querySpec;
        if (querySpecJson == null) {
            querySpec = QuerySpec.Builder.newInstance().build();
        } else {
            validator.validate(EDC_QUERY_SPEC_TYPE, querySpecJson).orElseThrow(ValidationFailureException::new);

            querySpec = transformerRegistry.transform(querySpecJson, QuerySpec.class)
                    .orElseThrow(InvalidRequestException::new);
        }

        var policyDefinitions = getAllPolicyDefinitions(querySpec);
        return policyDefinitions.stream()
                .sorted(Comparator.comparing(PolicyDefinition::getCreatedAt).reversed())
                .map(policyMapper::buildPolicyDefinitionDto)
                .toList();
    }

    private List<PolicyDefinition> getAllPolicyDefinitions(QuerySpec querySpec) {
        return service.search(querySpec).orElseThrow(f -> new EdcException("Error getting policy definitions: " + f.getFailureDetail()));
    }
}
