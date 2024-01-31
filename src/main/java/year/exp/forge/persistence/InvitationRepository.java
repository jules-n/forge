package year.exp.forge.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import year.exp.forge.domain.Invitation;
import year.exp.forge.domain.Membership;
import year.exp.forge.domain.Status;

@Repository
public interface InvitationRepository extends MongoRepository<Invitation, String> {

    Page<Invitation> findByInitiativeIdAndMembership(String initiativeId, Membership membership, Pageable pageable);
    Page<Invitation> findByUserIdAndMembership(String userId, Membership membership, Pageable pageable);

}
