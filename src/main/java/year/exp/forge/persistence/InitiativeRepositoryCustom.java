package year.exp.forge.persistence;

import year.exp.forge.domain.Initiative;
import year.exp.forge.domain.Status;

public interface InitiativeRepositoryCustom {

    boolean isMember(String userId, String initiativeId);
    void addMemberToInitiative(String initiativeId, String memberId);

    Initiative get(String initiativeId);

    void updateStatus(String initiativeId, Status status);
}
