package com.connection.erp;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.Map;

@RestController
public class ErpController {

    // 0000001000
    @GetMapping("/erp/{bapiFunction}")
    byte[] erp(@PathVariable String bapiFunction, @RequestParam Map<String, String> reqParams) throws JCoException {
        MyECCContextImpl eccCtx = new MyECCContextImpl();
        final JCoFunction jcoFunction = eccCtx.getFunction(bapiFunction);
        final JCoParameterList importParam = jcoFunction.getImportParameterList();

        if (reqParams != null && reqParams.size() > 0) {
            for (String key : reqParams.keySet()) {
                importParam.setValue(key, reqParams.get(key));
            }
        }

        eccCtx.execute(jcoFunction);

        final JCoParameterList exportParam = jcoFunction.getExportParameterList();

//        System.err.println("-----------------------------");
//        System.err.println("export param successful: class = [" + exportParam.getClass() + "]"
//                + exportParam);
//
//        String result = exportParam.getStructure("RETURN").getString("TYPE");
//        System.err.println("result=" + result);
//        final JCoStructure customerAddress = exportParam.getStructure("CUSTOMERADDRESS");
//        System.err.println("customerAddress=" + customerAddress);
//
//        Class JMgrklass = exportParam.getClass();
//        URL JMgrLocation = JMgrklass.getResource('/' + JMgrklass.getName().replace('.', '/') + ".class");
//        System.err.println("JCoParameterList coming from");
//        System.err.println(JMgrLocation);

        byte[] exBytes = SerializationUtils.serialize(exportParam);
        System.err.println("Serialization successful");

//        JCoParameterList result2 = (JCoParameterList) SerializationUtils.deserialize(exBytes);
//        String eResult2 = result2.getStructure("RETURN").getString("TYPE");
//        final JCoStructure customerAddress2 = result2.getStructure("CUSTOMERADDRESS");

        return exBytes;
    }

    @GetMapping("/geterp")
    String erp() throws JCoException {
        fetchERPResponse();
        return "ok";
    }
    public static JCoParameterList fetchERPResponse() {
        final RestTemplate restTemplate = new RestTemplate();

        // CF
        //final String fooResourceUrl = "https://testconn.cfapps.eu10.hana.ondemand.com/erp/BAPI_CUSTOMER_GETDETAIL2";

        // Neo
        String fooResourceUrl = "https://springrestexamplextvs7s4vl2.hana.ondemand.com/erp-0.0.1-SNAPSHOT/erp/BAPI_CUSTOMER_GETDETAIL2";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("CUSTOMERNO", "0000001000");

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(fooResourceUrl).queryParams(map);
        String uriBuilder = builder.build().encode().toUriString();

        final ResponseEntity<byte[]> response = restTemplate.getForEntity(uriBuilder, byte[].class);


        final JCoParameterList exportParam = (JCoParameterList) SerializationUtils.deserialize(response.getBody());

        return exportParam;
    }
}
