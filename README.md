# TalkforgeAI - AI-Powered Chatbot Creation Tool

## Overview
TalkforgeAI is an AI-powered chat software that facilitates the creation of personalized chatbots using the ChatGPT platform. It allows users to tailor the chat experience by creating individual AI profiles, known as Personas, each with its own personality profile and avatar image. In addition to text input, prompts can be generated via voice input, and the AI's responses can be delivered through browser-based or Elevenlabs text-to-speech output.

### Prerequisites
- An OpenAI API key is required. To obtain one, visit the [OpenAI website](https://openai.com/) and follow the instructions for API key generation.
- For optional Elevenlabs speech output, an API key is also necessary. Please visit the [Elevenlabs website](https://elevenlabs.io/) for instructions.

## Features

- Image generation through DALL-E 2 directly in chat, compatible with both GPT-3.5 and GPT-4 models.
- Access to all relevant GPT models:
    - gpt-4
    - gpt-4-32k
    - gpt-3.5-turbo
    - gpt-3.5-turbo-16k

- Creation of PlantUML diagrams, such as class, sequence, or activity diagrams.
- Graphical enhancement of generated LaTeX code.
- Custom instruction definitions.
- Code highlighting for generated source code.
- Docker container support for simplified deployment.

## Upcoming Features

- Multi-user support with access control for the features of each Persona, allowing for the assignment and sharing of Personas among users.
- Image interpretation, initially supported by Huggingface models.
- Reading and evaluating web links.
- Memory feature to store critical information in the database and retrieve it as needed, allowing for the persistence of information beyond the maximum token limit.
- Function support for tasks such as sending emails, querying calendar data, etc.

## Installation

### Building the Project
- Ensure Java 19 or higher is installed on your system and the JAVA_HOME environment variable ist set correctly.

### Starting the Server
- To run from a JAR file, use the following command with the appropriate API keys and data directory specified:

      java -jar ./talkforgeai-0.1.0.jar --server.port=8090 --openai.api-key=[your OpenAI API Key] --elevenlabs.api-key=[you Elevenlabs API-Key] --talkforgeai.datadir=[your TalkforgeAI data directory]

- Alternatively, use a properties file for configuration:

      java -jar ./talkforgeai-0.1.0.jar --spring.config.additional-location=./talkforgeai.properties

Example properties file:

    server.port=8090
    openai.api-key=[your OpenAI API Key]
    elevenlabs.api-key=[you Elevenlabs API-Key]
    talkforgeai.datadir=[your TalkforgeAI data directory]

### Using Docker
- Deploy using Docker with the following command:

      docker run -d -p 8099:8099 -v /usr/local/talkforgeai/talkforgeai.properties:/usr/local/talkforgeai/talkforgeai.properties:ro -v /home/jean/talkforgeai-data:/data jschm2019/talkforgeai

## Building from Source
- After cloning the repository, building is straightforward with Maven:

        ./mvn clean package

*Note: Replace placeholder text (e.g., [your OpenAI API Key]) with actual API keys and directory paths as required for your setup.*

## Contributing
Contributions are welcome! Please read our contributing guidelines for how to proceed.

## License
TalkforgeAI is released under the Apache License 2.0. See the [LICENSE](LICENSE) file for more details.

## Support
For support and queries, please open an issue on the GitHub repository or contact the maintainers directly.

---

This README is a basic guide for getting started with TalkforgeAI. For more detailed documentation, please refer to the project's documentation site.
