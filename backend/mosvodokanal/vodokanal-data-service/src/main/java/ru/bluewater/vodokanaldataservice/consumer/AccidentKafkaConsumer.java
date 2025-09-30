package ru.bluewater.vodokanaldataservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.bluewater.integration.message.AccidentMessage;
import ru.bluewater.vodokanaldataservice.api.dto.request.AccidentCreateRequest;
import ru.bluewater.vodokanaldataservice.mapper.AccidentMessageMapper;
import ru.bluewater.vodokanaldataservice.service.AccidentService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccidentKafkaConsumer {
    private final AccidentService accidentService;
    private final AccidentMessageMapper accidentMessageMapper;

    @KafkaListener(
            topics = "${app.kafka.topic.accidents}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "10"
    )
    public void consumeAccidentsBatch(List<ConsumerRecord<String, AccidentMessage>> records) {
        log.debug("Received batch of {} accident records on virtual thread: {}",
                records.size(), Thread.currentThread().isVirtual());

        records.parallelStream()
                .forEach(record -> processRecord(record.key(), record.value()));
    }

    private void processRecord(String itpId, AccidentMessage message) {
        try {
            log.debug("Processing accident message for ITP: {} on virtual thread: {}",
                    itpId, Thread.currentThread().isVirtual());

            if (message == null || message.getItpId() == null) {
                log.warn("Invalid accident message received for key: {}", itpId);
                return;
            }

            AccidentCreateRequest request = accidentMessageMapper.toCreateRequest(message);
            accidentService.create(request);

            log.debug("Successfully processed accident for ITP: {}", itpId);

        } catch (Exception e) {
            log.error("Error processing accident message for ITP: {}", itpId, e);
        }
    }
}