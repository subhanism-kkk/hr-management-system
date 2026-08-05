package az.ingress.hrms.repository;

import az.ingress.hrms.entity.organization.Structure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Structure> findByParentStructureIsNull(Pageable pageable);

    Page<Structure> findByParentStructure(Structure parent, Pageable pageable);

    boolean existsByParentStructure(Structure parent);
}
