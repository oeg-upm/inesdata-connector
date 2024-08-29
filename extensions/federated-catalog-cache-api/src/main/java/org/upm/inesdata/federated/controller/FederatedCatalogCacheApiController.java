package org.upm.inesdata.federated.controller;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
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

import java.io.StringReader;

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

        String jsonString = """
                [
                   {
                      "@id":"579097eb-12e1-4d88-bba9-1cabd08a8e03",
                      "@type":"http://www.w3.org/ns/dcat#Catalog",
                      "https://w3id.org/dspace/v0.8/participantId":"connector-c1",
                      "http://www.w3.org/ns/dcat#service":[
                         {
                            "@id":"6fc9cba4-6c08-4862-8fd5-54cc79825ea3",
                            "@type":"http://www.w3.org/ns/dcat#DataService",
                            "http://www.w3.org/ns/dcat#endpointDescription":"dspace:connector",
                            "http://www.w3.org/ns/dcat#endpointUrl":"http://connector-c1:19194/protocol",
                            "http://purl.org/dc/terms/terms":"dspace:connector",
                            "http://purl.org/dc/terms/endpointUrl":"http://connector-c1:19194/protocol"
                         }
                      ],
                      "https://w3id.org/edc/v0.0.1/ns/originator":"http://connector-c1:19194/protocol",
                      "https://w3id.org/edc/v0.0.1/ns/participantId":"connector-c1",
                      "http://www.w3.org/ns/dcat#dataset":[
                         {
                            "@id":"Asset 2008",
                            "@type":"http://www.w3.org/ns/dcat#Dataset",
                            "http://www.w3.org/ns/dcat#distribution":[
                               {
                                  "@type":"http://www.w3.org/ns/dcat#Distribution",
                                  "http://purl.org/dc/terms/format":{
                                     "@id":"HttpData-PULL"
                                  },
                                  "http://www.w3.org/ns/dcat#accessService":{
                                     "@id":"6fc9cba4-6c08-4862-8fd5-54cc79825ea3",
                                     "@type":"http://www.w3.org/ns/dcat#DataService",
                                     "http://www.w3.org/ns/dcat#endpointDescription":"dspace:connector",
                                     "http://www.w3.org/ns/dcat#endpointUrl":"http://connector-c1:19194/protocol",
                                     "http://purl.org/dc/terms/terms":"dspace:connector",
                                     "http://purl.org/dc/terms/endpointUrl":"http://connector-c1:19194/protocol"
                                  }
                               },
                               {
                                  "@type":"http://www.w3.org/ns/dcat#Distribution",
                                  "http://purl.org/dc/terms/format":{
                                     "@id":"HttpData-PUSH"
                                  },
                                  "http://www.w3.org/ns/dcat#accessService":{
                                     "@id":"6fc9cba4-6c08-4862-8fd5-54cc79825ea3",
                                     "@type":"http://www.w3.org/ns/dcat#DataService",
                                     "http://www.w3.org/ns/dcat#endpointDescription":"dspace:connector",
                                     "http://www.w3.org/ns/dcat#endpointUrl":"http://connector-c1:19194/protocol",
                                     "http://purl.org/dc/terms/terms":"dspace:connector",
                                     "http://purl.org/dc/terms/endpointUrl":"http://connector-c1:19194/protocol"
                                  }
                               },
                               {
                                  "@type":"http://www.w3.org/ns/dcat#Distribution",
                                  "http://purl.org/dc/terms/format":{
                                     "@id":"AmazonS3-PULL"
                                  },
                                  "http://www.w3.org/ns/dcat#accessService":{
                                     "@id":"6fc9cba4-6c08-4862-8fd5-54cc79825ea3",
                                     "@type":"http://www.w3.org/ns/dcat#DataService",
                                     "http://www.w3.org/ns/dcat#endpointDescription":"dspace:connector",
                                     "http://www.w3.org/ns/dcat#endpointUrl":"http://connector-c1:19194/protocol",
                                     "http://purl.org/dc/terms/terms":"dspace:connector",
                                     "http://purl.org/dc/terms/endpointUrl":"http://connector-c1:19194/protocol"
                                  }
                               },
                               {
                                  "@type":"http://www.w3.org/ns/dcat#Distribution",
                                  "http://purl.org/dc/terms/format":{
                                     "@id":"AmazonS3-PUSH"
                                  },
                                  "http://www.w3.org/ns/dcat#accessService":{
                                     "@id":"6fc9cba4-6c08-4862-8fd5-54cc79825ea3",
                                     "@type":"http://www.w3.org/ns/dcat#DataService",
                                     "http://www.w3.org/ns/dcat#endpointDescription":"dspace:connector",
                                     "http://www.w3.org/ns/dcat#endpointUrl":"http://connector-c1:19194/protocol",
                                     "http://purl.org/dc/terms/terms":"dspace:connector",
                                     "http://purl.org/dc/terms/endpointUrl":"http://connector-c1:19194/protocol"
                                  }
                               }
                            ],
                            "https://w3id.org/edc/v0.0.1/ns/assetType":"machineLearning",
                            "http://purl.org/dc/terms/format":"",
                            "https://w3id.org/edc/v0.0.1/ns/version":"Asset 2008",
                            "http://www.w3.org/ns/dcat#keyword":"test",
                            "http://www.w3.org/ns/dcat#byteSize":"",
                            "https://w3id.org/edc/v0.0.1/ns/name":"Asset 2008",
                            "https://w3id.org/edc/v0.0.1/ns/participantId":"connector-c1",
                            "https://w3id.org/edc/v0.0.1/ns/assetData":{
                               "https://w3id.org/edc/v0.0.1/ns/machine-learning-vocabulary":[
                                  {
                                     "https://w3id.org/edc/v0.0.1/ns/title":[
                                        {
                                           "@value":"title vocab"
                                        }
                                     ],
                                     "https://w3id.org/edc/v0.0.1/ns/keyword":[
                                        {
                                           "@value":"test"
                                        },
                                        {
                                           "@value":"conn1"
                                        }
                                     ],
                                     "https://w3id.org/edc/v0.0.1/ns/trainedOn":[
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-object"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-object"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/description":[
                                              {
                                                 "@value":"desc-object"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/url":[
                                              {
                                                 "@value":"desc-url"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-object-2"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-object-2"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/description":[
                                              {
                                                 "@value":"desc-object-2"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/url":[
                                              {
                                                 "@value":"desc-url-2"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-object-3"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-object-3"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/description":[
                                              {
                                                 "@value":"desc-object-3"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/url":[
                                              {
                                                 "@value":"desc-url-3"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-object-4"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-object-4"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/description":[
                                              {
                                                 "@value":"desc-object-4"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/url":[
                                              {
                                                 "@value":"desc-url-4"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-object-5"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-object-5"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/description":[
                                              {
                                                 "@value":"desc-object-5"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/url":[
                                              {
                                                 "@value":"desc-url-5"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-object-6"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-object-6 fjhkdshfsdjkfh djskfhsdjkfh sdjkfhsdjkfhs jkfdshjkfh sdkfjhsdjkfheuiwfhwefj dfbfewjfbewifu fewbfuwbfe"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/description":[
                                              {
                                                 "@value":"desc-object-6"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/url":[
                                              {
                                                 "@value":"desc-url-6"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-object-7"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-object-7"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/description":[
                                              {
                                                 "@value":"desc-object-7"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/url":[
                                              {
                                                 "@value":"desc-url-7"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-object-8"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-object-8"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/description":[
                                              {
                                                 "@value":"desc-object-8"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/url":[
                                              {
                                                 "@value":"desc-url-8"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-object-9"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-object-9"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/description":[
                                              {
                                                 "@value":"desc-object-9"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/url":[
                                              {
                                                 "@value":"desc-url-9"
                                              }
                                           ]
                                        }
                                     ],
                                     "https://w3id.org/edc/v0.0.1/ns/complexProperty":[
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-1"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-1"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-2"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-2"
                                              }
                                           ]
                                        },
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/identifier":[
                                              {
                                                 "@value":"id-3"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/name":[
                                              {
                                                 "@value":"name-3"
                                              }
                                           ]
                                        }
                                     ],
                                     "https://w3id.org/edc/v0.0.1/ns/objectProperty":[
                                        {
                                           "https://w3id.org/edc/v0.0.1/ns/id":[
                                              {
                                                 "@value":"id-single-object"
                                              }
                                           ],
                                           "https://w3id.org/edc/v0.0.1/ns/notation":[
                                              {
                                                 "@value":"notation-single-object"
                                              }
                                           ]
                                        }
                                     ],
                                     "https://w3id.org/edc/v0.0.1/ns/otherProperty":[
                                        {
                                           "@value":"other property"
                                        }
                                     ]
                                  }
                               ]
                            },
                            "http://purl.org/dc/terms/description":"<p>Asset 2008</p>",
                            "https://w3id.org/edc/v0.0.1/ns/id":"Asset 2008",
                            "https://w3id.org/edc/v0.0.1/ns/contenttype":"",
                            "https://w3id.org/edc/v0.0.1/ns/shortDescription":"Asset 2008",
                            "http://www.w3.org/ns/odrl/2/hasPolicy":[
                               {
                                  "contractOfferId":"YWx3YXlzLXRydWUtY29udHJhY3Q=:QXNzZXQgMjAwOA==:N2Y5ZDYwZGUtYjZkZC00NWQyLWIzZmQtNzBiYmY2NTQ2MzU1",
                                  "policy":{
                                     "policyJsonLd":"{\\"@id\\":\\"2b8de936-dab3-4e9e-98dc-ca9a2a09bf4e\\",\\"@type\\":\\"http://www.w3.org/ns/odrl/2/Set\\",\\"http://www.w3.org/ns/odrl/2/permission\\":[{\\"http://www.w3.org/ns/odrl/2/action\\":{\\"@id\\":\\"USE\\"},\\"http://www.w3.org/ns/odrl/2/constraint\\":[{\\"http://www.w3.org/ns/odrl/2/leftOperand\\":[{\\"@id\\":\\"ALWAYS_TRUE\\"}],\\"http://www.w3.org/ns/odrl/2/operator\\":[{\\"@id\\":\\"http://www.w3.org/ns/odrl/2/eq\\"}],\\"http://www.w3.org/ns/odrl/2/rightOperand\\":{\\"@value\\":\\"true\\"}}]}],\\"http://www.w3.org/ns/odrl/2/prohibition\\":[],\\"http://www.w3.org/ns/odrl/2/obligation\\":[]}",
                                     "expression":{
                                        "type":"CONSTRAINT",
                                        "constraint":{
                                           "left":"ALWAYS_TRUE",
                                           "operator":"EQ",
                                           "right":{
                                              "type":"STRING",
                                              "value":"true"
                                           }
                                        }
                                     },
                                     "errors":[
                                       \s
                                     ]
                                  }
                               },
                               {
                                  "contractOfferId":"aW5lc2RhdGEtY29udHJhY3Q=:QXNzZXQgMjAwOA==:YWY1OWNlNWItOGM4MC00OTE3LWFmNDYtZDc1MTI4MmNlODJj",
                                  "policy":{
                                     "policyJsonLd":"{\\"@id\\":\\"6a605ec9-3834-4450-99e3-5e7ce0731588\\",\\"@type\\":\\"http://www.w3.org/ns/odrl/2/Set\\",\\"http://www.w3.org/ns/odrl/2/permission\\":[{\\"http://www.w3.org/ns/odrl/2/action\\":{\\"@id\\":\\"USE\\"},\\"http://www.w3.org/ns/odrl/2/constraint\\":[{\\"http://www.w3.org/ns/odrl/2/leftOperand\\":[{\\"@id\\":\\"POLICY_EVALUATION_TIME\\"}],\\"http://www.w3.org/ns/odrl/2/operator\\":[{\\"@id\\":\\"http://www.w3.org/ns/odrl/2/lt\\"}],\\"http://www.w3.org/ns/odrl/2/rightOperand\\":{\\"@value\\":\\"2024-12-31T23:59:59+01:00\\"}}]}],\\"http://www.w3.org/ns/odrl/2/prohibition\\":[],\\"http://www.w3.org/ns/odrl/2/obligation\\":[]}",
                                     "expression":{
                                        "type":"CONSTRAINT",
                                        "constraint":{
                                           "left":"POLICY_EVALUATION_TIME",
                                           "operator":"LT",
                                           "right":{
                                              "type":"STRING",
                                              "value":"2024-12-31T23:59:59+01:00"
                                           }
                                        }
                                     },
                                     "errors":[
                                       \s
                                     ]
                                  }
                               }
                            ]
                         },
                         {
                            "@id":"another-asset",
                            "@type":"http://www.w3.org/ns/dcat#Dataset",
                            "http://www.w3.org/ns/dcat#distribution":[
                               {
                                  "@type":"http://www.w3.org/ns/dcat#Distribution",
                                  "http://purl.org/dc/terms/format":{
                                     "@id":"HttpData-PULL"
                                  },
                                  "http://www.w3.org/ns/dcat#accessService":{
                                     "@id":"6fc9cba4-6c08-4862-8fd5-54cc79825ea3",
                                     "@type":"http://www.w3.org/ns/dcat#DataService",
                                     "http://www.w3.org/ns/dcat#endpointDescription":"dspace:connector",
                                     "http://www.w3.org/ns/dcat#endpointUrl":"http://connector-c1:19194/protocol",
                                     "http://purl.org/dc/terms/terms":"dspace:connector",
                                     "http://purl.org/dc/terms/endpointUrl":"http://connector-c1:19194/protocol"
                                  }
                               },
                               {
                                  "@type":"http://www.w3.org/ns/dcat#Distribution",
                                  "http://purl.org/dc/terms/format":{
                                     "@id":"HttpData-PUSH"
                                  },
                                  "http://www.w3.org/ns/dcat#accessService":{
                                     "@id":"6fc9cba4-6c08-4862-8fd5-54cc79825ea3",
                                     "@type":"http://www.w3.org/ns/dcat#DataService",
                                     "http://www.w3.org/ns/dcat#endpointDescription":"dspace:connector",
                                     "http://www.w3.org/ns/dcat#endpointUrl":"http://connector-c1:19194/protocol",
                                     "http://purl.org/dc/terms/terms":"dspace:connector",
                                     "http://purl.org/dc/terms/endpointUrl":"http://connector-c1:19194/protocol"
                                  }
                               },
                               {
                                  "@type":"http://www.w3.org/ns/dcat#Distribution",
                                  "http://purl.org/dc/terms/format":{
                                     "@id":"AmazonS3-PULL"
                                  },
                                  "http://www.w3.org/ns/dcat#accessService":{
                                     "@id":"6fc9cba4-6c08-4862-8fd5-54cc79825ea3",
                                     "@type":"http://www.w3.org/ns/dcat#DataService",
                                     "http://www.w3.org/ns/dcat#endpointDescription":"dspace:connector",
                                     "http://www.w3.org/ns/dcat#endpointUrl":"http://connector-c1:19194/protocol",
                                     "http://purl.org/dc/terms/terms":"dspace:connector",
                                     "http://purl.org/dc/terms/endpointUrl":"http://connector-c1:19194/protocol"
                                  }
                               },
                               {
                                  "@type":"http://www.w3.org/ns/dcat#Distribution",
                                  "http://purl.org/dc/terms/format":{
                                     "@id":"AmazonS3-PUSH"
                                  },
                                  "http://www.w3.org/ns/dcat#accessService":{
                                     "@id":"6fc9cba4-6c08-4862-8fd5-54cc79825ea3",
                                     "@type":"http://www.w3.org/ns/dcat#DataService",
                                     "http://www.w3.org/ns/dcat#endpointDescription":"dspace:connector",
                                     "http://www.w3.org/ns/dcat#endpointUrl":"http://connector-c1:19194/protocol",
                                     "http://purl.org/dc/terms/terms":"dspace:connector",
                                     "http://purl.org/dc/terms/endpointUrl":"http://connector-c1:19194/protocol"
                                  }
                               }
                            ],
                            "https://w3id.org/edc/v0.0.1/ns/assetType":"machineLearning",
                            "http://purl.org/dc/terms/format":"",
                            "https://w3id.org/edc/v0.0.1/ns/version":"another-asset-version",
                            "http://www.w3.org/ns/dcat#keyword":"conn1,test",
                            "http://www.w3.org/ns/dcat#byteSize":"",
                            "https://w3id.org/edc/v0.0.1/ns/name":"another-asset-name",
                            "https://w3id.org/edc/v0.0.1/ns/participantId":"connector-c1",
                            "https://w3id.org/edc/v0.0.1/ns/assetData":{
                               "https://w3id.org/edc/v0.0.1/ns/machine-learning-vocabulary":[
                                  {
                                     "https://w3id.org/edc/v0.0.1/ns/title":[
                                        {
                                           "@value":"another-asset learning"
                                        }
                                     ],
                                     "https://w3id.org/edc/v0.0.1/ns/keyword":[
                                        {
                                           "@value":"learning"
                                        }
                                     ]
                                  }
                               ]
                            },
                            "http://purl.org/dc/terms/description":"<p>another-asset-long-desc</p>",
                            "https://w3id.org/edc/v0.0.1/ns/id":"another-asset",
                            "https://w3id.org/edc/v0.0.1/ns/contenttype":"",
                            "https://w3id.org/edc/v0.0.1/ns/shortDescription":"another-asset-short-desc",
                            "http://www.w3.org/ns/odrl/2/hasPolicy":[
                               {
                                  "contractOfferId":"YWx3YXlzLXRydWUtY29udHJhY3Q=:YW5vdGhlci1hc3NldA==:YTc3NThmODItNWMzNS00ZDI3LTgwNmQtODNkZGMzZWY4OGEw",
                                  "policy":{
                                     "policyJsonLd":"{\\"@id\\":\\"feca33c3-b915-4b4e-a606-8055b1eb6499\\",\\"@type\\":\\"http://www.w3.org/ns/odrl/2/Set\\",\\"http://www.w3.org/ns/odrl/2/permission\\":[{\\"http://www.w3.org/ns/odrl/2/action\\":{\\"@id\\":\\"USE\\"},\\"http://www.w3.org/ns/odrl/2/constraint\\":[{\\"http://www.w3.org/ns/odrl/2/leftOperand\\":[{\\"@id\\":\\"ALWAYS_TRUE\\"}],\\"http://www.w3.org/ns/odrl/2/operator\\":[{\\"@id\\":\\"http://www.w3.org/ns/odrl/2/eq\\"}],\\"http://www.w3.org/ns/odrl/2/rightOperand\\":{\\"@value\\":\\"true\\"}}]}],\\"http://www.w3.org/ns/odrl/2/prohibition\\":[],\\"http://www.w3.org/ns/odrl/2/obligation\\":[]}",
                                     "expression":{
                                        "type":"CONSTRAINT",
                                        "constraint":{
                                           "left":"ALWAYS_TRUE",
                                           "operator":"EQ",
                                           "right":{
                                              "type":"STRING",
                                              "value":"true"
                                           }
                                        }
                                     },
                                     "errors":[
                                       \s
                                     ]
                                  }
                               },
                               {
                                  "contractOfferId":"aW5lc2RhdGEtY29udHJhY3Q=:YW5vdGhlci1hc3NldA==:ZmRjYWM5MjQtNDExOC00N2UzLWE2YmUtNzAzNjY4NTEzNmFi",
                                  "policy":{
                                     "policyJsonLd":"{\\"@id\\":\\"1148f5dc-b54a-4b2f-9e0a-bf98c340d579\\",\\"@type\\":\\"http://www.w3.org/ns/odrl/2/Set\\",\\"http://www.w3.org/ns/odrl/2/permission\\":[{\\"http://www.w3.org/ns/odrl/2/action\\":{\\"@id\\":\\"USE\\"},\\"http://www.w3.org/ns/odrl/2/constraint\\":[{\\"http://www.w3.org/ns/odrl/2/leftOperand\\":[{\\"@id\\":\\"POLICY_EVALUATION_TIME\\"}],\\"http://www.w3.org/ns/odrl/2/operator\\":[{\\"@id\\":\\"http://www.w3.org/ns/odrl/2/lt\\"}],\\"http://www.w3.org/ns/odrl/2/rightOperand\\":{\\"@value\\":\\"2024-12-31T23:59:59+01:00\\"}}]}],\\"http://www.w3.org/ns/odrl/2/prohibition\\":[],\\"http://www.w3.org/ns/odrl/2/obligation\\":[]}",
                                     "expression":{
                                        "type":"CONSTRAINT",
                                        "constraint":{
                                           "left":"POLICY_EVALUATION_TIME",
                                           "operator":"LT",
                                           "right":{
                                              "type":"STRING",
                                              "value":"2024-12-31T23:59:59+01:00"
                                           }
                                        }
                                     },
                                     "errors":[
                                       \s
                                     ]
                                  }
                               }
                            ]
                         }
                      ]
                   }
                ]
                """;

