package com.consultadd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
public class SoftPhoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftPhoneApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
            }
        };
    }

    //    @Bean
    //    public Docket api() {
    //        return new Docket(DocumentationType.SWAGGER_2)
    //                .select()
    //                .apis(RequestHandlerSelectors.basePackage("com.consultadd.controller"))
    //                .paths(PathSelectors.any())
    //                .build()
    //                .pathMapping("/");
    //    }
}
