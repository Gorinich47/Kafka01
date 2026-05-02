package ru.kfkproduser;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kfkproduser.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;


/**
 * Поставщик данных, асинхронный вызов Callback.
 *
 */
public class KafraProducer04 {
    // создание логера для класса
    private static final Logger logger = LoggerFactory.getLogger(KafraProducer04.class);
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

                producer.send(producerRec, (md, ex) ->{
                    if(ex != null){
                        logger.warn("Отправлено: key={}, value={}",
                                producerRec.key(),
                                producerRec.value(),
                                ex.getMessage());
                    } else {
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
                });

            }
            logger.info("Все данные успешно переданы");
        } catch(Exception e) {
            logger.error("Ошибка при отправке сообщений в кафку",e);
            Thread.currentThread().interrupt(); // разрываем соединение
        }
    }
}
