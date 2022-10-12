package com.connection.erp;

import com.sap.conn.jco.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyECCContextImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyECCContextImpl.class);

    private final JCoDestination destination;

    private JCoRepository repository;

    public MyECCContextImpl() throws JCoException {
        this.destination = JCoDestinationManager.getDestination("ECC_RFC_DESTINATION");;
        LOGGER.error("destination found: " + destination);
        setRepositoryWithMoreRetryTime(destination);
        LOGGER.error("Repo set successfully");
    }

    public JCoFunction getFunction(final String functionName)
    {
        int retryCount = 0;
        Exception exception = null;
        while (retryCount < 3) {
            try {
                retryCount++;
                final JCoFunction function = repository.getFunction(functionName);
                return function;
            } catch (final JCoException e) {
                LOGGER.error("----JcoException----, Retry: " + retryCount, e);
                exception = e;
            }
        }
        throw new RuntimeException(exception);
    }

    public void execute(final JCoFunction function)
    {
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                retryCount++;
                function.execute(destination);
                return;
            } catch (final JCoException e) {
                LOGGER.error("execute Function error", e);
            }
        }
    }

    private String setRepositoryWithMoreRetryTime(JCoDestination destination)
    {
        int retryCount = 0;
        // Try for 30 second to get the repository
        // Try for 60 second/2 minute to get the repository
        Exception ex = null;
        while (repository == null && retryCount < 3)
        {
            try
            {
                retryCount++;
                repository = destination.getRepository();
            }
            catch (final Exception e)
            {
                ex = e;
                try
                {
                    Thread.sleep(3000);
                }
                catch (final InterruptedException ie)
                {
                    LOGGER.error("Failed to sleep : {} ", retryCount);
                    Thread.currentThread().interrupt();
                }
                LOGGER.error("Retrying to get the repository from destination : {} ", retryCount);
            }
        }
        if (repository == null)
        {
            if (ex != null)
            {
                LOGGER.error("Error while connecting to ERP system", ex);
                return ex.getMessage();
            }
        }
        return "success";
    }
}
