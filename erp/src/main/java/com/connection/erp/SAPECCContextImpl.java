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
        setRepositoryIfNull(true);
    }

    public SAPECCContextImpl(JCoDestination destination) throws JCoException {
        this.destination = destination;
        setRepositoryIfNull(true);
    }

    public JCoFunction getFunction(final String functionName) {
        try {
            setRepositoryIfNull(false);
            return repository.getFunction(functionName);
        } catch (final JCoException e) {
            throw new RuntimeException(e);
        }

    }

    public void execute(final JCoFunction function) {
        try {
            if (repository == null) {
                setRepositoryIfNull(true);
                if (repository == null) {
                    throw new RuntimeException("Failed to get the ECC Connection");
                }
            }
            final StopWatch sw = new StopWatch();
            JCoFunction faultToleranceFunction = JCoUtils.faultTolerantWrapper(function);
            faultToleranceFunction.execute(destination);
            sw.stop();
            if (LOGGER.isTraceEnabled())
                LOGGER.trace("execution of {} took {}ms", function.getName(), sw.getTotalTimeMillis());
        } catch (final JCoException e) {

            throw new RuntimeException(e);
        }
    }

    private void setRepositoryIfNull(final boolean forceToGet) {
        if (repository != null) {
            return;
        }

        if (forceToGet) {
            setRepositoryWithMoreRetryTime();
        } else {
            setRepositoryWithSpecifiedRetry();
        }

        if (repository == null) {
            throw new RuntimeException("Unable to get the repository from ERP due to communication error.");
        }

    }

    private void setRepositoryWithMoreRetryTime() {
        int retryCount = 0;
        // Try for 30 second to get the repository
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // Try for 60 second/2 minute to get the repository
        while (repository == null && stopWatch.getTotalTimeMillis() < (120000)) {
            try {
                retryCount++;
                repository = destination.getRepository();
            } catch (final Exception e) {
                try {
                    Thread.sleep(RETRY_WAITING_TIME);
                } catch (final InterruptedException ie) {
                    LOGGER.error("Failed to sleep : {} ", retryCount);
                    Thread.currentThread().interrupt();
                }
                LOGGER.error("Retrying to get the repository from destination : {} ", retryCount);
            }
        }
        if (repository == null) {
            throw new RuntimeException(
                    "Failed to get the repository after retrying for " + retryCount + " times.");
        }
    }

    /**
     * ${tags}
     */
    private int setRepositoryWithSpecifiedRetry() {
        int retryCount = 0;
        while (repository == null && retryCount < NUMBER_OF_RETRY) {
            try {
                retryCount++;
                repository = destination.getRepository();
            } catch (final Exception e) {
                try {
                    Thread.sleep(RETRY_WAITING_TIME);
                    LOGGER.error("Retrying to get the repository from destination : {} ", retryCount);
                } catch (final InterruptedException e1) {
                    Thread.currentThread().interrupt(); // restore interrupt
                }
            }
        }
        return retryCount;
    }
}
