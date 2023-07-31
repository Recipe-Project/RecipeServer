package com.recipe.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        RequestParameter parameter = new RequestParameterBuilder()
                .name("X-ACCESS-TOKEN")
                .in(ParameterType.HEADER)
                .query(p -> p.model(m -> m.scalarModel(ScalarType.STRING)))
                .description("Authorization Header")
                .required(false)
                .build();

        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(parameter);

        return new Docket(DocumentationType.SWAGGER_2)
                .globalRequestParameters(parameters)
                .useDefaultResponseMessages(false)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        return consumes;
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("레시피 저장소 API 명세서")
                .description("레시피 저장소 API 목록")
                .version("1.0")
                .build();
    }
}
