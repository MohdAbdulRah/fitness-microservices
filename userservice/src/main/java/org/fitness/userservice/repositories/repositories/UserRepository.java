package org.fitness.userservice.repositories.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.fitness.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
     boolean existsByEmail(String email);

    Boolean existsByKeyCloakId(String userId);

    User findByEmail(@NotBlank(message = "Email is Required") @Email(message = "Invalid Email Format") String email);
}
