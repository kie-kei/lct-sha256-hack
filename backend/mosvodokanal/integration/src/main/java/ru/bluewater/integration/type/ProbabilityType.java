package ru.bluewater.integration.type;

import lombok.Getter;

@Getter
public enum ProbabilityType {
    LOW("Низкая"),
    MEDIUM("Средняя"),
    HIGH("Высокая"),
    CRITICAL("Критическая");

    private final String value;

    ProbabilityType(String value) {
        this.value = value;
    }
}
