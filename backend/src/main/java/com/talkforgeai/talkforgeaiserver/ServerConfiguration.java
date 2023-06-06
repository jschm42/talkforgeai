package com.talkforgeai.talkforgeaiserver;

import com.talkforgeai.talkforgeaiserver.properties.ElevenlabsProperties;
import com.talkforgeai.talkforgeaiserver.properties.OpenAIProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({OpenAIProperties.class, ElevenlabsProperties.class})
public class ServerConfiguration {

}
