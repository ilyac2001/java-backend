package ru.andersenlab.authenticationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.andersenlab.authenticationservice.domain.enumerated.EmployeeStatusEnum;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "employee_status")
public class EmployeeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_status_id")
    private Integer employeeStatusId;

    @Column(name = "status_name", nullable = false)
    private String statusName;

    public EmployeeStatusEnum toEnum() {
        return EmployeeStatusEnum.fromId(this.employeeStatusId);
    }
}
