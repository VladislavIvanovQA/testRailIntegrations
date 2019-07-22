package ru.integrations.testRail.exceptions;

public class NotFoundSection extends RuntimeException {

    public NotFoundSection(String message) {
        super(message);
    }
}
