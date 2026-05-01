package ru.kfk;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kfk.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.concurrent.Future;


/**
 * Поставщик данных
 */
public class KafraProducer02 {
    // создание логера для класса
    private static final Logger logger = LoggerFactory.getLogger(KafraProducer02.class);
    private static final int MAX_MSG = 200;
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
                // send возвращает объект Future<RecordMetadata> - это асинхронный вызов
                //  RecordMetadata содержит:
                //0. topic - имя топика
                //1. partition - номер партиции, в которую записано сообщение
                //2. offset - номер позиции в партиции
                //3. timestamp - временная метка

                Future<RecordMetadata> result = producer.send(producerRec);
                RecordMetadata md =result.get();
                // запишем в лог отправку сообщения
                logger.info("Отправлено: key={}, value={} metadata (topic={}, partition={}, offset={}, timestamp={})",
                        producerRec.key(),
                        producerRec.value(),
                        md.topic(),
                        md.partition(),
                        md.offset(),
                        md.timestamp()
                );
            }
            logger.info("Все данные успешно переданы");
        } catch(Exception e) {
            logger.error("Ошибка при отправке сообщений в кафку",e);
        }
    }
}
