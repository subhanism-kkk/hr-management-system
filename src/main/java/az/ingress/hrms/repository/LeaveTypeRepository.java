package az.ingress.hrms.repository;

import az.ingress.hrms.entity.lookup.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {

    Optional<LeaveType> findByCodeIgnoreCase(String code);
    boolean existsByNameIgnoreCase(String name);

    @Query(value = "SELECT * FROM leave_type WHERE id = :id", nativeQuery = true)
    Optional<LeaveType> findByIdWithDeleted(Integer id);
}