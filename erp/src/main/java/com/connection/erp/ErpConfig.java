package com.connection.erp;

import com.sap.conn.jco.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErpConfig {

    @Bean
    public JCoDestination getDestination() throws JCoException {
        JCoDestination destination = JCoDestinationManager.getDestination("ECC_RFC_DESTINATION");

        ClassLoader destinationClassLoader = destination.getClass().getClassLoader();
        CustomDeserialization.setCustomClassLoader(destinationClassLoader);

        return destination;
    }
}
