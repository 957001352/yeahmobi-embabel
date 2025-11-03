package com.yeahmobi.embabel;

import com.embabel.agent.config.annotation.EnableAgents;
import com.embabel.agent.config.annotation.LoggingThemes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@EnableAgents(
        loggingTheme = LoggingThemes.STAR_WARS
//        ,mcpServers = {McpServers.DOCKER_DESKTOP}
)
@ConfigurationPropertiesScan(
        basePackages = {
                "com.yeahmobi.embabel"
        }
)
public class EmbabelMcpClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmbabelMcpClientApplication.class, args);
    }
}

