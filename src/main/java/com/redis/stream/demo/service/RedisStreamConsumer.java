package com.redis.stream.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisStreamConsumer {

    private static final String STREAM_KEY = "chat_stream";
    private static final String PROCESSED_SET = "processed_messages";  // Set of processed message IDs

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void consumeMessages() {
        StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();

        // Read all messages from the stream
        List<MapRecord<String, String, String>> messages = streamOps.read(
                StreamReadOptions.empty().count(10),  // You can customize read options here
                // StreamOffset.latest(STREAM_KEY)  // Start from the beginning of the stream
                StreamOffset.fromStart(STREAM_KEY)  // Start from the beginning of the stream
        );
        // prints the entire messages map:
        // System.out.println("Consumed messages: " + messages);
        for (MapRecord<String, String, String> message : messages) {
            processMessageIfNotProcessed(message);
//            RecordId recordId = message.getId();
//            String sessionId = message.getValue().get("sessionId");
//            String chatMessage = message.getValue().get("message");
//            System.out.println("Consumed message: [Session: " + sessionId + "] - " + chatMessage);
            // Delete the message from the stream to prevent re-processing
            // redisTemplate.opsForStream().delete(STREAM_KEY, recordId);
            // this approach has many pitfalls, hence not recommended..
        }
    }

    public void processMessageIfNotProcessed(MapRecord<String, String, String> message) {
        RecordId messageId = message.getId();

        // Check if the message has already been processed
        Boolean alreadyProcessed = redisTemplate.opsForSet().isMember(PROCESSED_SET, messageId.getValue());

        if (!alreadyProcessed) {
            // Process the message
            System.out.println("Processing message: " + message.getValue());

            // Mark the message as processed by adding it to the set
            redisTemplate.opsForSet().add(PROCESSED_SET, messageId.getValue());

            System.out.println("Message marked as processed: " + messageId);
        } else {
            System.out.println("Message already processed, skipping: " + messageId);
        }
    }

    public void consumeMessagesGroup() {
        StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
        try {
            // Create a consumer group
            streamOps.createGroup(STREAM_KEY, ReadOffset.from("0"), "my-consumer-group");
        } catch(Exception e){
            System.out.println("Exception occurred: " + e.getMessage());
        }
        // Read messages as part of a consumer group
        List<MapRecord<String, String, String>> messages = streamOps.read(
                Consumer.from("my-consumer-group", "consumer1"),  // Consumer within the group
                StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed())
        );

        System.out.println("@consumeMessagesGroup() :: Consumed messages: " + messages);
        for (MapRecord<String, String, String> message : messages) {
            RecordId recordId = message.getId();
            String sessionId = message.getValue().get("sessionId");
            String chatMessage = message.getValue().get("message");

            System.out.println("Consumed message: [Session: " + sessionId + "] - " + chatMessage);

            // Optionally acknowledge the message (useful for consumer groups)
            streamOps.acknowledge("my-consumer-group", message);
        }
    }
}