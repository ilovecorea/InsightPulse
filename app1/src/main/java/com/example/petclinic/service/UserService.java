package com.example.petclinic.service;

import com.example.petclinic.model.Role;
import com.example.petclinic.model.User;
import com.example.petclinic.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveUser(User user) {

        if(user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("User must have at least a role set!");
        }

        for (Role role : user.getRoles()) {
            if(!role.getName().startsWith("ROLE_")) {
                role.setName("ROLE_" + role.getName());
            }

            if(role.getUser() == null) {
                role.setUser(user);
            }
        }

        userRepository.save(user);
    }
}
