package eu.urbanage.GeoDataExtractor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://gisviewer.santander.dev.ecosystem-urbanage.eu",
                        "https://gisviewer.santander.ecosystem-urbanage.eu",
                        "http://localhost:4200",
                        "https://geocacher-dev.urbreath.tech",
                        "https://geocacher-api-dev.urbreath.tech"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
