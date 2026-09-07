package com.springai.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RagService {

    private final VectorStore vectorStore;

    public RagService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestText(List<String> texts, String source) {
        // Step 1: Create Documents -- Extract
        List<Document> documents = texts.stream()
                .map(text -> new Document(text, Map.of("source", source)))
                .toList();

        // Step 2: Split into Chunks -- Transform
        TokenTextSplitter splitter = TokenTextSplitter
                            .builder()
                            .withChunkSize(500)
                            .withMinChunkSizeChars(100)
                            .withMinChunkLengthToEmbed(5)
                            .withMaxNumChunks(10_000)
                            .withKeepSeparator(true)
                            .build();

        List<Document> chunks = splitter.apply(documents);

        vectorStore.add(chunks);

        log.info("Ingested: {} chunks from source {}: ", chunks.size(), source);
    }

    // Ingest a PDF file
    public void ingestPdf(Resource pdfResource, String source) {
        // pdfResource -- actual file
        // source -- name of the file
        // Step 1 — Read PDF
        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                pdfResource,
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(
                                ExtractedTextFormatter.builder()
                                        .withNumberOfBottomTextLinesToDelete(3) // remove footers
                                        .withNumberOfTopPagesToSkipBeforeDelete(1)
                                        .build()
                        )
                        .withPagesPerDocument(1) // one Document per page
                        .build()
        );

        List<Document> pages = reader.read();

        // Step 2 — Split
        TokenTextSplitter splitter = TokenTextSplitter
                .builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(100)
                .withMinChunkLengthToEmbed(5)
                .withMaxNumChunks(10000)
                .withKeepSeparator(true)
                .build();

        List<Document> chunks = splitter.apply(pages);

        // Add source metadata to all chunks
        chunks.forEach(chunk ->
                chunk.getMetadata().put("source", source)
        );

        // Step 3 — Store
        vectorStore.add(chunks);

        log.info("Ingested: {} chunks from PDF {}: ", chunks.size(), source);
    }
}
