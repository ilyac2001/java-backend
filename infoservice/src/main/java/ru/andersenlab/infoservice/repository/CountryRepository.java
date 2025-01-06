package ru.andersenlab.infoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andersenlab.infoservice.domain.model.Country;

import java.util.UUID;

@Repository
public interface CountryRepository extends JpaRepository<Country, UUID> {
}
