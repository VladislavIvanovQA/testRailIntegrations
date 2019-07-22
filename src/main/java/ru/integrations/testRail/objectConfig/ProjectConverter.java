package ru.integrations.testRail.objectConfig;

import org.aeonbits.owner.Converter;

import java.lang.reflect.Method;

public class ProjectConverter implements Converter<Project> {
    public Project convert(Method targetMethod, String text) {
        String[] split = text.split(":", -1);
        String testClass = split[0];
        String project = "";
        if (split.length >= 2)
            project = split[1];
        return new Project(testClass, project);
    }
}