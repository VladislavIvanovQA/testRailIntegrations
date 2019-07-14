package ru.integrations.testRail.exceptions;

public class NotFoundProject extends RuntimeException{
    public NotFoundProject(String message) {
        super(message);
    }
}
