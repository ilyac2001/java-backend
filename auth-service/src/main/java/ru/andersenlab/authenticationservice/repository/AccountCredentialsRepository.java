package ru.andersenlab.authenticationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeInfoProjection;
import ru.andersenlab.authenticationservice.model.AccountCredentials;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountCredentialsRepository extends JpaRepository<AccountCredentials, UUID> {

    Optional<AccountCredentials> findByWorkEmail(String workEmail);

    boolean existsByWorkEmail(String email);

    @Query("SELECT employeeId AS employeeId, workEmail AS workEmail, accessLevel.accessLevelId AS accessLevelId, employeeStatus.employeeStatusId AS employeeStatusId "
            + "FROM AccountCredentials "
            + "WHERE employeeId = :employeeId "
            + "AND employeeStatus.employeeStatusId = :employeeStatusId")
    Optional<EmployeeInfoProjection> findByEmployeeIdAndEmployeeStatus_EmployeeStatusId(
            @Param("employeeId") UUID employeeId,
            @Param("employeeStatusId") Integer employeeStatusId);
}