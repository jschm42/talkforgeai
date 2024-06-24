# TalkforgeAI - AI-Powered Chatbot Creation Tool

## Overview

TalkforgeAI is an advanced AI-powered chat software designed to facilitate the creation of highly
personalized chatbots, now referred to as "Assistants". This software integrates with multiple AI
platforms including OpenAI, Mistral, Anthropic, and Ollama, supporting both cloud-based and local AI
models. Users can customize each Assistant with unique personality traits and an avatar image. The
chatbots can interact via text or voice inputs, with responses delivered through browser-based or
Elevenlabs text-to-speech output.

**Note:** This project is still in the early stages of development and may not yet be suitable for
production use.

### Prerequisites

- An API key from any supported AI provider (OpenAI, Mistral, Anthropic, Ollama) is required.
- For optional Elevenlabs speech output, an API key is also necessary.

## Features

- 🤖 Create and manage multiple, GPT-style, chatbot Assistants with custom personalities using AI
  providers like OpenAI, Mistral, Anthropic, and Ollama.
- 🎨 Each assistant can be customized with unique personality traits, an avatar image and one of the
  supported AI models.
- ☁️ Support for both cloud-based and local AI models, enhancing flexibility and control over data
  processing.
- 🗣️ Support for text and voice input, with text-to-speech output through Elevenlabs or the browser
  build-in MS Speech API.
- 🖼️ Generate a profile picture for your Assistant directly inside the app.
- 🎭 Image generation through DALL-E 3, compatible with various GPT models.
- 📊 Advanced diagram creation with PlantUML and graphical enhancement of LaTeX code.
- 💻 Code highlighting for generated source code.
- 📦 Docker container support for simplified deployment.

## Upcoming Features

- Enhanced multi-user support with comprehensive access controls for managing Assistants.
- Support for uploading and interpreting files (PDF, TXT, etc.).
- Persistent memory capabilities for storing essential information.
- Support for Vector Storage like Apache Cassandra, Redis, Neo4j and local storage.
- Expanded task functions like email automation and calendar queries.
- Integration with additional AI providers and services.
- Enhanced image generation capabilities with OpenAI and StablilityAI .

## Installation

### Building the Project

- Ensure Java 21 or higher is installed on your system and the JAVA_HOME environment variable is
  correctly set.

### Starting the Server

To run from a JAR file, include API keys and specify the data directory as shown in this example:

    java -jar ./talkforgeai-0.1.0.jar --server.port=8090 --spring.ai.openai.api-key=[your API Key] --talkforgeai.datadir=[your TalkforgeAI data directory]

Alternatively, configure using a properties file:

    java -jar ./talkforgeai-0.1.0.jar --spring.config.additional-location=./talkforgeai.properties

Example properties file (if you don't want to use a model, leave property empty or remove it from
the file):

    spring.ai.openai.api-key=[your API Key]
    spring.ai.mistralai.api-key=[your API Key]
    spring.ai.anthropic.api-key=[your API Key]
    spring.ai.ollama.base-url=http://localhost:11434
    elevenlabs.api-key=[your API Key]
    server.port=8090
    talkforgeai.datadir=[your TalkforgeAI data directory]

Available properties:

| Property                                    | Default Value                                       | Description                                          |
|---------------------------------------------|-----------------------------------------------------|------------------------------------------------------|
| `server.port`                               | `8090`                                              | The port on which the server will run.               |
| `talkforgeai.datadir`                       | `${user.home}/.talkforgeai`                         | The directory where TalkforgeAI data will be stored. |
| `spring.servlet.multipart.max-file-size`    | `5MB`                                               | The maximum file size for multipart file uploads.    |
| `spring.servlet.multipart.max-request-size` | `5MB`                                               | The maximum request size for multipart file uploads. |
| `spring.ai.mistralai.api-key`               | -                                                   | The API key for Mistral AI.                          |
| `spring.ai.ollama.base-url`                 | `http://localhost:11434`                            | The base URL for Ollama AI.                          |
| `spring.ai.anthropic.api-key`               | -                                                   | The API key for Anthropic AI.                        |
| `spring.ai.anthropic.version`               | `2023-06-01`                                        | The version of Anthropic AI.                         |
| `elevenlabs.api-key`                        | -                                                   | The API key for Elevenlabs.                          |
| `elevenlabs.api-url`                        | `https://api.elevenlabs.io`                         | The base URL for Elevenlabs API.                     |
| `spring.datasource.url`                     | `jdbc:sqlite:${talkforgeai.datadir}/talkforgeai.db` | The URL for the SQLite database.                     |
| `spring.datasource.driverClassName`         | `org.sqlite.JDBC`                                   | The driver class name for the SQLite database.       |
| `spring.jpa.database-platform`              | `org.hibernate.community.dialect.SQLiteDialect`     | The dialect for the SQLite database.                 |
| `logging.level.com.talkforgeai`             | `INFO`                                              | The log level for TalkforgeAI.                       |

### Using Docker

Deploy using Docker with commands customized for your local settings and environment variables
managed via a `.env` file:

1. **Create an `.env` file** on your system where you'll specify all necessary environment variables
   for the Docker container. Example content for your `.env` file:

    ```plaintext
    SPRING_AI_OPENAI_API_KEY=[your OpenAI API Key]
    SPRING_AI_MISTRALAI_API_KEY=[your Mistral AI API Key]
    SPRING_AI_ANTHROPIC_API_KEY=[your Anthropic API Key]
    SPRING_AI_OLLAMA_BASE_URL=http://localhost:11434
    ELEVENLABS_API_KEY=[your ElevenLabs API Key]
    ```

2. **Run the Docker container** using the `.env` file to provide the environment variables:

    ```bash
    docker run -d -p [your local port]:8090 --env-file /path/to/your/.env file talkforgeai/talkforgeai:latest
    ```

   Replace `/path/to/your/.env` with the actual path to your `.env` file and `[your local port]`
   with the port number you want to use on your host.

   For example, if your `.env` file is located at `/home/user/talkforgeai/.env` and you want to run
   your container on local port 8090, the command would be:

    ```bash
    docker run -d -p 8090:8090 --env-file /home/user/talkforgeai/.env talkforgeai/talkforgeai:latest
    ```

This approach allows you to manage environment variables centrally within the `.env` file,
simplifying the Docker command and enhancing security by avoiding the direct listing of sensitive
information in the command line or scripts.

## Building from Source

After cloning the repository, build the project using Maven:

    mvn clean package

*Note: Replace placeholder text (e.g., [your API Key]) with actual API keys and directory paths as
needed.*

## Contributing

Contributions are welcome! Review our contributing guidelines to get started.

## License

TalkforgeAI is released under the Apache License 2.0. See the LICENSE file for more details.

## Dependencies and Licenses

This project uses several open-source libraries as dependencies. The direct dependencies and their
licenses are listed in the DEPENDENCIES file.

Please note that these dependencies may have their own transitive dependencies with various
licenses. Users of this project should be aware of these licenses when using the software.

To view a full list of dependencies and their licenses, you can use the following Maven command:

mvn license:aggregate-add-third-party

## Support

For support and inquiries, please open an issue on the GitHub repository or contact the maintainers
directly.
