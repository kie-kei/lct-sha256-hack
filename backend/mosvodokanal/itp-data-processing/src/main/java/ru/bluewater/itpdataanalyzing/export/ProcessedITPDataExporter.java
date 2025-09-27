package ru.bluewater.itpdataanalyzing.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.bluewater.integration.message.ITPDataMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessedITPDataExporter {
    private final KafkaTemplate<String, ITPDataMessage> kafkaTemplate;

    @Value("${app.kafka.topic.output}")
    private String outputTopic;

    public void exportITPData(String itpId, ITPDataMessage itpDataMessage) {
        log.debug("Sending ITP data to Kafka topic: {} with key: {}", outputTopic, itpId);

        kafkaTemplate.send(outputTopic, itpId, itpDataMessage)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to send ITP data with key: {}", itpId, throwable);
                    } else {
                        log.info("Successfully sent ITP data with key: {}", itpId);
                    }
                });
    }
}
