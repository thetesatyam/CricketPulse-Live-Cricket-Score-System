package com.cricketpulse;

import com.cricketpulse.entity.Role;
import com.cricketpulse.entity.User;
import com.cricketpulse.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class CricketPulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CricketPulseApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDefaultUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User(
                        "admin",
                        "admin@cricketpulse.io",
                        passwordEncoder.encode("admin123"),
                        Role.ROLE_ADMIN
                );
                userRepository.save(admin);
                System.out.println(">>> Initialized default ADMIN user: admin / admin123");
            }

            if (!userRepository.existsByUsername("scorer1")) {
                User scorer = new User(
                        "scorer1",
                        "scorer@cricketpulse.io",
                        passwordEncoder.encode("scorer123"),
                        Role.ROLE_SCORER
                );
                userRepository.save(scorer);
                System.out.println(">>> Initialized default SCORER user: scorer1 / scorer123");
            }

            if (!userRepository.existsByUsername("viewer1")) {
                User viewer = new User(
                        "viewer1",
                        "viewer@cricketpulse.io",
                        passwordEncoder.encode("viewer123"),
                        Role.ROLE_VIEWER
                );
                userRepository.save(viewer);
                System.out.println(">>> Initialized default VIEWER user: viewer1 / viewer123");
            }
        };
    }
}
