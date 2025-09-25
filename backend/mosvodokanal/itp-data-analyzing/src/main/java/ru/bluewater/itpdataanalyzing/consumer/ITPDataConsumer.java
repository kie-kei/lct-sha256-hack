package ru.bluewater.itpdataanalyzing.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.bluewater.integration.message.ITPDataMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ITPDataConsumer {
    @KafkaListener(
            topics = "${app.kafka.topic.input}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "20",
            containerFactory = "batchKafkaListenerContainerFactory"
    )
    public void consumeITPDataBatch(List<ConsumerRecord<String, ITPDataMessage>> records) {
        log.debug("Received batch of {} records on virtual thread: {}",
                records.size(), Thread.currentThread());

        records.parallelStream()
                .forEach(record ->
                        CompletableFuture.runAsync(
                                () -> processRecord(record.key(), record.value()),
                                Executors.newVirtualThreadPerTaskExecutor()
                        )
                );
    }

    private void processRecord(String itpId, ITPDataMessage itpDataMessage) {
        try {
            log.debug("Processing on virtual thread: {}", Thread.currentThread().isVirtual());
            // analyzing and processing
        } catch (Exception e) {
            log.error("Error processing ITP data with key: {}", itpId, e);
        }
    }
}
