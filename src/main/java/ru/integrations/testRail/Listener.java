package ru.integrations.testRail;

import org.aeonbits.owner.ConfigFactory;
import org.testng.*;
import ru.integrations.testRail.config.Project;
import ru.integrations.testRail.config.ProjectConfig;
import ru.integrations.testRail.exceptions.NotFoundParam;
import ru.integrations.testRail.exceptions.NotFoundProject;

import java.lang.reflect.Method;

public class Listener extends TestListenerAdapter implements IClassListener {
    private ITestRail testRail;
    private int countErrors = 0;
    private ProjectConfig config = ConfigFactory.create(ProjectConfig.class);
    private String project;
    private Project[] projects;

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
                throw new NotFoundParam("project или projects");
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
                throw new NotFoundProject("Проект " + project + " не найден! Проверте корректность названия проекта.");
            }
        } else {
            testRail = new ITestRail(project, config);
        }
        if (config.openMilestone() == null || config.openMilestone().equals("")) {
            testRail.createMilestones(config.newMilestone(), "Test automation");
        } else {
            testRail.searchMilestones(config.openMilestone());
        }
        testRail.createTestRun("Auto-Test " + config.nameProject(), "Авто-тесты для " + config.nameProject());
    }

    @Override
    public void onFinish(ITestContext testContext) {
        if (config.projects() == null){
            if (config.testRailIntegrations()) {
                if (!(countErrors > 0)) {
                    testRail.closeRun();
                    if (!config.regress()) {
                        testRail.closeMilestone();
                    }
                }
            }
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
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        try {
            Method testMethod = result.getTestClass().getRealClass().getMethod(result.getMethod().getMethodName());
            if (testMethod.isAnnotationPresent(TestRailImpl.class)) {
                TestRailImpl testData = testMethod.getAnnotation(TestRailImpl.class);
                String COMMENT = "Тест прошел!";
                if (config.testRailIntegrations()) {
                    testRail.setCaseStatus(testData.CaseName(), 1, COMMENT);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            Method testMethod = result.getTestClass().getRealClass().getMethod(result.getMethod().getMethodName());
            if (testMethod.isAnnotationPresent(TestRailImpl.class)) {
                TestRailImpl testData = testMethod.getAnnotation(TestRailImpl.class);
                String error = "";
                error += result.getThrowable().getMessage();
                error += "=================REQUEST=================\n";
                error += "=================RESPONSE=================\n";
                if (config.testRailIntegrations()) {
                    testRail.setCaseStatus(testData.CaseName(), 5, error);
                    countErrors++;
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}