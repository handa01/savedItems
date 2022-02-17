package com.redis.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {

    @Autowired
    RedisMessagePublisher redisMessagePublisher;

    @Autowired
    CurrencyHolder currencyHolder;

    @GetMapping("hi")
    public String hello() {
        return "hello world";
    }

    @GetMapping("/addCurrency/{currency}")
    public String addCurrency(@PathVariable("currency") String currency) {
        Currency currencyObj = new Currency(currency);
        currencyHolder.addCurrency(currencyObj);

        return "success";
    }

    @GetMapping("/publishCurrency/{currency}")
    public String publishCurrency(@PathVariable("currency") String currency) {
        try {
            Currency currencyObj = new Currency(currency);
            currencyHolder.addCurrency(currencyObj);
            redisMessagePublisher.publish(currencyHolder);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
        return "success";
    }
}
