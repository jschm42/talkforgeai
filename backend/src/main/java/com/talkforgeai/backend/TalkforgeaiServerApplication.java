package com.talkforgeai.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.talkforgeai.backend", "com.talkforgeai.service"})
public class TalkforgeaiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalkforgeaiServerApplication.class, args);
    }

}
