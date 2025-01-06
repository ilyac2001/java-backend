package ru.andersenlab.authenticationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andersenlab.authenticationservice.model.AccessLevel;

@Repository
public interface AccessLevelRepository extends JpaRepository<AccessLevel, Integer> {
}
