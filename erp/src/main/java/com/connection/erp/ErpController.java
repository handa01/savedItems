package com.connection.erp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.conn.jco.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static com.connection.erp.SAPECCContextImpl.ECC_RFC_DESTINATION;

@RestController
public class ErpController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErpController.class);

    ObjectMapper mapper = new ObjectMapper();

    @GetMapping(value = "/getDestination", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode getDestinationDetail() throws JCoException {
        DestinationDetails details = new DestinationDetails();
        JCoDestination destination = JCoDestinationManager.getDestination(ECC_RFC_DESTINATION);
        details.setAsHost(destination.getApplicationServerHost());
        details.setMsHost(destination.getMessageServerHost());
        details.setClient(destination.getClient());

        return mapper.convertValue(details, JsonNode.class);
    }

    @GetMapping("/jcoFunction/{bapiFunction}")
    byte[] jcoFunction(@PathVariable String bapiFunction) throws JCoException {
        System.err.println("Function name = " + bapiFunction);

        SAPECCContextImpl eccCtx = new SAPECCContextImpl();
        final JCoFunction jcoFunction = eccCtx.getFunction(bapiFunction);

        byte[] exBytes = SerializationUtils.serialize(jcoFunction);
        System.err.println("getFunction: Serialization successful");

        return exBytes;
    }

    @GetMapping("/isValidJcoFunction/{bapiFunction}")
    boolean isValidJcoFunction(@PathVariable String bapiFunction) throws JCoException {
        System.err.println("Function name = " + bapiFunction);

        SAPECCContextImpl eccCtx = new SAPECCContextImpl();
        final JCoFunction jcoFunction = eccCtx.getFunction(bapiFunction);

        return jcoFunction != null && jcoFunction.getName() != null && !jcoFunction.getName().equals("");
    }

    @PostMapping("/executeJcoFunction")
    byte[] executeJcoFunction(@RequestBody byte[] bapiFunction) throws JCoException {
        JCoFunction jcoFunction = (JCoFunction) new CustomDeserialization().deserialize(bapiFunction);
        System.err.println("execute: Deserialization successful");
        SAPECCContextImpl eccCtx = new SAPECCContextImpl();
        eccCtx.execute(jcoFunction);
        byte[] exBytes = SerializationUtils.serialize(jcoFunction);

        System.err.println("execute: Serialization successful");

        return exBytes;
    }

    @PostMapping("/executeJcoFunctionWithTransaction")
    byte[] executeJcoFunctionWithTransaction(@RequestBody byte[] bapiFunction) throws JCoException {
        JCoFunction jcoFunction = (JCoFunction) new CustomDeserialization().deserialize(bapiFunction);
        System.err.println("execute(Transaction): Deserialization successful: " + jcoFunction.getName());
        JCoDestination destination = null;
        try {
            destination = JCoDestinationManager.getDestination(ECC_RFC_DESTINATION);

            // begin transaction
            JCoContext.begin(destination);

            SAPECCContextImpl eccCtx = new SAPECCContextImpl(destination);
            eccCtx.execute(jcoFunction);

            byte[] exBytes = SerializationUtils.serialize(jcoFunction);

            System.err.println("execute: Serialization successful");

            return exBytes;
        } finally {
            if (destination != null) {
                JCoContext.end(destination);
            }
        }
    }


    @PostMapping("/executeJcoFunctionWithTransactionAndCommit")
    byte[] executeJcoFunctionWithTransactionAndCommit(@RequestBody byte[] bapiFunction) throws JCoException {
        JCoFunction jcoFunction = (JCoFunction) new CustomDeserialization().deserialize(bapiFunction);
        System.err.println("execute(TransactionAndCommit): Deserialization successful: " + jcoFunction.getName());
        JCoDestination destination = null;
        try {
            ArrayList<JCoFunction> jCoFunctionList = new ArrayList<>();
            destination = JCoDestinationManager.getDestination(ECC_RFC_DESTINATION);

            // begin transaction
            JCoContext.begin(destination);

            SAPECCContextImpl eccCtx = new SAPECCContextImpl(destination);

            JCoFunction commitFunction = eccCtx.getFunction("BAPI_TRANSACTION_COMMIT");

            // Sales Order Create
            eccCtx.execute(jcoFunction);

            jCoFunctionList.add(jcoFunction);

            // Find errors
            JCoParameterList tables = jcoFunction.getTableParameterList();
            boolean errorHappened = false;
            final JCoTable result = tables.getTable("RETURN");
            final int numRows = result.getNumRows();
            for (int i = 0; i < numRows; i++) {
                result.setRow(i);

                final String msgType = result.getString("TYPE");
                String message = result.getString("MESSAGE");
                if ("E".equals(msgType) || "A".equals(msgType)) {
                    System.err.println("Error message: " + message);
                    errorHappened = true;
                }
            }

            // execute the commit function if validation passes
            if (!errorHappened) {
                eccCtx.execute(commitFunction);
                jCoFunctionList.add(commitFunction);
            } else {
                LOGGER.error("Error message found. Skipping commit");
            }

            byte[] exBytes = SerializationUtils.serialize(jCoFunctionList);

            System.err.println("execute: Serialization successful");

            return exBytes;
        } finally {
            // end context
            if (destination != null) {
                JCoContext.end(destination);
            }
        }
    }
}
