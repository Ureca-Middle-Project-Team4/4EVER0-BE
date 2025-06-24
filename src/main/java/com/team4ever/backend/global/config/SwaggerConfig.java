package com.team4ever.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("cookieAuth", new SecurityScheme()
								.type(SecurityScheme.Type.APIKEY)
								.in(SecurityScheme.In.COOKIE)
								.name("ACCESS_TOKEN")  // Spring 세션 쿠키 이름
						)
				)
				.addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
				.info(new Info()
						.title("4EVER0 API")
						.description("4EVER0 Swagger 문서")
						.version("v1.0"));
	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("public-api")
				.pathsToMatch("/**")
				.build();
	}
}
