package year.exp.forge.persistence;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import year.exp.forge.domain.User;

import java.util.Map;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private MongoTemplate mongoTemplate;

    public UserRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User getByCriteria(Map<String, Object> criteria) {
        Query query = new Query();
        criteria.entrySet().forEach(entry -> query.addCriteria(new Criteria(entry.getKey()).is(entry.getValue())));
        return mongoTemplate.findOne(query,User.class, User.COLLECTION);
    }

    @Override
    public void updateByCriteria(Map<String, Object> criteria, Map<String, Object> data) {
        Query query = new Query();
        criteria.entrySet().forEach(entry -> query.addCriteria(new Criteria(entry.getKey()).is(entry.getValue())));
        Update update = new Update();
        data.entrySet().forEach(entry -> update.set(entry.getKey(), entry.getValue()));
        mongoTemplate.updateFirst(query, update, User.class, User.COLLECTION);
    }
}
