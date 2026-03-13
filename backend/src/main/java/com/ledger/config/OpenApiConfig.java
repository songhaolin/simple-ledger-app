package com.ledger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ledgerOpenAPI() {
        // JWT 安全方案
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("JWT");

        // 服务器配置
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("本地开发环境");

        // API 信息
        Info info = new Info()
                .title("简账APP API")
                .version("1.0.0")
                .description("简账APP 后端API接口文档\n\n" +
                        "## 认证说明\n\n" +
                        "除登录和注册接口外，所有接口都需要在请求头中携带JWT Token：\n\n" +
                        "`Authorization: Bearer {token}`\n\n" +
                        "## 错误码说明\n\n" +
                        "| 错误码 | 说明 |\n" +
                        "|--------|------|\n" +
                        "| 1000 | 成功 |\n" +
                        "| 1001 | 未知错误 |\n" +
                        "| 1002 | 参数错误 |\n" +
                        "| 1003 | 数据不存在 |\n" +
                        "| 1004 | 未授权（Token无效或过期）|\n" +
                        "| 1005 | 数据已存在 |\n" +
                        "| 1006 | 权限不足 |\n" +
                        "| 4010 | 用户名或密码错误 |\n" +
                        "| 4011 | Token无效或过期 |\n" +
                        "| 4012 | Token不能为空 |\n" +
                        "| 4013 | 请求头参数不能为空 |")
                .contact(new Contact()
                        .name("菜菜子")
                        .email("support@simpleledger.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .addSecurityItem(securityRequirement)
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("JWT", securityScheme));
    }
}
