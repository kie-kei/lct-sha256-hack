package ru.bluewater.itpdataprocessing.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.bluewater.integration.model.ITPData;
import ru.bluewater.itpdataprocessing.service.ITPDataService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ITPDataConsumer {
    private final ITPDataService itpDataService;

    @KafkaListener(
            topics = "${app.kafka.topic.input}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "5",
            containerFactory = "batchKafkaListenerContainerFactory"
    )
    public void consumeITPDataBatch(List<ConsumerRecord<String, ITPData>> records) {
        log.debug("Received batch of {} ITP data records", records.size());

        CompletableFuture.runAsync(() -> {
            List<CompletableFuture<Void>> futures = records.stream()
                    .map(record -> CompletableFuture.runAsync(() ->
                            processRecord(record.key(), record.value())
                    ))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .join();
        });
    }

    @KafkaListener(
            topics = "${app.kafka.topic.input}",
            groupId = "${spring.kafka.consumer.group-id}-single",
            concurrency = "3",
            containerFactory = "singleKafkaListenerContainerFactory"
    )
    public void consumeITPDataSingle(ConsumerRecord<String, ITPData> record) {
        processRecord(record.key(), record.value());
    }

    private void processRecord(String itpId, ITPData itpData) {
        try {
            log.debug("Processing ITP data with key: {}", itpId);
            itpDataService.processItpData(itpId, itpData);
        } catch (Exception e) {
            log.error("Error processing ITP data with key: {}", itpId, e);
            // Можно добавить retry логику или отправку в DLQ
        }
    }
}
