package com.talkforgeai.talkforgeaiserver.dto.ws;

import java.util.UUID;

public class WSChatFunctionMessage extends WebsocketMessage {

    private String functionName;
    private String functionArguments;

    public WSChatFunctionMessage(UUID sessionId, String functionName, String functionArguments) {
        super(sessionId, WebsocketMessageType.FUNCTION_CALL);
        this.functionName = functionName;
        this.functionArguments = functionArguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionArguments() {
        return functionArguments;
    }

    public void setFunctionArguments(String functionArguments) {
        this.functionArguments = functionArguments;
    }
}
