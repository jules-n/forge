package year.exp.forge.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import year.exp.forge.domain.Initiative;

import java.util.List;

@Repository
public interface InitiativeRepository extends MongoRepository<Initiative, String>, InitiativeRepositoryCustom {

    @Query("{ 'members' : ?0 }")
    Page<Initiative> findByMembersContaining(String memberId, Pageable pageable);

    @Query("{ 'members' : { $ne: ?0 }, 'status' : 'IN_SEARCH_OF_MEMBERS' }")
    Page<Initiative> findByMembersNotContainingAndStatusInSearchOfMembers(String memberId, Pageable pageable);

    @Query("{ 'members' : ?0 , 'status' : 'IN_SEARCH_OF_MEMBERS' }")
    List<Initiative> findByMembersContainingAndStatusInSearchOfMembers(String memberId);
}
