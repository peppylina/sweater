package com.utkanos.sweater.repos;

import com.utkanos.sweater.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByActivationCode(String activationCode);
}
