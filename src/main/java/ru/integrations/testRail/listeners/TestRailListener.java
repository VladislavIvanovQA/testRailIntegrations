package ru.integrations.testRail.listeners;

import org.aeonbits.owner.ConfigFactory;
import org.testng.*;
import ru.integrations.testRail.ITestRail;
import ru.integrations.testRail.ITestRailPojo;
import ru.integrations.testRail.TestRail;
import ru.integrations.testRail.config.ProjectConfig;
import ru.integrations.testRail.exceptions.NotFoundParam;
import ru.integrations.testRail.exceptions.NotFoundProject;
import ru.integrations.testRail.exceptions.NotFoundSection;
import ru.integrations.testRail.objectConfig.Project;
import ru.integrations.testRail.objectConfig.Section;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


public class TestRailListener extends TestListenerAdapter implements IClassListener {
    private Map<String, ITestRailPojo> testRail = new HashMap<>();
    private ProjectConfig config = ConfigFactory.create(ProjectConfig.class);
    private String project;
    private Project[] projects;
    private Set<String> classList = new LinkedHashSet<>();
    private List<String> sections = new ArrayList<>();
    private List<String> notTagged = new ArrayList<>();
    private List<String> caseNotFound = new ArrayList<>();
    private Map<String, Boolean> projectList = new HashMap<>();
    private String thisProjectName;

    @Override
    public void onStart(ITestContext testContext) {
        if (config.testRailIntegrations()) {
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
                if (config.section() == null && config.sections().length == 0) {
                    throw new NotFoundParam("section or sections");
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
                if (config.sections().length != 0) {
                    for (int i = 0; i < testContext.getAllTestMethods().length; i++) {
                        classList.add(testContext.getAllTestMethods()[i].getTestClass().getName());
                    }

                }
            } else {
                projects = config.projects();
                List<String> tempProject = new ArrayList<>();
                for (Project pr : projects) {
                    tempProject.add(pr.getProject());
                }
                tempProject.stream()
                        .distinct()
                        .forEach(pr -> projectList.put(pr, false));
            }
        }
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
        if (config.testRailIntegrations()) {
            if (config.project() != null) {
                if (testRail.size() == 0) {
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
                if (project.getTestClass().equalsIgnoreCase(name)) {
                    String nameProject = project.getProject();
                    thisProjectName = nameProject;
                    if (!projectList.get(nameProject)) {
                        ITestRailPojo pojo = new ITestRailPojo();
                        pojo.setTestRail(new ITestRail(nameProject, config));
                        testRail.put(nameProject, pojo);
                        find = true;
                        projectList.replace(nameProject, true);
                        milestoneAndTestRun();
                        break;
                    } else {
                        find = true;
                    }
                }
            }
            if (!find) {
                throw new NotFoundProject("Project " + Arrays.toString(projects) + " not found! " +
                        "Please check correct ProjectName in TestRail or config");
            }
        } else {
            ITestRailPojo pojo = new ITestRailPojo();
            if (config.sections().length != 0) {
                for (String value : classList) {
                    List<Section> sectionList = Arrays.stream(config.sections())
                            .filter(section -> section.getTestClass().equalsIgnoreCase(value))
                            .distinct()
                            .collect(Collectors.toList());

                    if (sectionList.size() == 0) {
                        StringBuilder message = new StringBuilder();
                        message.append("Please check correct name sections in TestRai \n");
                        message.append("Parameter of config: \n");
                        if (config.sections().length != 0) {
                            for (int i = 0; i < config.sections().length; i++) {
                                message.append(config.sections()[i].toString()).append("\n");
                            }
                        }
                        message.append("\n Test class name: \n");
                        if (classList.size() != 0) {
                            for (String next : classList) {
                                message.append(next).append("\n");
                            }
                        }
                        throw new NotFoundSection(message.toString());
                    }

                    sections.add(sectionList.get(0).getNameSectionTR());
                }
                assert sections != null;
                pojo.setTestRail(new ITestRail(project, sections, config));
            } else {
                pojo.setTestRail(new ITestRail(project, config));
            }
            testRail.put(project, pojo);
            thisProjectName = project;
            milestoneAndTestRun();
        }
    }

    private void milestoneAndTestRun() {
        if (config.openMilestone() == null || config.openMilestone().equals("")) {
            testRail.get(thisProjectName).getTestRail().createMilestones(config.newMilestone());
        } else {
            testRail.get(thisProjectName).getTestRail().searchMilestones(config.openMilestone());
        }
        if (config.testRun() == null) {
            testRail.get(thisProjectName).getTestRail().createTestRun("Auto-Test " + config.nameProject(),
                    "Automation Test in " + config.nameProject());
        } else {
            testRail.get(thisProjectName).getTestRail().searchTestRun(config.testRun());
        }
    }

    @Override
    public void onFinish(ITestContext testContext) {
        if (testRail.size() != 0) {
            if (config.projects() == null) {
                if (config.testRailIntegrations()) {
                    if (!(testRail.get(thisProjectName).getCountError() > 0)) {
                        testRail.get(thisProjectName).getTestRail().closeRun();
                        if (!config.regress()) {
                            testRail.get(thisProjectName).getTestRail().closeMilestone();
                        }
                    }
                }
            }
            printProblems();
        }
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        if (config.projects() != null) {
            if (config.testRailIntegrations()) {
                if (!(testRail.get(thisProjectName).getCountError() > 0)) {
                    testRail.get(thisProjectName).getTestRail().closeRun();
                    if (!config.regress()) {
                        testRail.get(thisProjectName).getTestRail().closeMilestone();
                    }
                }
            }
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
                    boolean caseStatus = testRail.get(thisProjectName).getTestRail().setCaseStatus(testData.CaseName(),
                            1, COMMENT);
                    if (!caseStatus) {
                        caseNotFound.add(testData.CaseName());
                    }
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
                if (config.testRailIntegrations()) {
                    boolean caseStatus = testRail.get(thisProjectName).getTestRail().setCaseStatus(testData.CaseName(),
                            5, error);
                    if (!caseStatus) {
                        caseNotFound.add(testData.CaseName());
                    }
                    testRail.get(thisProjectName).setCountError(testRail.get(thisProjectName).getCountError() + 1);
                }
            } else {
                notTagged.add(testMethod.getName());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void printProblems() {
        StringBuilder message = new StringBuilder();
        if (notTagged.size() > 0) {
            message.append("The name of the method that does not contain annotations. \n");
            for (String method : notTagged) {
                message.append(method).append("\n");
            }
        }
        if (caseNotFound.size() > 0) {
            message.append("This cases, not set status in TestRail, please check title case. \n");
            for (String txt : caseNotFound) {
                message.append(txt).append("\n");
            }
        }
        System.err.println(message);
    }
}