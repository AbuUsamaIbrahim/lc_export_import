package com.pacific.restapi.util;

import com.pacific.restapi.model.Role;
import com.pacific.restapi.model.User;
import com.pacific.restapi.repository.RoleRepository;
import com.pacific.restapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbInit {
    @Value("${login.user.name}")
    private String email;
    @Value("${login.user.password}")
    private String password;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public DbInit(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    //    @PostConstruct
    public void setAdminUser() {
//        List<User> userList = userRepository.findAllByActiveTrue();
//        if (userList.size() == 0) {
        List<Role> roleList = new ArrayList<>();
        Role role = new Role();
        role.setId(Long.valueOf("1"));
//            role.setLevel(1);
        role.setName("ROLE_ADMIN");
        role.setIsActive(true);
        role = roleRepository.save(role);
        roleList.add(role);
        User user = new User();
        user.setId(Long.valueOf("1"));
        user.setEmail(email);
        user.setUsername("Zahangir");
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(password));
        user.setName("Admin User");
        user.setStatus(UserStatus.ACTIVE.name());
//            user.setNoOfAttempt(0);
        user.setRoles(roleList);
        user = userRepository.save(user);
//        }
    }
}
