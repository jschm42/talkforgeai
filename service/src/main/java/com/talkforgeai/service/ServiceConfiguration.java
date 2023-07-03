package com.talkforgeai.service;

import com.talkforgeai.service.properties.ElevenlabsProperties;
import com.talkforgeai.service.properties.OpenAIProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({OpenAIProperties.class, ElevenlabsProperties.class})
public class ServiceConfiguration {

}
