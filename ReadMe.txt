Методы отправки сообщений:

1. Отправил и забыл
try(KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaConfig.getProducerConfig())){
    producer.send(record)
    logger.info("Все данные успешно переданы");
} catch (Exception e)){}

2. Синхронная отправка
try(KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaConfig.getProducerConfig())){

    // send возвращает объект Future<RecordMetadata> - это асинхронный вызов
    //  RecordMetadata содержит:
    //0. topic - имя топика
    //1. partition - номер партиции, в которую записано сообщение
    //2. offset - номер позиции в партиции
    //3. timestamp - временная метка

    RecordMataDate md = producer.send(record).get();
    logger.info("Отправлено: key={}, value={} metadata (topic={}, partition={}, offset={}, timestamp={})" ,
                            producerRec.key(),
                            producerRec.value(),
                            md.topic(),
                            md.partition(),
                            md.offset(),
                            md.timestamp()
                    );
} catch (Exception e){}

3. Асинхронная отправка с Callback
public void sendCustomData(ProducerRecord<String, String> record){
    try(KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaConfig.getProducerConfig())){
        // Можно отдельным void методом реализовать
        producer.send(producerRec, new Callback(){
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                // обработчик ответов от сервера

                // Логирование
                logger.info(String.format("Топик %s Партиция %s Офсет %s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset())"");
            }
        });

    } catch (Exception e){}
}


За создание партиций в топике отвечает администратор кластера, либо оставляем всё по умолчанию.
Он может явно указать количество партиций в каждом топике
Properties properties = KafkaConfig.getProducerConfig()
try(AdminClient adminC = AdminClient.create(properties)){

    //Создание группы топиков
    NewTopic topic1 = new NewTopic("topic1", 5); /* имя топика и кол-во партиций*/
    NewTopic topic2 = new NewTopic("topic1", 15); /* имя топика и кол-во партиций*/
    NewTopic topic3 = new NewTopic("topic1", 3); /* имя топика и кол-во партиций*/

    CreateTopicsResult = result = adminC.createTopics(List.of(topic1, topic2, topic3));
    result.all().get;

}

