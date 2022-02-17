package com.redis.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisMessageSubscriber implements MessageListener {


    public static List<String> messageList = new ArrayList<String>();
    CurrencyHolder currencyHolder;
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisMessageSubscriber(CurrencyHolder currencyHolder, RedisTemplate<String, Object> redisTemplate) {
        this.currencyHolder = currencyHolder;
        this.redisTemplate = redisTemplate;
    }

//    @Autowired
//    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }

    public void onMessage(Message message, byte[] pattern) {
        messageList.add(message.toString());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Pattern: " + new String(pattern));
        System.out.println("Message received: " + message);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");

        Object object = redisTemplate.getValueSerializer().deserialize(message.getBody());
        CurrencyHolder currencyHolderFromMessage = (CurrencyHolder) object;
        currencyHolder.copyCurrencyHolder(currencyHolderFromMessage);
    }
}
