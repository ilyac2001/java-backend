package ru.andersenlab.infoservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.andersenlab.infoservice.domain.model.Office;

import java.util.UUID;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID>, JpaSpecificationExecutor<Office> {

    @EntityGraph(attributePaths = {
            "address",
            "address.country",
            "address.state",
            "address.city",
            "address.street",
            "schedule"
    })
    Page<Office> findAll(Specification<Office> specification, Pageable pageable);


}
