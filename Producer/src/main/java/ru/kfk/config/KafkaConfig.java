package ru.kfk.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaConfig {
    /* Имя топика в который будут отправляться сообщения */
    public static final String TOPIC_NAME = "topic1";
    private static final String BOOTSTRAP_SERVERS = "localhost:9093";
    //private static final String GROUP_ID="group1";


    private KafkaConfig(){
    }

//    создаем статический метод возвращающий конфигурацию для продюсера Kafka
    public static Properties getProducerConfig(){
        Properties properties = new Properties();
        // устанавливаем адреса кафка брокеров для подключения
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        /* Устанавливаем уровень надежности
        * - At-most-Once - не более одного раза (максимум 1 раз), сообщение может быть потеряно, и повторно обработано не будет.
        *       Продюсер отправляет сообщение и не ждёт подтверждение от брокера
        *       Продюсер: send() -> не ждет ack (пожтверждение) -> возможна потеря данных
        *       Консьюмер: read() -> коммитит offset -> возможны потери при подении
        *       Проблема - высокий риск потери данных
        *       Потери    - Да
        *       Скорость  - высокая
        *       Дубликаты - нет
        *
        * - At-leat-Once - хотя бы один раз (минимум 1 раз). Сообщение гарантированно доставлено, но может быть обработано повторно
        *       Продюсер - ждет подтверждения от всех реплик
        *       Возможны дубликаты сообщений
        *       Потери    - Нет
        *       Скорость - средняя
        *       Дубликаты - да
        *
        * - Exactly-Once - ровно один раз. Гарантировано обработает ровно 1 раз.
        *       Потери    - нет
        *       Скорость - Низкая
        *       Дубликаты - нет
        */
        // Уточняем через acks гарантию доставки.
        //acks = 0 - Продюсер не будет ждать подтверждения брокера
        //acks = 1 - Продюсер будет ждать подтверждения от лидера партиции, но не от всех реплик
        //acks = all- Продюсеру будет ждать от всех партиций, включая реплики
        properties.put(ProducerConfig.ACKS_CONFIG, "all");

        // Установили класс для сериализации ключей сообщений (String -> byte[])
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Установили класса сирилизации значений
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        /*
         * Разные настройки можно посмотреть тут:
         */

        return properties;
    }
}
