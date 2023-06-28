package com.talkforgeai.talkforgeaiserver.controller;

import com.talkforgeai.talkforgeaiserver.properties.ElevenlabsProperties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

//@EnableAsync
@RestController
public class AudioStreamController {
    private static final String VOICE_ID = "21m00Tcm4TlvDq8ikWAM";

    private final OkHttpClient httpClient;

    private final ElevenlabsProperties properties;
    String textLine1 = "Good morning, good afternoon, good evening to everyone out there in radioland! This is your favorite on-air personality, John Doe, coming to you live from the heart of the music universe, Radio Bum-Bum. We're keeping the airwaves alive with the sound of music that stirs the soul, tickles the mind, and sets your feet to dancing.";

    String textLine2 = "Now, we've got a song queued up that's not just a song, folks, it's a phenomenon. From the sultry rhythms to the tantalizing lyrics, this track is a journey, a roller coaster ride of sounds, emotions, and memories. So buckle up and prepare to be taken to a new dimension of audio excellence.";

    String textLine3 = "Good morning, good afternoon, good evening to everyone out there in radioland! This is your favorite on-air personality, John Doe, coming to you live from the heart of the music universe, Radio Bum-Bum. We're keeping the airwaves alive with the sound of music that stirs the soul, tickles the mind, and sets your feet to dancing.";

    public AudioStreamController(ElevenlabsProperties properties, OkHttpClient httpClient) {
        this.properties = properties;
        this.httpClient = httpClient;
    }

    // @Async("audioSreamTaskExecutor")
    @GetMapping(value = "/streamaudio", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Callable<StreamingResponseBody> streamAudio() throws Exception {
        return () -> outputStream -> {
            speak(outputStream, textLine1);
            speak(outputStream, textLine2);
            speak(outputStream, textLine3);
        };
    }

    private void speak(OutputStream outputStream, String text) throws IOException {
        okhttp3.MediaType JSON = okhttp3.MediaType.get("application/json; charset=utf-8");
        String json = "{\n" +
                "  \"text\": \"" + text + "\",\n" +
                "  \"model_id\": \"eleven_monolingual_v1\",\n" +
                "  \"voice_settings\": {\n" +
                "    \"stability\": 0.5,\n" +
                "    \"similarity_boost\": 0.5\n" +
                "  }\n" +
                "}";

        Request request = new Request.Builder()
                .url(properties.apiUrl() + "/v1/text-to-speech/" + VOICE_ID + "/stream")
                .post(RequestBody.create(json, JSON))
                .addHeader("Accept", "audio/mpeg")
                .addHeader("Content-Type", "application/json")
                .addHeader("xi-api-key", properties.apiKey())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = response.body().source().read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

}
