package org.acme.Util;

public class ResponseMessage {
    private boolean success;
    private String message;

    public ResponseMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
