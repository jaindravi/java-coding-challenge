package com.crewmeister.cmcodingchallenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomConfig(){
        return new OpenAPI()
                .info(
                        new Info().title("Foreign Exchange Rate Service API")
                                .description("API Documentation for Foreign Exchange Rate Service")
                                .version("1.0.0"))
                .tags(Arrays.asList(
                        new Tag().name("FX RATE")
                ));
    }
}
