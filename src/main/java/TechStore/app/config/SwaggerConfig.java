package TechStore.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Configuration
@OpenAPIDefinition(info = @Info(title = "The document of the API of the MarketPlace", version = "v1"))
public class SwaggerConfig {
    ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("The document of the API of the MarketPlace")
                .description("This is all of APIs for marketplace project").license("Apache 2.0")
                .licenseUrl("http://apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("http://antada.com.vn").version("1.0.0")
                .contact(new Contact("antada", "http://antada.com", "marketplace@antada.com.vn")).build();
    }

    public Docket api() {
        return new Docket(DocumentationType.OAS_30).select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any()).build().apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(apiKey()))
                .securityContexts(Collections.singletonList(securityContext()))
                .useDefaultResponseMessages(false);
    }
    private ApiKey apiKey() {
        return new ApiKey("apiKey", "x-access-token", "header");

    }
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("apiKey", authorizationScopes));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .operationSelector(operationContext -> operationContext.requestMappingPattern().matches("/api/*"))
                .build();
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder().clientId("api-marketplace-client-id")
                .clientSecret("api-client-secret").realm("marketplace-realm").appName("api-market")
                .scopeSeparator(",").additionalQueryStringParams(null)
                .useBasicAuthenticationWithAccessCodeGrant(false).build();
    }
}
