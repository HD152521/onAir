package com.sejong.project.onair.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("onAir") // API의 제목
                .description("공기 청정기 프로젝트") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}

//JWT 사용할 경우
//@Configuration
//public class SwaggerConfig {
//    @Bean
//    public OpenAPI openAPI() {
//        String jwt = "JWT";
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
//        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
//                .name(jwt)
//                .type(SecurityScheme.Type.HTTP)
//                .scheme("bearer")
//                .bearerFormat("JWT")
//        );
//        return new OpenAPI()
//                .components(new Components())
//                .info(apiInfo())
//                .addSecurityItem(securityRequirement)
//                .components(components);
//    }
//
//    private Info apiInfo() {
//        return new Info()
//                .title("API Test") // API의 제목
//                .description("Let's practice Swagger UI") // API에 대한 설명
//                .version("1.0.0"); // API의 버전
//    }
//}