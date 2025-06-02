package com.markin.togglefox;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "ToggleFox API",
                version = "1.0.0",
                description = "Enterprise-grade feature flag management system built with Clean Architecture",
                contact = @Contact(
                        name = "ToggleFox Team",
                        url = "https://github.com/jonamarkin/togglefox"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        )
)
public class ToggleFoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToggleFoxApplication.class, args);
    }
}