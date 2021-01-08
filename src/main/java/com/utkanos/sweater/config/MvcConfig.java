package com.utkanos.sweater.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.Properties;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    //дополнительно мы добавляем на адрес /login новый шаблонизатор login
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations(String.format("file://%s/", uploadPath));
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }


    //определяем мэппинг для ошибок
    @Bean(name="simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver
    createSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver r =
                new SimpleMappingExceptionResolver();

        Properties mappings = new Properties();
        mappings.setProperty("PermissionDeniedException",      "exceptions/permissionDeniedError");
        mappings.setProperty("FileSizeLimitExceededException", "exceptions/fileSizeError"); //для такого исключения будет отображаться своя страничка
        mappings.setProperty("MaxUploadSizeExceededException", "exceptions/fileSizeError");

        r.setExceptionMappings(mappings);  // None by default
        r.setDefaultErrorView("error");    // No default
        r.setExceptionAttribute("ex");     // Default is "exception"
        return r;
    }
}
