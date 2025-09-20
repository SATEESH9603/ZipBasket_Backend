package com.example.onlinetest.Domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorModel {
    public String message;
    public String errorCode;
    public String developerMessage;
}
