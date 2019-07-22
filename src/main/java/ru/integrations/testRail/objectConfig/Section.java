package ru.integrations.testRail.objectConfig;

public class Section {
    private final String testClass;
    private final String nameSectionTR;

    public Section(String testClass, String nameSectionTR) {
        this.testClass = testClass;
        this.nameSectionTR = nameSectionTR;
    }

    public String getTestClass() {
        return testClass;
    }

    public String getNameSectionTR() {
        return nameSectionTR;
    }

    @Override
    public String toString() {
        return "Section{" +
                "testClass='" + testClass + '\'' +
                ", nameSectionTR='" + nameSectionTR + '\'' +
                '}';
    }
}
