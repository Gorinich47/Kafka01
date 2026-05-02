package ru.kfkconsumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

public class KafkaConfig {
    /* Имя топика в который будут отправляться сообщения */
    public static final String TOPIC_NAME = "topic1";
    private static final String BOOTSTRAP_SERVERS = "localhost:9093";
    private static final String GROUP_ID="my-consumer-group";


    private KafkaConfig(){
    }

    //    создаем статический метод возвращающий конфигурацию для продюсера Kafka
    public static Properties getConsumerConfig(){
        Properties properties = new Properties();
        // устанавливаем адреса кафка брокеров для подключения
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        // регистрация группы слушателей
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

         // Установили класс для десериализации ключей сообщений (byte[] -> String)
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // Установили класса десирилизации значений
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());

        /*
         * Разные настройки можно посмотреть тут:
         */


        //Управление поведением получателя данных при первом подключении к топику иои при потере сохранения смещения
        // earliest - считываем сообщение с самого начала топика
        // latest - считываем сообщения с самого последнего доступного смещения
        // none - если нет ни одного доступного смещения, то выбрашиваем исключение
        // error - будет ошибка если нет доступного смещения (offset)
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        return properties;
    }
}
