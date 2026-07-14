package com.javaee.blog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI aaBlogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AA_Blog API")
                        .version("1.0")
                        .description("个人博客接口文档"));
    }
}
