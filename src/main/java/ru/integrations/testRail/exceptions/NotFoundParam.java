package ru.integrations.testRail.exceptions;

public class NotFoundParam extends RuntimeException {
    public NotFoundParam() {
        super();
    }

    public NotFoundParam(String message, Throwable cause) {
        super("Проверте, есть ли параметр " + message + " в testRail.properties", cause);
    }

    public NotFoundParam(Throwable cause) {
        super(cause);
    }

    protected NotFoundParam(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super("Проверте, есть ли параметр " + message + " в testRail.properties", cause, enableSuppression, writableStackTrace);
    }

    public NotFoundParam(String message) {
        super("Проверте, есть ли параметр " + message + " в testRail.properties \n Интеграция не работает.");
    }
}
