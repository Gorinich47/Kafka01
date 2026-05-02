--------Методы отправки сообщений------------

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

-------------Методы получения------------

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

-------------Admin API--------------
1. Создание нового топика
admin.createTopics(Collection.singletonList(new Topic("Имя_топика", 3, (short) 1)));
2. Удаление топика
admin.deleteTopics(Collection.singletonList("Имя_топика"));
3. Получение информации о партициях
DescribeTopicsResult res = admin.describeTopics(Collection.singletonList("Имя_топика"));

--- ACL и квоты
ACL (Access Control Lists) - механизм обеспечивающий безопасность на уровне топиков.
Это механизм авторизации, который определяет кто и что может делать в кластере.

Пример - запрещаем доступ если не админ
super.user = User:admin;User:kafka  --предоставляет доступ только для админов
authorize.class.name=kafka.security.authorizer.AclAuthorizer --доступ закрыт для всех без ACL

--- Проблема дублирования сообщений
Future<RecordMetadata> future = producer.send(record);

Решение №1 - используем идемпотентный Producer
Идемпотентность - это свойство операций, при котором многократное повторение даёт одинаковый результат.
Properties props = new Properties();
props.put("bootstrap.servers", "localhost":9092);
props.put("enable.idempotence", true); --главная настройка
props.put("acks", "all"); --обязательно all

KafkaProducer<?,?> producer = new KafkaProducer<>(props);

Решение №2 - использование транзакций
Транзакция означает, что все операции внутри одной транзакции будут применены сразу все (атомарность), либо ни одна не выполняется.

Как работать с транзакциями:
--Этап инициализации
1. Продюсеру назначается transactional.id
2. При первом запуске продюсер регистрируется у Транзакционного координатора.
Координатор записывает сопоставление transactional.id и идентификатора продюсера(PID) в специальный топик  __transaction_state
3. Это позволяет координировать работу одного и того же продюсера при перезапусках.
--Начало транзакции
1. Продюсер вызывает метод: producer.initTransactions()
2. Для каждой новой транзакции producer.beginTransactions()
3. В транзакции выполняем действия и отправляем сообщения в топик
4. Если сохраняем результат - producer.commitTransactions()
5. Если хотим откатить транзакцию - producer.abortTransactions()
