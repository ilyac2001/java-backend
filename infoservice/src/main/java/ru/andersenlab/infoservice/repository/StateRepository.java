package ru.andersenlab.infoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andersenlab.infoservice.domain.model.State;

import java.util.UUID;

@Repository
public interface StateRepository extends JpaRepository<State, UUID> {
}
