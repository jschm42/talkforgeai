package com.talkforgeai.talkforgeaiserver.service;

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

@Service
public class OpenAIImageService {
    private final OpenAiService service;

    public OpenAIImageService(OpenAiService service) {
        this.service = service;
    }

    public ImageResult submit(String imagePrompt) {
        CreateImageRequest imageRequest = new CreateImageRequest();
        imageRequest.setPrompt(imagePrompt);
        imageRequest.setN(1);
        imageRequest.setSize("512x512");
        imageRequest.setResponseFormat("url");
        return service.createImage(imageRequest);
    }
}
