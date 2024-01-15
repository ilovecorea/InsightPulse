package com.example.petclinic.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.example.petclinic.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"local"})
class UserServiceTests extends BaseServiceTests {

  @Autowired
  private UserService userService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void shouldAddUser() throws Exception {
    User user = new User();
    user.setUsername("username");
    user.setPassword("password");
    user.setEnabled(true);
    user.addRole("OWNER_ADMIN");

    userService.saveUser(user);
    assertThat(user.getRoles().parallelStream().allMatch(role -> role.getName().startsWith("ROLE_")), is(true));
    assertThat(user.getRoles().parallelStream().allMatch(role -> role.getUser() != null), is(true));
  }

}
