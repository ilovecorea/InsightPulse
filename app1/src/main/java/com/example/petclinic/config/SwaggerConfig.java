package com.example.petclinic.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI customOpenAPI() {

        return new OpenAPI().components(new Components()).info(new Info()
                .title("REST Petclinic backend Api Documentation").version("1.0")
                .termsOfService("Petclinic backend terms of service")
                .description(
                        "This is REST API documentation of the Spring Petclinic backend. If authentication is enabled, when calling the APIs use admin/admin")
                .license(swaggerLicense()).contact(swaggerContact()));
    }

    private Contact swaggerContact() {
        Contact petclinicContact = new Contact();
        petclinicContact.setName("Vitaliy Fedoriv");
        petclinicContact.setEmail("vitaliy.fedoriv@gmail.com");
        petclinicContact.setUrl("https://github.com/spring-petclinic/spring-petclinic-rest");
        return petclinicContact;
    }

    private License swaggerLicense() {
        License petClinicLicense = new License();
        petClinicLicense.setName("Apache 2.0");
        petClinicLicense.setUrl("http://www.apache.org/licenses/LICENSE-2.0");
        petClinicLicense.setExtensions(Collections.emptyMap());
        return petClinicLicense;
    }

}
