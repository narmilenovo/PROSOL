package com.example.user_management.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileStorageProperties {

    private String uploadDir;
}