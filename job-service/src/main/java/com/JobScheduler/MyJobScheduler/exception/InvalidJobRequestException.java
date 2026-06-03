package com.JobScheduler.MyJobScheduler.exception;

public class InvalidJobRequestException extends RuntimeException {
    public InvalidJobRequestException(String message) {
        super(message);
    }
}