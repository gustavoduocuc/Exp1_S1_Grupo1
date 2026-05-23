package com.minimarket.exception;

public class InvalidRequestException extends RuntimeException {

    private final String clientMessage;

    public InvalidRequestException(String clientMessage) {
        super(clientMessage);
        this.clientMessage = clientMessage;
    }

    public InvalidRequestException(String clientMessage, String logDetail) {
        super(logDetail);
        this.clientMessage = clientMessage;
    }

    public String getClientMessage() {
        return clientMessage;
    }
}
