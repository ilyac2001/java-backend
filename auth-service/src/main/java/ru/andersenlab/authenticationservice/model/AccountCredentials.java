package ru.andersenlab.authenticationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "account_credentials")
public class AccountCredentials {

    @Id
    private UUID employeeId;

    @Column(nullable = false, unique = true)
    private String workEmail;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "employee_status_id", nullable = false)
    private EmployeeStatus employeeStatus;

    @ManyToOne
    @JoinColumn(name = "access_level_id", nullable = false)
    private AccessLevel accessLevel;

    @Column(name = "is_temporary_password", nullable = false)
    private boolean isTemporaryPassword;
}