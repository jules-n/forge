package year.exp.forge.persistence;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import year.exp.forge.domain.Education;

import java.util.Map;

public class EducationRepositoryCustomImpl implements EducationRepositoryCustom{

    private MongoTemplate mongoTemplate;

    public EducationRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Education getByCriteria(Map<String, Object> criteria) {
        Query query = new Query();
        criteria.entrySet().forEach(entry -> query.addCriteria(new Criteria(entry.getKey()).is(entry.getValue())));
        return mongoTemplate.findOne(query, Education.class, Education.COLLECTION);
    }
}
