package ru.integrations.testRail.config;

import org.aeonbits.owner.Config;
import ru.integrations.testRail.objectConfig.Project;
import ru.integrations.testRail.objectConfig.ProjectConverter;
import ru.integrations.testRail.objectConfig.Section;
import ru.integrations.testRail.objectConfig.SectionConverter;

@Config.Sources({"classpath:testRail.properties"})
public interface ProjectConfig extends Config {
    String nameProject();

    String hostTR();

    @DefaultValue("false")
    boolean testRailIntegrations();

    String loginTR();

    String passTR();

    String testRun();

    String newMilestone();

    String openMilestone();

    boolean regress();

    String section();

    @ConverterClass(SectionConverter.class)
    Section[] sections();

    String project();

    @ConverterClass(ProjectConverter.class)
    Project[] projects();
}