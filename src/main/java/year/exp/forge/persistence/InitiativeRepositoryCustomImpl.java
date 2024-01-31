package year.exp.forge.persistence;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import year.exp.forge.domain.Initiative;
import year.exp.forge.domain.Status;

public class InitiativeRepositoryCustomImpl implements InitiativeRepositoryCustom{

    private MongoTemplate mongoTemplate;

    public InitiativeRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public boolean isMember(String userId, String initiativeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(initiativeId));
        query.addCriteria(Criteria.where("members").is(userId));
        return mongoTemplate.exists(query, Initiative.class);
    }

    @Override
    public void addMemberToInitiative(String initiativeId, String memberId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(initiativeId));
        Update update = new Update();
        update.addToSet("members", memberId);
        mongoTemplate.updateFirst(query, update, Initiative.class);
    }

    @Override
    public Initiative get(String initiativeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(initiativeId));
        return mongoTemplate.findOne(query, Initiative.class);
    }

    @Override
    public void updateStatus(String initiativeId, Status status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(initiativeId));
        Update update = new Update();
        update.set("status", status);
        mongoTemplate.updateFirst(query, update, Initiative.class);
    }
}
