package dungeon.trading.core.kafka.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaErrorService {
    private final KafkaErrorRepository kafkaErrorRepository;

    public KafkaErrorService(
        KafkaErrorRepository kafkaErrorRepository) {
        this.kafkaErrorRepository = kafkaErrorRepository;
    }

    public void newKafkaError(String topic, String exception) {
        String message = "Error while consuming '" + topic + "' event:\n"
                + exception;

        KafkaError error = new KafkaError(message);
        this.kafkaErrorRepository.save(error);
    }

    public void newKafkaError(String topic, String consumerRecord, String exception) {
        String message = "Error while consuming '" + topic + "' event:\n"
                + consumerRecord + "\n"
                + exception;

        KafkaError error = new KafkaError(message);
        this.kafkaErrorRepository.save(error);
    }
}
