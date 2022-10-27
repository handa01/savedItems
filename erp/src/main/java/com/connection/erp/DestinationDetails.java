package com.connection.erp;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DestinationDetails {

    String asHost;
    String msHost;

    String client;

    public String getAsHost() {
        return asHost;
    }

    public void setAsHost(String asHost) {
        this.asHost = asHost;
    }

    public String getMsHost() {
        return msHost;
    }

    public void setMsHost(String msHost) {
        this.msHost = msHost;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
