package org.example;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Book API",
                description = "lấy danh sách, tạo, cập nhật, xóa",
                version = "1.0.0"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local server")
        },
        security = {
                @SecurityRequirement(name = "BearerAuth")
        },
        tags = {
                @Tag(name = "Book", description = "Các API quản lý sách")
        },
        externalDocs = @ExternalDocumentation(
                description = "Tài liệu OpenAPI tham khảo",
                url = "https://swagger.io/specification/"
        )
)
@SpringBootApplication
public class SoaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoaApplication.class, args);
    }
}