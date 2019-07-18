package ru.integrations.testRail.config;

import org.aeonbits.owner.Config;


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

    String nameSection();

    String project();

    @ConverterClass(ProjectConverter.class)
    Project[] projects();
}