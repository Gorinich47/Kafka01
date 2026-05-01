Методы отправки сообщений:

1. Отправил и забыл
try(KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaConfig.getProducerConfig())){
    producer.send(record)
    logger.info("Все данные успешно переданы");
} catch (Exception e)){}

2. Синхронная отправка
try(KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaConfig.getProducerConfig())){

    RecordMataDate md = producer.send(record).get();
    logger.info(String.format("Топик %s Партиция %s Офсет %s", md.topic(), md.partition(), md.offset())"");
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