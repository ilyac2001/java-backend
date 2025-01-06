package ru.andersenlab.infoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andersenlab.infoservice.domain.model.Atm;

import java.util.UUID;

@Repository
public interface AtmRepository extends JpaRepository<Atm, UUID> {
}
