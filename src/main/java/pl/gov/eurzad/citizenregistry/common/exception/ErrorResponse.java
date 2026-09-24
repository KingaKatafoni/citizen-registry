package pl.gov.eurzad.citizenregistry.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timeStamp;

    public ErrorResponse(int status, String error, String message){
        this.status = status;
        this.error = error;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }
}
