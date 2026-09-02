package com.springai.controllers;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/embeddings")
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/generate")
    public Map<String, Object> generateEmbedding(@RequestParam String text) {

        EmbeddingResponse response = embeddingModel.embedForResponse(
                List.of(text)
        );

        float[] vector = response.getResults()
                .getFirst()
                .getOutput();

        return Map.of(
                "text", text,
                "dimensions", vector.length,
                "first5values", new float[]{
                        vector[0], vector[1],
                        vector[2], vector[3], vector[4]
                }
        );
    }

    @GetMapping("/similarity")
    public Map<String, Object> similarity(
            @RequestParam String text1,
            @RequestParam String text2) {

        float[] vec1 = embeddingModel.embed(text1);
        float[] vec2 = embeddingModel.embed(text2);

        double similarity = cosineSimilarity(vec1, vec2); // 0.0 - 1.0

        return Map.of(
                "text1", text1,
                "text2", text2,
                "similarity", similarity,
                "interpretation", similarity > 0.65
                        ? "Very similar"
                        : similarity > 0.40
                        ? "Somewhat similar"
                        : "Not similar"
        );
    }

    // Cosine similarity formula
    private double cosineSimilarity(float[] vec1, float[] vec2) {
        double dot = 0, norm1 = 0, norm2 = 0;
        for (int i = 0; i < vec1.length; i++) {
            dot   += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
