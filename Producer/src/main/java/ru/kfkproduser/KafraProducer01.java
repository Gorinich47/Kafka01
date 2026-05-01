package ru.kfk.kfk;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kfk.kfk.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;


/**
 * Поставщик данных
 */
public class KafraProducer01 {
    // создание логера для класса
    private static final Logger logger = LoggerFactory.getLogger(KafraProducer01.class);
    private static final int MAX_MSG = 10;
    public static void main(String[] args) {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaConfig.getProducerConfig())){
            for (int i=0; i<MAX_MSG; i++) {
                // Каждое сообщение класса это ProducerRecord
                ProducerRecord<String,String> producerRec = new ProducerRecord<>(
                        KafkaConfig.TOPIC_NAME,
                        "key"+i,
                        "val-msg "+i
                );
                // отправка сообщения в топик
                // Выполняется асинхронная отправка сообщения без обработки ответа
                producer.send(producerRec);

                // запишем в лог отправку сообщения
                logger.info("Отправлено: key={}, value={}",i, "val-msg "+i);
            }
            logger.info("Все данные успешно переданы");
        } catch(Exception e) {
            logger.error("Ошибка при отправке сообщений в кафку",e);
        }
    }
}
