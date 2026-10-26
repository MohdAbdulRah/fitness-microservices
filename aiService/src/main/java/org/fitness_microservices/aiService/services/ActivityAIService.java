package org.fitness_microservices.aiService.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fitness_microservices.aiService.models.Activity;
import org.fitness_microservices.aiService.models.Recommendation;
import org.fitness_microservices.aiService.repositries.RecommendationRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;
    private final RecommendationRepository recommendationRepository;

    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String response = geminiService.getRecommendation(prompt);
        log.info(response);
        System.out.println("=========Response============");
        return processAIResponse(activity,response);
    }


    private Recommendation processAIResponse(Activity activity, String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Step 1: Parse the outer Gemini API response
            JsonNode rootNode = mapper.readTree(response);

            JsonNode textNode = rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isMissingNode() || textNode.isNull()) {
                throw new IllegalArgumentException(
                        "Gemini response does not contain candidate text"
                );
            }

            // Step 2: Extract the actual AI-generated text
            String jsonContent = textNode.asString().trim();

            log.debug("Raw AI-generated content: {}", jsonContent);

            // Step 3: Remove Markdown code fences
            if (jsonContent.startsWith("```json")) {
                jsonContent = jsonContent.substring(7).trim();
            } else if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.substring(3).trim();
            }

            if (jsonContent.endsWith("```")) {
                jsonContent = jsonContent.substring(
                        0, jsonContent.length() - 3
                ).trim();
            }

            // Step 4: Parse the inner JSON
            JsonNode headNode = mapper.readTree(jsonContent);

            String recomText = headNode.path("recommendation").asString();

            List<String> improvements =
                    extractList(headNode.path("improvements"));

            List<String> suggestions =
                    extractList(headNode.path("suggestions"));

            List<String> safety =
                    extractList(headNode.path("safety"));

            // Step 5: Build and save the recommendation
            Recommendation recommendation = Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .recommendation(recomText)
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .build();

            return recommendationRepository.save(recommendation);

        } catch (Exception e) {
            log.error(
                    "Failed to process AI response for activity ID: {}",
                    activity.getId(),
                    e
            );

            throw new IllegalStateException(
                    "Failed to generate recommendation from AI response", e
            );
        }
    }


    private List<String> extractList(JsonNode improvementNode) {
       List<String> imporvements = new ArrayList<>();

       if(improvementNode.isArray()){
           improvementNode.forEach(imp -> {
               imporvements.add(imp.asString());
           });
       }
//       System.out.println("Improvements : "+imporvements);
       return imporvements;
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
         - Keep the recommendation between 50 and 100 words.
         - Return at most 4 improvements.
        - Return at most 4 suggestions.
        - Return at most 4 safety points.
         - Keep each list item under 35 words.
         - Use concise, practical language.
        - The first character of the response must be {
         - The last character of the response must be }
         - Never use Markdown code fences.
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
