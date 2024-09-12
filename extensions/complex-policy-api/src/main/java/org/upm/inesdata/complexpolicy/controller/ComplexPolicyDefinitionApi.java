package org.upm.inesdata.complexpolicy.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.eclipse.edc.api.model.ApiCoreSchema;
import org.upm.inesdata.complexpolicy.model.PolicyDefinitionCreateDto;
import org.upm.inesdata.complexpolicy.model.PolicyDefinitionDto;

import java.util.List;

@OpenAPIDefinition(
    info = @Info(
        version = "v3"
    )
)
@Tag(
    name = "Complex Policy Definition"
)
public interface ComplexPolicyDefinitionApi {
  @Operation(
      description = "Creates a new policy definition",
      requestBody = @RequestBody(
          content = {@Content(
              schema = @Schema(
              )
          )}
      ),
      responses = {@ApiResponse(
          responseCode = "200",
          description = "policy definition was created successfully. Returns the Policy Definition Id and created timestamp",
          content = {@Content(
              schema = @Schema(
                  implementation = ApiCoreSchema.IdResponseSchema.class
              )
          )}
      ), @ApiResponse(
          responseCode = "400",
          description = "Request body was malformed",
          content = {@Content(
              array = @ArraySchema(
                  schema = @Schema(
                      implementation = ApiCoreSchema.ApiErrorDetailSchema.class
                  )
              )
          )}
      ), @ApiResponse(
          responseCode = "409",
          description = "Could not create policy definition, because a contract definition with that ID already exists",
          content = {@Content(
              array = @ArraySchema(
                  schema = @Schema(
                      implementation = ApiCoreSchema.ApiErrorDetailSchema.class
                  )
              )
          )}
      )}
  )
  JsonObject createPolicyDefinitionV3(PolicyDefinitionCreateDto var1);

    @Operation(
            description = "Creates a new policy definition",
            requestBody = @RequestBody(
                    content = {@Content(
                            schema = @Schema(
                            )
                    )}
            ),
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Returns the Policy Definitions",
                    content = {@Content(
                            schema = @Schema(
                                    implementation = ApiCoreSchema.IdResponseSchema.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Request body was malformed",
                    content = {@Content(
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = ApiCoreSchema.ApiErrorDetailSchema.class
                                    )
                            )
                    )}
            )}
    )
    List<PolicyDefinitionDto> getPolicyDefinitions(JsonObject querySpecJson);

}
