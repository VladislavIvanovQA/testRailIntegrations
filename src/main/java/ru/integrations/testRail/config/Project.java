package ru.integrations.testRail.config;


public class Project {
    private final String testClass;
    private final String project;

    public Project(String testClass, String project) {
        this.testClass = testClass;
        this.project = project;
    }

    public String getTestClass() {
        return testClass;
    }

    public String getProject() {
        return project;
    }

    @Override
    public String toString() {
        return "Project{" +
                "testClass='" + testClass + '\'' +
                ", project='" + project + '\'' +
                '}';
    }
}
