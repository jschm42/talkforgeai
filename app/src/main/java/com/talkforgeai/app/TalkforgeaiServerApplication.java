package com.talkforgeai.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.talkforgeai.backend", "com.talkforgeai.service"})
@EnableJpaRepositories("com.talkforgeai.backend")
@EntityScan("com.talkforgeai.backend")
public class TalkforgeaiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalkforgeaiServerApplication.class, args);
    }

}
