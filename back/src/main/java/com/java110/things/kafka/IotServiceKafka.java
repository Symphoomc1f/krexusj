package com.java110.things.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * kafka侦听
 * Created by wuxw on 2018/4/15.
 */
public class IotServiceKafka {

    private final static Logger logger = LoggerFactory.getLogger(IotServiceKafka.class);


    /**
     * 协议
     * {
     *     appId:"",
     *     action:“”,
     *     sign:"",
     *     data:{
     *
     *     },
     *
     * }
     * @param record
     */
    @KafkaListener(topics = {"${kafka.topic}"})
    public void listen(ConsumerRecord<?, ?> record) {
        logger.info("kafka的key: " + record.key());
        logger.info("kafka的value: " + record.value().toString());
        String data = record.value().toString();

    }


}
