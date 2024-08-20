package com.jts.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.jts.user.entity.Users;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String email);
}
