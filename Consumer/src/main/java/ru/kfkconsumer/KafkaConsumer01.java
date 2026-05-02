package ru.kfkconsumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kfkconsumer.config.KafkaConfig;

import java.time.Duration;
import java.util.Collections;

/**
 * Потребитель данных из кафки
 */
public class KafkaConsumer01{
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer01.class);
    private static final Duration DURATION_10_MILLISECONDS = Duration.ofMillis(10);

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(KafkaConfig.getConsumerConfig());
        try(consumer){
            // Подписались на топик из конфиг файла
            consumer.subscribe(Collections.singletonList(KafkaConfig.TOPIC_NAME));
            while (true){
                // Опрашиваем Кафку о наличии новых сообщений
                ConsumerRecords<String, String> consumerRecords = consumer.poll(DURATION_10_MILLISECONDS);
                // Итерации по всем полученным сообщениям в текущем пакете
                for (var record :  consumerRecords ) {
                    logger.info("topic={}, partition={}, offset={}, key={} value={}",
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            record.key(),
                            record.value()
                    );
                }
            }
        }
    }
}
