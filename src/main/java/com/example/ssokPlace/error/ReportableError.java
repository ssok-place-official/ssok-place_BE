package com.example.ssokPlace.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReportableError extends RuntimeException{

    private final HttpStatus status;
    private final String clientMessage;
    private final Object data;

    public ReportableError(HttpStatus status){
        this(status, status.getReasonPhrase(), null);
    }
    public ReportableError(HttpStatus status, String clientMessage){
        this(status, clientMessage, null);
    }
    public ReportableError(HttpStatus status, String clientMessage, Object data){
        super(clientMessage);
        this.status = status;
        this.clientMessage = clientMessage;
        this.data = data;
    }
}
