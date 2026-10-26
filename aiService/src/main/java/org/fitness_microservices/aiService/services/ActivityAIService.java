package org.fitness_microservices.aiService.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fitness_microservices.aiService.models.Activity;
import org.fitness_microservices.aiService.models.Recommendation;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String response = geminiService.getRecommendation(prompt);
        log.info(response);
        System.out.println("=========Response============");
        return processAIResponse(activity,response);
    }

    private Recommendation processAIResponse(Activity activity, String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0).path("content").path("parts")
                    .get(0).path("text");
        }
        catch(Exception e){

        }
    }

    private String createPromptForActivity(Activity activity) {

        String prompt = """
        You are an intelligent fitness coach and activity analyst.

        Analyze the following user's physical activity and generate
        a personalized, practical, and safe fitness recommendation.

        Activity Details:
        - Activity ID: %s
        - User ID: %s
        - Activity Type: %s
        - Duration: %s minutes
        - Calories Burned: %s kcal
        - Start Time: %s
        - Additional Metrics: %s

        Based on this activity, provide the following:

        1. recommendation:
           A concise overall assessment of the activity and its
           contribution to the user's fitness.

        2. improvements:
           A list of specific, actionable ways the user can improve
           their performance in future sessions.

        3. suggestions:
           A list of practical suggestions related to consistency,
           recovery, hydration, and fitness progression, as relevant.

        4. safety:
           A list of relevant safety precautions, recovery advice,
           or warning signs to watch for. Avoid unnecessary warnings
           when they are not relevant.

        Guidelines:
        - Personalize the response using the supplied activity data.
        - Do not invent missing measurements or user characteristics.
        - Do not assume the user's age, medical history, fitness level,
          or goals when they have not been provided.
        - Keep advice practical, clear, and encouraging.
        - Avoid making medical diagnoses.
        - If the activity data is insufficient for a specific conclusion,
          acknowledge the limitation.
        - Return valid JSON only, without Markdown fences or extra text.

        Required JSON format:
        {
          "recommendation": "Overall activity assessment",
          "improvements": [
            "Actionable improvement 1",
            "Actionable improvement 2"
          ],
          "suggestions": [
            "Practical suggestion 1",
            "Practical suggestion 2"
          ],
          "safety": [
            "Relevant safety precaution"
          ]
        }
        """.formatted(
                activity.getId(),
                activity.getUserId(),
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getStartTime(),
                activity.getAdditionalMetrics()
        );

        return prompt;
    }
}
