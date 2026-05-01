import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kfkconsumer.config.KafkaConfig;

import java.time.Duration;

/**
 * Потребитель данных из кафки
 */
public class KafkaConsumer01{
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer01.class);
    private static final Duration DURATION_10_MILLISECONDS = Duration.ofMillis(10);

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(KafkaConfig.getConsumerConfig());
    }
}
