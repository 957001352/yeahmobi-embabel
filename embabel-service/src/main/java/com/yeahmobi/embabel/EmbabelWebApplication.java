package com.yeahmobi.embabel;

import com.embabel.agent.config.annotation.EnableAgents;
import com.embabel.agent.config.annotation.LocalModels;
import com.embabel.agent.config.annotation.LoggingThemes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@EnableAgents(
        loggingTheme = LoggingThemes.COLOSSUS
//        ,mcpServers = {McpServers.DOCKER_DESKTOP}
)
@ConfigurationPropertiesScan(
        basePackages = {
                "com.yeahmobi.embabel"
        }
)
public class EmbabelWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmbabelWebApplication.class, args);
    }
}

