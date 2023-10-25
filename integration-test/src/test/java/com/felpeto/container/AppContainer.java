package com.felpeto.container;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.InternetProtocol;

public class AppContainer extends GenericContainer<AppContainer> {

  private static final String DOCKER_IMAGE_NAME = "app-test:integration";

  public AppContainer() {
    super(DOCKER_IMAGE_NAME);
  }

  public AppContainer withHostDebug(int debugPort) {
    super.withEnv("JAVA_TOOL_OPTIONS",
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:5005");
    super.addFixedExposedPort(5005, debugPort, InternetProtocol.TCP);
    super.logger()
        .info("Debug listener is enabled at port {}, see README.md or {} for details", debugPort,
            "https://www.jetbrains.com/help/idea/tutorial-remote-debug.html");
    return this.self();
  }

}
