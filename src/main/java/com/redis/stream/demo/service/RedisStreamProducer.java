package com.redis.stream.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RedisStreamProducer {

    private static final String STREAM_KEY = "chat_stream";  // Stream name

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Send a message to the Redis stream
    public void sendMessage(String sessionId, String message) {
        System.out.println("1. Inside sendMessage():: " + sessionId + ", " + message);
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("sessionId", sessionId);
        messageMap.put("message", message);

        MapRecord<String, String, String> record = StreamRecords.newRecord()
                .ofMap(messageMap)
                .withStreamKey(STREAM_KEY);
        System.out.println("1. Inside sendMessage():: record:: " + record);
        redisTemplate.opsForStream().add(record);

        System.out.println("############ Message sent to stream: " + message);
    }
}