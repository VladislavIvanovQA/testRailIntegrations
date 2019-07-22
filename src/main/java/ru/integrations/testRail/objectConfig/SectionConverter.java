package ru.integrations.testRail.objectConfig;

import org.aeonbits.owner.Converter;

import java.lang.reflect.Method;

public class SectionConverter implements Converter<Section> {
    public Section convert(Method targetMethod, String text) {
        String[] split = text.split(":", -1);
        String testClass = split[0];
        String nameSection = "";
        if (split.length >= 2)
            nameSection = split[1];
        return new Section(testClass, nameSection);
    }
}
