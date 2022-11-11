package com.connection.erp;

import com.sap.conn.jco.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class SAPECCContextImpl {

    public static final String ECC_RFC_DESTINATION = "ECC_RFC_DESTINATION";
    private static final Logger LOGGER = LoggerFactory.getLogger(SAPECCContextImpl.class);
    private static final int RETRY_WAITING_TIME = 3000;
    private static final int NUMBER_OF_RETRY = 3;
    private final JCoDestination destination;

    private JCoRepository repository;

    public SAPECCContextImpl() throws JCoException {
        this.destination = JCoDestinationManager.getDestination(ECC_RFC_DESTINATION);
        setRepositoryIfNull();
    }

    public SAPECCContextImpl(JCoDestination destination) throws JCoException {
        this.destination = destination;
        setRepositoryIfNull();
    }

    public JCoFunction getFunction(final String functionName) throws JCoException {
        if (repository == null) {
            setRepositoryIfNull();
            if (repository == null) {
                throw new RuntimeException("Failed to get the ECC Connection");
            }
        }
        int retryCount = 0;
        JCoException jCoException = null;
        while (retryCount < NUMBER_OF_RETRY) {
            try {
                retryCount++;
                return repository.getFunction(functionName);
            } catch (final Exception e) {
                try {
                    if (e instanceof JCoException) {
                        jCoException = (JCoException) e;
                    }
                    Thread.sleep(RETRY_WAITING_TIME);
                    LOGGER.error("Retrying to get jcoFunction from repository: {} ", retryCount);
                } catch (final InterruptedException e1) {
                    Thread.currentThread().interrupt(); // restore interrupt
                }
            }
        }

        if (jCoException != null) {
            throw jCoException;
        } else {
            throw new RuntimeException("Unable to get get jcoFunction from repository : " + retryCount);
        }
    }

    public void execute(final JCoFunction function) throws JCoException {
        if (repository == null) {
            setRepositoryIfNull();
            if (repository == null) {
                throw new RuntimeException("Failed to get the ECC Connection");
            }
        }

        int retryCount = 0;
        JCoException jCoException = null;
        while (retryCount < NUMBER_OF_RETRY) {
            try {
                retryCount++;
                function.execute(destination);
                return;
            } catch (final Exception e) {
                try {
                    if (e instanceof JCoException) {
                        jCoException = (JCoException) e;
                    }
                    Thread.sleep(RETRY_WAITING_TIME);
                    LOGGER.error("Retrying to execute jcoFunction : {} ", retryCount);
                } catch (final InterruptedException e1) {
                    Thread.currentThread().interrupt(); // restore interrupt
                }
            }
        }

        if (jCoException != null) {
            throw jCoException;
        } else {
            throw new RuntimeException("Unable to execute the jcoFunction: " + function.getName());
        }
    }

    private void setRepositoryIfNull() throws JCoException {
        if (repository != null) {
            return;
        }

        setRepositoryWithSpecifiedRetry();
    }

    /**
     * ${tags}
     */
    private void setRepositoryWithSpecifiedRetry() throws JCoException {
        int retryCount = 0;
        JCoException jCoException = null;
        while (repository == null && retryCount < NUMBER_OF_RETRY) {
            try {
                retryCount++;
                repository = destination.getRepository();
                return;
            } catch (final Exception e) {
                try {
                    if (e instanceof JCoException) {
                        jCoException = (JCoException) e;
                    }
                    Thread.sleep(RETRY_WAITING_TIME);
                    LOGGER.error("Retrying to get the repository from destination : {} ", retryCount);
                } catch (final InterruptedException e1) {
                    Thread.currentThread().interrupt(); // restore interrupt
                }
            }
        }

        if (jCoException != null) {
            throw jCoException;
        } else {
            throw new RuntimeException("Unable to get the repository from destination : " + retryCount);
        }
    }
}
