package org.fitness.userservice.repositories.repositories;

import org.fitness.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
     boolean existsByEmail(String email);
}
