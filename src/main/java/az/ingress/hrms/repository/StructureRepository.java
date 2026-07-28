package az.ingress.hrms.repository;

import az.ingress.hrms.entity.organization.Structure;
import az.ingress.hrms.entity.person.PersonPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StructureRepository extends JpaRepository<Structure,Integer> {
    @Query(value = " SELECT * FROM Structure WHERE id = :id", nativeQuery = true)
    Optional<Structure> findByIdWithDeleted(Integer id);

    boolean existsByNameIgnoreCase(String name);

    List<Structure> findByParentStructureIsNull();

    List<Structure> findByParentStructure(Structure parent);

    boolean existsByParentStructure(Structure parent);
}
