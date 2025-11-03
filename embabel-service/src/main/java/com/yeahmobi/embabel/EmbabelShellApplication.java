package com.yeahmobi.embabel;

import com.embabel.agent.config.annotation.EnableAgents;
import com.embabel.agent.config.annotation.LoggingThemes;
import com.embabel.agent.config.annotation.McpServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@EnableAgents(
        loggingTheme = LoggingThemes.SEVERANCE
//        ,mcpServers = {McpServers.DOCKER_DESKTOP}
)
@ConfigurationPropertiesScan(
        basePackages = {
                "com.yeahmobi.embabel"
        }
)
public class EmbabelShellApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmbabelShellApplication.class, args);
    }
}
