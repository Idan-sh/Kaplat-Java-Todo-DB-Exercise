package com.http.server.java.server.obj;

public class Response <T> {
    private T result;
    private String errorMessage;

    public Response(T result, String errorMessage) {
        this.result = result;
        this.errorMessage = errorMessage;
    }

    public T getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
