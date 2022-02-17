package com.redis.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    CurrencyHolder currencyHolder;

    @Scheduled(fixedDelay = 10000, initialDelay = 20000)
    public void printBean() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(currencyHolder);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
    }
}
