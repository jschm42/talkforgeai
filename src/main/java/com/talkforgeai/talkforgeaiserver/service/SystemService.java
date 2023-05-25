package com.talkforgeai.talkforgeaiserver.service;

import org.springframework.stereotype.Service;

@Service
public class SystemService {
    public static final String IMAGE_GEN_SYSTEM = "Create an image prompt in English to generate an image whenever instructed to output an image. " +
        "The prompt should inspire creativity and imagination, and be vividly descriptive. Describe the scene from the view of a third-person stranger. For example, " +
        "\"A painting of a stormy sea with a lighthouse in the distance,\" or \"A photograph of a city street at night, illuminated by neon lights.\". " +
        "Place the prompt between the following tags: <image-prompt></image-prompt>.";
}
