package org.upm.inesdata.complexpolicy;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import org.eclipse.edc.connector.controlplane.api.management.policy.transform.JsonObjectFromPolicyDefinitionTransformer;
import org.eclipse.edc.connector.controlplane.api.management.policy.transform.JsonObjectToPolicyDefinitionTransformer;
import org.eclipse.edc.connector.controlplane.api.management.policy.validation.PolicyDefinitionValidator;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.upm.inesdata.complexpolicy.controller.ComplexPolicyDefinitionApiController;
import org.upm.inesdata.complexpolicy.mapper.AtomicConstraintMapper;
import org.upm.inesdata.complexpolicy.mapper.ExpressionExtractor;
import org.upm.inesdata.complexpolicy.mapper.ExpressionMapper;
import org.upm.inesdata.complexpolicy.mapper.LiteralMapper;
import org.upm.inesdata.complexpolicy.mapper.OperatorMapper;
import org.upm.inesdata.complexpolicy.mapper.PolicyMapper;
import org.upm.inesdata.complexpolicy.mapper.PolicyValidator;

import java.util.Map;

import static org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition.EDC_POLICY_DEFINITION_TYPE;
import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;

/**
 * Extension that provides an API for managing complex policy definition
 */
@Extension(value = ComplexPolicyDefinitionApiExtension.NAME)
public class ComplexPolicyDefinitionApiExtension implements ServiceExtension {

  public static final String NAME = "Complex policy definition API Extension";

  @Inject
  private TypeTransformerRegistry transformerRegistry;

  @Inject
  private WebService webService;

  @Inject
  private PolicyDefinitionService service;

  @Inject
  private JsonObjectValidatorRegistry validatorRegistry;

  @Inject
  private TypeManager typeManager;

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    var jsonBuilderFactory = Json.createBuilderFactory(Map.of());
    var managementApiTransformerRegistry = transformerRegistry.forContext("management-api");
    var mapper = typeManager.getMapper(JSON_LD);
    managementApiTransformerRegistry.register(new JsonObjectToPolicyDefinitionTransformer());
    managementApiTransformerRegistry.register(
        new JsonObjectFromPolicyDefinitionTransformer(jsonBuilderFactory, mapper));

    validatorRegistry.register(EDC_POLICY_DEFINITION_TYPE, PolicyDefinitionValidator.instance());

    var monitor = context.getMonitor();
      ExpressionMapper expressionMapper = new ExpressionMapper(
          new AtomicConstraintMapper(new LiteralMapper(new ObjectMapper()), new OperatorMapper()));
      ExpressionExtractor expressionExtractor = new ExpressionExtractor(new PolicyValidator(), expressionMapper);
      PolicyMapper policyMapper = new PolicyMapper(expressionExtractor, expressionMapper, transformerRegistry);

      webService.registerResource(ApiContext.MANAGEMENT,
        new ComplexPolicyDefinitionApiController(transformerRegistry, service, monitor, validatorRegistry,policyMapper));
  }
}
