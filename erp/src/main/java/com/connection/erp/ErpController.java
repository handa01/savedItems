package com.connection.erp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.conn.jco.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import static com.connection.erp.SAPECCContextImpl.ECC_RFC_DESTINATION;

@RestController
public class ErpController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErpController.class);

    ObjectMapper mapper = new ObjectMapper();


//    public static JCoParameterList fetchERPResponse() {
//        final RestTemplate restTemplate = new RestTemplate();
//
//        // CF
//        final String fooResourceUrl = "https://testconn.cfapps.eu10.hana.ondemand.com/erp/BAPI_CUSTOMER_GETDETAIL2";
//
//        // Neo
//        //String fooResourceUrl = "https://springrestexamplextvs7s4vl2.hana.ondemand.com/erp-0.0.1-SNAPSHOT/erp/BAPI_CUSTOMER_GETDETAIL2";
//
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("CUSTOMERNO", "0000001000");
//
//        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(fooResourceUrl).queryParams(map);
//        String uriBuilder = builder.build().encode().toUriString();
//
//        final ResponseEntity<byte[]> response = restTemplate.getForEntity(uriBuilder, byte[].class);
//
//
//        final JcoWrapper exportParam = (JcoWrapper) SerializationUtils.deserialize(response.getBody());
//
//        return null;
//    }

    // 0000001000
//    @GetMapping("/erp/{bapiFunction}")
//    byte[] erp(@PathVariable String bapiFunction, @RequestParam Map<String, String> reqParams) throws JCoException {
//        MyECCContextImpl eccCtx = new MyECCContextImpl(destination);
//        final JCoFunction jcoFunction = eccCtx.getFunction(bapiFunction);
//        final JCoParameterList importParam = jcoFunction.getImportParameterList();
//
//        if (reqParams != null && reqParams.size() > 0) {
//            for (String key : reqParams.keySet()) {
//                importParam.setValue(key, reqParams.get(key));
//            }
//        }
//
//        final JCoParameterList exportParamBefore = jcoFunction.getExportParameterList();
//        eccCtx.execute(jcoFunction);
//
//        final JCoParameterList exportParam = jcoFunction.getExportParameterList();
//
//        //JcoWrapper wrapper = new JcoWrapper(exportParam);
//
//        System.err.println("-----------------------------");
//        System.err.println("export param successful: class = [" + exportParam.getClass() + "]"
//                + exportParam);
//
//        String result = exportParam.getStructure("RETURN").getString("TYPE");
//        //System.err.println("result=" + result);
//        final JCoStructure customerAddress = exportParam.getStructure("CUSTOMERADDRESS");
//        //System.err.println("customerAddress=" + customerAddress);
//
//        Class JMgrklass = exportParam.getClass();
//        URL JMgrLocation = JMgrklass.getResource('/' + JMgrklass.getName().replace('.', '/') + ".class");
//        System.err.println("JCoParameterList coming from");
//        System.err.println(JMgrLocation);
//
//        byte[] exBytes = SerializationUtils.serialize(exportParam);
//        System.err.println("Serialization successful");
//
////        JCoParameterList result2 = (JCoParameterList) SerializationUtils.deserialize(exBytes);
////        String eResult2 = result2.getStructure("RETURN").getString("TYPE");
////        final JCoStructure customerAddress2 = result2.getStructure("CUSTOMERADDRESS");
//
//        return exBytes;
//    }

    @GetMapping("/jcoFunction/{bapiFunction}")
    byte[] jcoFunction(@PathVariable String bapiFunction) throws JCoException {
        System.err.println("Function name = " + bapiFunction);

        SAPECCContextImpl eccCtx = new SAPECCContextImpl();
        final JCoFunction jcoFunction = eccCtx.getFunction(bapiFunction);

//        ClassLoader classLoader = jcoFunction.getClass().getClassLoader();
//        CustomDeserialization.setCustomClassLoader(classLoader);
//
//        Class JMgrklass = jcoFunction.getClass();
//        URL JMgrLocation = JMgrklass.getResource('/' + JMgrklass.getName().replace('.', '/') + ".class");
//        System.err.println("JcoFunction is coming from");
//        System.err.println(JMgrLocation);

//        if(jcoFunction instanceof AbapFunction) {
//            System.err.println("jcoFunction is Abapfunction");
//        } else {
//            System.err.println("jcoFunction is not Abapfunction");
//        }

        byte[] exBytes = SerializationUtils.serialize(jcoFunction);
        System.err.println("getFunction: Serialization successful");

        return exBytes;
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
    byte[] executeJcoFunctionWithTransaction(@RequestBody byte[] bapiFunction) {
        JCoFunction jcoFunction = (JCoFunction) new CustomDeserialization().deserialize(bapiFunction);
        System.err.println("execute: Deserialization successful");
        JCoDestination destination = null;
        try {
            destination = JCoDestinationManager.getDestination(ECC_RFC_DESTINATION);
            SAPECCContextImpl eccCtx = new SAPECCContextImpl(destination);


            // begin transaction
            JCoContext.begin(destination);
            eccCtx.execute(jcoFunction);

            byte[] exBytes = SerializationUtils.serialize(jcoFunction);

            System.err.println("execute: Serialization successful");

            return exBytes;
        } catch (JCoException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (destination != null) {
                    JCoContext.end(destination);
                }
            } catch (JCoException e) {
                LOGGER.error("Exception during context end", e);
            }
        }
    }

    @GetMapping(value = "/getDestination", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode getDestinationDetail() throws JCoException {
        DestinationDetails details = new DestinationDetails();
        JCoDestination destination = JCoDestinationManager.getDestination(ECC_RFC_DESTINATION);
        details.setAsHost(destination.getApplicationServerHost());
        details.setMsHost(destination.getMessageServerHost());
        details.setClient(destination.getClient());

        return mapper.convertValue(details, JsonNode.class);
    }


//    @GetMapping("/geterp")
//    String erp() throws JCoException {
//        fetchERPResponse();
//        return "ok";
//    }

//    @GetMapping("/getJcoFunc")
//    String jcoFuc() throws JCoException {
//        final RestTemplate restTemplate = new RestTemplate();
//
//        // CF
//        final String fooResourceUrl = "https://testconn.cfapps.eu10.hana.ondemand.com/jcoFunction/BAPI_CUSTOMER_GETDETAIL2";
//
//        // Neo
//        //String fooResourceUrl = "https://springrestexamplextvs7s4vl2.hana.ondemand.com/erp-0.0.1-SNAPSHOT/erp/BAPI_CUSTOMER_GETDETAIL2";
//
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
////        map.add("CUSTOMERNO", "0000001000");
//
//        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(fooResourceUrl).queryParams(map);
//        String uriBuilder = builder.build().encode().toUriString();
//
//        final ResponseEntity<byte[]> response = restTemplate.getForEntity(uriBuilder, byte[].class);
//
//
//        final JCoFunction exportParam = (JCoFunction) SerializationUtils.deserialize(response.getBody());
//        return "ok";
//    }
}
