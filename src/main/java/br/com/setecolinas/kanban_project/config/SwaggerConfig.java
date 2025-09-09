package br.com.setecolinas.kanban_project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Kanban - Desafio Técnico Backend")
                        .version("1.0.0")
                        .description("Documentação da API para gerenciamento de Projetos, " +
                                "Responsáveis e Secretarias com Kanban e indicadores."));
    }
}
