package az.ingress.hrms.log.person.person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonLogRepository
        extends JpaRepository<PersonLog, Integer> {
}