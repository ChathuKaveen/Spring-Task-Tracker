package com.task_management.Task.Management.repositories;

import com.task_management.Task.Management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User , Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("""
            SELECT COUNT(l) > 0 FROM User l
            WHERE l.id = :id
            """)
    boolean findIfOwner(@Param("id") Long id);
}
