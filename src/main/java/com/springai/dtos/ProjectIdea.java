package com.springai.dtos;

import java.util.List;

public record ProjectIdea(
        String title,
        String description,
        String difficulty,
        List<String> skillsLearned
) {}
