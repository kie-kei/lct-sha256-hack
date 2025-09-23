package ru.bluewater.itpdataprocessing.export;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.bluewater.integration.model.ITPData;

@Component
@Slf4j
public class ProcessedITPDataExporter {
    private final KafkaTemplate<String, ITPData> kafkaTemplate;

    @Value("${app.kafka.topic.output}")
    private String outputTopic;

    public ProcessedITPDataExporter(KafkaTemplate<String, ITPData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void exportITPData(String itpId, ITPData itpData) {
        log.debug("Sending ITP data to Kafka topic: {} with key: {}", outputTopic, itpId);

        kafkaTemplate.send(outputTopic, itpId, itpData)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to send ITP data with key: {}", itpId, throwable);
                    } else {
                        log.info("Successfully sent ITP data with key: {}", itpId);
                    }
                });
    }
}
