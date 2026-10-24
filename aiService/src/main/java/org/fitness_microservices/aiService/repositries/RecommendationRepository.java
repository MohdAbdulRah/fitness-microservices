package org.fitness_microservices.aiService.repositries;

import org.fitness_microservices.aiService.models.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation,String> {
   List<Recommendation> findAllByUserId(String userId);
   List<Recommendation> findAllByActivityId(String activityId);
}
