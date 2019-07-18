package ru.integrations.testRail.listeners;

import org.aeonbits.owner.ConfigFactory;
import org.testng.*;
import ru.integrations.testRail.ITestRail;
import ru.integrations.testRail.TestRail;
import ru.integrations.testRail.config.Project;
import ru.integrations.testRail.config.ProjectConfig;
import ru.integrations.testRail.exceptions.NotFoundParam;
import ru.integrations.testRail.exceptions.NotFoundProject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static ru.integrations.testRail.listeners.ListenerRestAssured.getRequestFil;
import static ru.integrations.testRail.listeners.ListenerRestAssured.getResponseFil;

public class TestRailListener extends TestListenerAdapter implements IClassListener {
    private ITestRail testRail;
    private int countErrors = 0;
    private ProjectConfig config = ConfigFactory.create(ProjectConfig.class);
    private String project;
    private Project[] projects;
    private List<String> notTagged = new ArrayList<>();

    @Override
    public void onStart(ITestContext testContext) {
        try {
            if (config.hostTR() == null) {
                throw new NotFoundParam("hostTR");
            }
            if (config.loginTR() == null) {
                throw new NotFoundParam("loginTR");
            }
            if (config.passTR() == null) {
                throw new NotFoundParam("passTR");
            }
            if (config.nameSection() == null) {
                throw new NotFoundParam("nameSection");
            }
            if (config.project() == null && config.projects().length == 0) {
                throw new NotFoundParam("project or projects");
            }
            if (config.openMilestone() == null && config.newMilestone() == null) {
                throw new NotFoundParam("openMilestone or newMilestone");
            }
        } catch (NotFoundParam notFoundParam) {
            notFoundParam.printStackTrace();
        }
        if (config.project() != null) {
            project = config.project();
        } else {
            projects = config.projects();
        }
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
        if (config.testRailIntegrations()) {
            if (config.project() != null) {
                if (testRail == null) {
                    lifeCycle(testClass);
                }
            } else if (config.projects().length != 0) {
                lifeCycle(testClass);
            }
        }

    }

    private void lifeCycle(ITestClass testClass) {
        if (config.projects() != null && config.projects().length != 0) {
            String name = testClass.getRealClass().getName();
            boolean find = false;
            for (Project project : projects) {
                if (project.getTestClass().contains(name)) {
                    testRail = new ITestRail(project.getProject(), config);
                    find = true;
                }
            }
            if (!find) {
                throw new NotFoundProject("Project " + project + " not found! Please check correct ProjectName in TestRail or config");
            }
        } else {
            testRail = new ITestRail(project, config);
        }
        if (config.openMilestone() == null || config.openMilestone().equals("")) {
            testRail.createMilestones(config.newMilestone());
        } else {
            testRail.searchMilestones(config.openMilestone());
        }
        if (config.testRun() == null) {
            testRail.createTestRun("Auto-Test " + config.nameProject(), "Automation Test in " + config.nameProject());
        } else {
            testRail.searchTestRun(config.testRun());
        }
    }

    @Override
    public void onFinish(ITestContext testContext) {
        if (config.projects() == null) {
            if (config.testRailIntegrations()) {
                if (!(countErrors > 0)) {
                    testRail.closeRun();
                    if (!config.regress()) {
                        testRail.closeMilestone();
                    }
                }
            }
            printMethodNotAnnotations();
        }
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        if (config.projects() != null) {
            if (config.testRailIntegrations()) {
                if (!(countErrors > 0)) {
                    testRail.closeRun();
                    if (!config.regress()) {
                        testRail.closeMilestone();
                    }
                }
            }
            printMethodNotAnnotations();
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        try {
            Method testMethod = result.getTestClass().getRealClass().getMethod(result.getMethod().getMethodName());
            if (testMethod.isAnnotationPresent(TestRail.class)) {
                TestRail testData = testMethod.getAnnotation(TestRail.class);
                String COMMENT = "Test passed!";
                if (config.testRailIntegrations()) {
                    testRail.setCaseStatus(testData.CaseName(), 1, COMMENT);
                }
            } else {
                notTagged.add(testMethod.getName());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            Method testMethod = result.getTestClass().getRealClass().getMethod(result.getMethod().getMethodName());
            if (testMethod.isAnnotationPresent(TestRail.class)) {
                TestRail testData = testMethod.getAnnotation(TestRail.class);
                String error = "";
                error += result.getThrowable().getMessage();
                error += "\n";
                error += "=================REQUEST=================\n";
                error += getRequestFil() + "\n";
                error += "\n";
                error += "=================RESPONSE=================\n";
                error += getResponseFil() + "\n";
                if (config.testRailIntegrations()) {
                    testRail.setCaseStatus(testData.CaseName(), 5, error);
                    countErrors++;
                }
            } else {
                notTagged.add(testMethod.getName());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void printMethodNotAnnotations() {
        if (notTagged.size() > 0) {
            StringBuilder string = new StringBuilder();
            string.append("The name of the method that does not contain annotations.");
            for (String method : notTagged) {
                string.append(method);
            }
            System.out.println(string);
        }
    }
}