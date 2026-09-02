package com.springai.controllers;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/vector")
public class VectorController {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    public VectorController(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    @PostMapping("/store")
    public ResponseEntity<String> storeDocuments() {
        List<Document> documents = List.of(
                new Document(
                        "Spring AI is a framework that simplifies AI integration in Spring Boot applications.",
                        Map.of("source", "spring-ai-docs", "topic", "spring-ai")
                ),
                new Document(
                        "Kubernetes is an open-source container orchestration platform for automating deployment and scaling.",
                        Map.of("source", "k8s-docs", "topic", "kubernetes")
                ),
                new Document(
                        "PostgreSQL is a powerful open-source relational database system with over 35 years of development.",
                        Map.of("source", "pg-docs", "topic", "postgresql")
                ),
                new Document(
                        "Spring Boot makes it easy to create production-grade Spring applications with minimal configuration.",
                        Map.of("source", "spring-docs", "topic", "spring-boot")
                ),
                new Document(
                        "Your text content here",
                        Map.of(

                                "source",   "manual-v2.pdf",
                                "page",     "42",
                                "category", "refund-policy",
                                "author",   "legal-team"
                        )
                )
        );

        vectorStore.add(documents);

        return ResponseEntity.ok("Stored " + documents.size() + " documents!");
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(@RequestParam String query) {
        List<Document> results = vectorStore
                .similaritySearch(
                        SearchRequest.builder()
                                .query(query)
                                .topK(2) // max 2 results
//                                .similarityThreshold(0.7) // score >= 0.7
                                .filterExpression("category == 'refund-policy'")
                                .build()
                );

        return results
                .stream()
                .map(document -> Map.of(
                        "content", document.getText(),
                        "metadata", document.getMetadata(),
                        "score", document.getScore()
                ))
                .toList();
    }
}
