package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    void deleteAllByUserIdAndRole(Integer userId, String role);
    Token findByUserIdAndRole(Integer userId, String role);
}
