package com.example.petclinic.rest.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/")
public class RootRestController {

  @Value("#{servletContext.contextPath}")
  private String servletContextPath;

  @RequestMapping(value = "/")
  public void redirectToSwagger(HttpServletResponse response) throws IOException {
    response.sendRedirect(this.servletContextPath + "/swagger-ui/index.html");
  }

}

