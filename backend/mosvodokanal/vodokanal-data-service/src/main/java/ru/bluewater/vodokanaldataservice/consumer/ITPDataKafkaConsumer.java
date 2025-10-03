package ru.bluewater.vodokanaldataservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.bluewater.integration.message.ITPDataMessage;
import ru.bluewater.vodokanaldataservice.service.ITPDataMessageProcessorService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ITPDataKafkaConsumer {
    private final ITPDataMessageProcessorService messageProcessorService;

    @KafkaListener(
            topics = "${app.kafka.topic.input}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "1"
    )
    public void consumeProcessedITPData(List<ConsumerRecord<String, ITPDataMessage>> records) {
        log.debug("Received batch of {} processed ITP data records on virtual thread: {}",
                records.size(), Thread.currentThread().isVirtual());

        records.parallelStream()
                .forEach(record -> processRecord(record.key(), record.value()));
    }

    private void processRecord(String itpId, ITPDataMessage message) {
        try {
            log.debug("Processing ITP data message with key: {} on virtual thread: {}",
                    itpId, Thread.currentThread().isVirtual());

            if (message == null || message.getItp() == null) {
                log.warn("Invalid message received for key: {}", itpId);
                return;
            }

            messageProcessorService.processITPDataMessage(message);
            log.debug("Successfully processed ITP data for key: {}", itpId);
        } catch (Exception e) {
            log.error("Error processing ITP data message with key: {}", itpId, e);
        }
    }
}