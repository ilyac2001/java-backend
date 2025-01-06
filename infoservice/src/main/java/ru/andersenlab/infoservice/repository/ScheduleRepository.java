package ru.andersenlab.infoservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andersenlab.infoservice.domain.model.Schedule;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    @EntityGraph(attributePaths = {"scheduleWeekDays.weekDay"})
    Optional<Schedule> findWithDetailsByScheduleId(UUID scheduleId);
}