/*                JsonArray response =  service.searchPagination(querySpec).orElseThrow(exceptionMapper(QuerySpec.class, null)).stream()
                .map(it -> transformerRegistry.transform(it, JsonObject.class))
                .peek(r -> r.onFailure(f -> monitor.warning(f.getFailureDetail()))).filter(Result::succeeded)
                .map(Result::getContent)
                .map(this::modifyPolicyResponse)
                .collect(toJsonArray());*/

        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonArray jsonArray = jsonReader.readArray();
        jsonReader.close();

        return jsonArray;
    }

    private JsonObject modifyPolicyResponse(JsonObject originalResponse) {
        if (originalResponse.get(Prop.Dcat.DATASET) != null) {
            JsonArray datasets = originalResponse.getJsonArray(Prop.Dcat.DATASET);
            JsonArrayBuilder datasetArrayBuilder = Json.createArrayBuilder();

            for (JsonValue datasetElement : datasets) {

                JsonObject dataset = datasetElement.asJsonObject();

                if (dataset.getJsonArray(Prop.Odrl.HAS_POLICY) != null) {
                    var contractOffers = JsonLdUtils.listOfObjects(dataset, Prop.Odrl.HAS_POLICY).stream()
                            .map(this::buildContractOffer)
                            .map(this::buildUiContractOffer)
                            .toList();

                    JsonArrayBuilder uiContractOffersJsonBuilder = Json.createArrayBuilder();
                    contractOffers.forEach(offer -> uiContractOffersJsonBuilder.add(transformerRegistry.transform(offer, JsonObject.class).getContent()));

                    JsonArray uiContractOffersJson = uiContractOffersJsonBuilder.build();

                    JsonObject modifiedDatasetJson = Json.createObjectBuilder(dataset)
                            .remove(Prop.Odrl.HAS_POLICY)
                            .add(Prop.Odrl.HAS_POLICY, uiContractOffersJson)
                            .build();

                    datasetArrayBuilder.add(modifiedDatasetJson);
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
        typeArrayBuilder.add("http://www.w3.org/ns/odrl/2/Set");

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
