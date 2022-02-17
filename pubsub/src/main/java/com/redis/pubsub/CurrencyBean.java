package com.redis.pubsub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
public class CurrencyBean {

    @Bean
    public CurrencyHolder currencyHolder() {
        Currency inr = new Currency("INR");
        CurrencyHolder currencyHolder = new CurrencyHolder();
        currencyHolder.addCurrency(inr);

        return currencyHolder;
    }
}

class Currency implements Serializable {
    private String symbol;

    String getSymbol() {
        return symbol;
    }

    public Currency(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return '{' + symbol +  '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(symbol, currency.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}

class CurrencyHolder implements Serializable{
    private final List<Currency> currencyList;

    public CurrencyHolder() {
        currencyList = new ArrayList<>();
    }

    public void addCurrency(Currency currency) {
        currencyList.add(currency);
    }

    public void copyCurrencyHolder(CurrencyHolder currencyHolder) {
        List<Currency> diff = new ArrayList<>();
        currencyHolder.currencyList.forEach(c -> {
            if(!currencyList.contains(c)) {
                diff.add(c);
            }
        });
        currencyList.addAll(diff);
    }

    @Override
    public String toString() {
        return "currencyList=" + currencyList;
    }

    public void clear() {
        currencyList.clear();
    }
}
