package ru.bluewater.itpdataprocessing.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.bluewater.integration.model.ITPData;
import ru.bluewater.itpdataprocessing.service.ITPDataService;

@Component
@Slf4j
public class ITPDataConsumer {

    private final ITPDataService itpDataService;

    @Autowired
    public ITPDataConsumer(ITPDataService itpDataService) {
        this.itpDataService = itpDataService;
    }


    @KafkaListener(topics = "${app.kafka.topic.input}")
    public void consumeITPData(String itpId, ITPData itpData) {
        try {
            log.info("Received ITP data with key: {}", itpId);

            itpDataService.processItpData(itpId, itpData);
        } catch (Exception e) {
            log.error("Error processing ITP data with key: {}", itpId, e);
        }
    }
}
