package year.exp.forge.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import year.exp.forge.domain.Education;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EducationRepository extends MongoRepository<Education, String>, EducationRepositoryCustom {
    Page<Education> findAll(Pageable pageable);
}
