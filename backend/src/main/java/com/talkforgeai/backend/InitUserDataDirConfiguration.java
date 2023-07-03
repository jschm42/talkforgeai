package com.talkforgeai.backend;

import com.talkforgeai.backend.storage.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitUserDataDirConfiguration {
    Logger logger = LoggerFactory.getLogger(InitUserDataDirConfiguration.class);

    @Bean
    CommandLineRunner initDataDirectory(FileStorageService fileStorageService) {
        return args -> {
            fileStorageService.createDataDirectories();
            fileStorageService.copyImagesToHomeDirectory();
        };
    }

}
