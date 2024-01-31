package year.exp.forge.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import year.exp.forge.domain.Education;
import year.exp.forge.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {

    @Query("{ 'role' : 'USER' }")
    Page<User> findByRoleInUser(Pageable pageable);

    @Query("{ 'role' : 'USER' }")
    List<User> findByRoleInUser();
}
