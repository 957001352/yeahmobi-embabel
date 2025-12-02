package com.yeahmobi.embabel;

import com.embabel.agent.config.annotation.EnableAgents;
import com.embabel.agent.config.annotation.LoggingThemes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@EnableAgents(
        loggingTheme = LoggingThemes.COLOSSUS
        ,mcpServers = {"risk-mcp"}
)
@ConfigurationPropertiesScan(
        basePackages = {
                "com.yeahmobi.embabel"
        }
)
public class EmbabelMcpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmbabelMcpServerApplication.class, args);
    }
}

