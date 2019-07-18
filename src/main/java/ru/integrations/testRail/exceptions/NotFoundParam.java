package ru.integrations.testRail.exceptions;

public class NotFoundParam extends RuntimeException {
    public NotFoundParam(String message) {
        super("Please, check param " + message + " in testRail.properties \n Integrations not worked!");
    }
}
