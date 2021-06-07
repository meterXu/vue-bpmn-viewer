package com.sipsd.flow.config;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * swagger配置项
 * @author hul
 *
 */

@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/static/swagger/");
    }

    //工作流服务接口
    @Bean(value = "sysApi")
    @Order(value = 1)
    public Docket defaultApi() {
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        List<Parameter> parameters = Lists.newArrayList();
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(sysInfo()).groupName("工作流服务接口").select()
                .apis(RequestHandlerSelectors.basePackage("com.sipsd.flow.rest.api"))
                //加了ApiOperation注解的类，才生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any()).build().globalOperationParameters(parameters);
        //.securityContexts(Lists.newArrayList(securityContext(), securityContext()));
        //.securitySchemes(Lists.<SecurityScheme>newArrayList(apiKey()));
    }

    //旧版兼容工作流服务接口
    @Bean(value = "sysOldApi")
    @Order(value = 2)
    public Docket defaultOldApi() {
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        List<Parameter> parameters = Lists.newArrayList();
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(sysInfo()).groupName("旧版工作流服务接口").select()
                .apis(RequestHandlerSelectors.basePackage("com.sipsd.flow.rest.v3.api"))
                //加了ApiOperation注解的类，才生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any()).build().globalOperationParameters(parameters);
        //.securityContexts(Lists.newArrayList(securityContext(), securityContext()));
        //.securitySchemes(Lists.<SecurityScheme>newArrayList(apiKey()));
    }

    private ApiInfo sysInfo() {
        return new ApiInfoBuilder().title("swagger-bootstrap-ui-demo RESTful APIs")
                .description("swagger-bootstrap-ui-demo RESTful APIs")
                .termsOfServiceUrl("http://192.168.126.19:9763/")
                .version("1.0").build();
    }





    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/.*")).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("BearerToken", authorizationScopes));
    }

}