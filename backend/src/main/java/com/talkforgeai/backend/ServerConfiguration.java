package com.talkforgeai.backend;

import com.talkforgeai.backend.properties.ElevenlabsProperties;
import com.talkforgeai.backend.properties.OpenAIProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({OpenAIProperties.class, ElevenlabsProperties.class})
public class ServerConfiguration {

}
