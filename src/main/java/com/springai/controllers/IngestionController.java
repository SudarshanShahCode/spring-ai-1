package com.springai.controllers;

import com.springai.services.RagService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/ai/ingest")
public class IngestionController {

    private final RagService ragService;

    public IngestionController(RagService ragService) {
        this.ragService = ragService;
    }

    // Ingest hardcoded knowledge base
    @PostMapping("/knowledge-base")
    public ResponseEntity<String> ingestKnowledgeBase() {

        List<String> documents = List.of(
                """
                XYZCorp Refund Policy:
                Customers can request a full refund within 30 days of purchase.
                After 30 days, only store credit is available.
                To initiate a refund, contact support@xyzcorp.com with your order ID.
                Refunds are processed within 5-7 business days.
                """,
                """
                XYZCorp Shipping Policy:
                Standard shipping takes 5-7 business days and costs $4.99.
                Express shipping takes 1-2 business days and costs $14.99.
                Free shipping is available on orders above $50.
                We ship to all 50 US states and 30 international countries.
                """,
                """
                XYZCorp PRO Plan Features:
                The PRO plan costs $29/month and includes:
                - Unlimited API calls
                - Priority customer support (response within 2 hours)
                - Access to all premium features
                - Up to 10 team members
                - Advanced analytics dashboard
                """,
                """
                XYZCorp FREE Plan Features:
                The FREE plan includes:
                - Up to 100 API calls per month
                - Standard customer support (response within 48 hours)
                - Access to basic features only
                - Single user only
                - Basic analytics
                """
        );

        ragService.ingestText(documents, "XYZCorp-policies");
        return ResponseEntity.ok("Knowledge base ingested successfully!");
    }

    // Ingest an uploaded PDF
    @PostMapping("/pdf")
    public ResponseEntity<String> ingestPdf(@RequestPart("file") MultipartFile file)
            throws IOException {

        Resource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        ragService.ingestPdf(resource, file.getOriginalFilename());
        return ResponseEntity.ok("PDF ingested: " + file.getOriginalFilename());
    }
}
