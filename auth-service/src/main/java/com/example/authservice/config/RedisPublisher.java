package com.example.authservice.config;

import com.example.authservice.DTO.ParticipantDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic topic;

    @Autowired
    public RedisPublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.topic = new ChannelTopic("channel-participant");
    }

    public void publish(ParticipantDTO message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }


}