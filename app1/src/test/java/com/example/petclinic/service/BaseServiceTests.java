package com.example.petclinic.service;

import java.io.File;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseServiceTests {

  @Container
  static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(
      new File("src/test/resources/docker-compose.yml")
  ).withExposedService("postgres", 5432);

}
