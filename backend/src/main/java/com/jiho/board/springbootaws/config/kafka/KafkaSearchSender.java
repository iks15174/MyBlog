package com.jiho.board.springbootaws.config.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.jiho.board.springbootaws.aop.postcommit.PostCommit;

@Component
public class KafkaSearchSender {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${kafka.search.topic.name}")
    private String topicName;

    @PostCommit
    public void send(SearchOpType searchOpType, Long target) {

        Message<String> message = MessageBuilder
                .withPayload(searchOpType.name() + "," + target)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();

        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> stringObjectSendResult) {
                System.out.println("Sent message=[" + stringObjectSendResult.getProducerRecord().value() +
                        "] with offset=[" + stringObjectSendResult.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=[] due to : " + ex.getMessage());
            }
        });
    }
}
