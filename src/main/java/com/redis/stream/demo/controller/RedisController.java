package com.redis.stream.demo.controller;

import com.redis.stream.demo.service.RedisStreamConsumer;
import com.redis.stream.demo.service.RedisStreamProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/redis")
public class RedisController {

    Logger logger = LoggerFactory.getLogger("RedisController.class");
    @Autowired
    private RedisStreamProducer streamProducer;

    @Autowired
    private RedisStreamConsumer streamConsumer;

    // UI sends messages to this end-point
    // http://localhost:9095/redis/send/{sessionId}
    @PostMapping("/send/{sessionId}")
    public String sendMessage(@PathVariable String sessionId, @RequestBody String message) {
        streamProducer.sendMessage(sessionId, message);
        System.out.println("@sendMessage() :: Message sent to chat stream for session " + sessionId);
        return "redis-stream"; // forward to publish message UI again..
    }

    // Call this end-point to consume messages in the stream...
    // http://localhost:9095/redis/consume
    @GetMapping("/consume")
    public String consumeMessages() {
        streamConsumer.consumeMessages();
        System.out.println("@consumeMessages():: Messages consumed");
        return "redis-stream-consumed";
    }

    // Call this end-point to consume messages in the stream...
    // http://localhost:9095/redis/consume-group
    @GetMapping("/consume-group")
    public String consumeMessageGroup() {
        streamConsumer.consumeMessagesGroup();
        System.out.println("@consumeMessageGroup():: Messages consumed");
        return "redis-stream-consumed";
    }

    // We are not using this method to consume messages..
    // only use it to publish messages using UI.
    // http://localhost:9095/redis/stream
    @GetMapping("/stream")
    public String stream(@RequestParam(required=false) String message, @RequestParam(required=false) Integer channelNum){
        if(message != null){
            logger.debug("##########pubSub() publishing message:: " + message );
        }
        else{
            logger.debug("##########pubSub()........." );
        }
        return "redis-stream";
    } // end of stream()
}