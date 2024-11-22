package br.com.fiap.jadv.prospeco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class DateFormatterConfig {

    @Bean
    public DateTimeFormatter dateFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
}
