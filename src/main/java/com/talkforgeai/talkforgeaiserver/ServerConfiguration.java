package com.talkforgeai.talkforgeaiserver;

import com.talkforgeai.talkforgeaiserver.properties.OpenAIProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
public class ServerConfiguration {

}
