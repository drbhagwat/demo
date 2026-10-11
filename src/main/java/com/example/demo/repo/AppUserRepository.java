package com.example.demo.repo;

import com.example.demo.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
  Optional<AppUser> findByUsername(String username);

  @Query("""
      SELECT COUNT(u)
          FROM AppUser u
          JOIN u.roles r
          WHERE r = :role
      """)
  long countUsersByRole(@Param("role") String role);
}
