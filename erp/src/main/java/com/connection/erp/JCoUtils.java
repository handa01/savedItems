package com.connection.erp;

import com.sap.conn.jco.AbapClassException.Mode;
import com.sap.conn.jco.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public final class JCoUtils {

    public static JCoFunction faultTolerantWrapper(final JCoFunction function) {
        return new FaultTolerantJCoFunction(function);
    }

    static boolean xFlag2bool(final String val) {
        return "X".equals(val);
    }

    public static final class FaultTolerantJCoFunction implements JCoFunction {

        private static final long serialVersionUID = 1L;

        private static final Logger LOGGER = LoggerFactory.getLogger(FaultTolerantJCoFunction.class);

        private static final int DEFAULT_ATTEMPTS = 3;

        private static final int DEFAULT_TIMEOUT = 2;

        private static final int DEFAULT_SLEEP = 30;

        private final JCoFunction function;

        /**
         * Maximum number of retries.
         */
        private final int maxAttempts;

        /**
         * Timeout in minutes for a function to execute.
         */
        private final int timeout;

        /**
         * Number of seconds to wait when a communication problem has been
         * detected.
         */
        private final int sleep;

        private final boolean validFunction;

        private FaultTolerantJCoFunction(final JCoFunction function) {
            this(function, DEFAULT_ATTEMPTS, DEFAULT_TIMEOUT, DEFAULT_SLEEP);
        }

        private FaultTolerantJCoFunction(final JCoFunction function, final int maxAttempts, final int timeout,
                                         final int sleep) {
            this.maxAttempts = maxAttempts;
            this.function = function;
            this.timeout = timeout;
            this.sleep = sleep;
            validFunction = isFunctionFound(function);
        }

        public static boolean isNullorEmpty(final Object obj) {
            return obj == null || obj.equals("");
        }

        @Override
        public JCoFunction clone() {
            return new FaultTolerantJCoFunction(function, maxAttempts, timeout, sleep);
        }

        private void execute(final Callable<Void> func) throws JCoException {

            final ExecutorService es = Executors.newSingleThreadExecutor();

            int attempt = 0;
            boolean success = false;
            Exception lastException = null;

            try {

                while (attempt < maxAttempts && !success) {

                    try {

                        attempt++;
                        es.submit(func).get(timeout, TimeUnit.MINUTES);
                        success = true;

                    } catch (final ExecutionException e) {

                        final Throwable cause = e.getCause();

                        if (cause instanceof JCoException) {

                            final JCoException jcoe = (JCoException) cause;
                            lastException = jcoe;

                            if (isAbapException(jcoe))
                                throw jcoe;

                            if (LOGGER.isDebugEnabled())
                                LOGGER.debug("error during function execution of {}, attempt {}/{}", getName(), attempt,
                                        maxAttempts);

                            if (isCommunicationProblem(jcoe)) {

                                if (attempt < maxAttempts) {

                                    LOGGER.warn("communication problem detected - waiting for {}s", sleep);

                                    try {
                                        Thread.sleep(TimeUnit.SECONDS.toMillis(sleep));
                                    } catch (final InterruptedException e1) {
                                        if (LOGGER.isDebugEnabled())
                                            LOGGER.debug("interrupted while waiting");
                                    }

                                } else {

                                    LOGGER.error("communication problem detected", jcoe);

                                }

                            }

                        } else {

                            //Throwables.propagate(e);
                            throw new RuntimeException(e);

                        }

                    } catch (final TimeoutException e) {

                        lastException = e;
                        LOGGER.warn("timeout while waiting for result of {}, attempt {}/{}", getName(), attempt,
                                maxAttempts);

                    } catch (final InterruptedException e) {
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("thread interrupted while waiting for result of {}, attempt {}/{}", getName(),
                                    attempt, maxAttempts);

                    }

                }

                if (!success) {

                    LOGGER.warn("giving up after {} failed attempts", attempt);

                    if (lastException != null) {
                        throw new RuntimeException(lastException);
                        //Throwables.propagate(lastException);
                    } else
                        throw new RuntimeException("failed to execute " + getName());

                }

            } finally {

                es.shutdown();

            }

        }

        @Override
        public void execute(final JCoDestination arg0) throws JCoException {
            execute(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    function.execute(arg0);
                    return null;
                }
            });
        }

        @Override
        public void execute(final JCoDestination arg0, final String arg1) throws JCoException {
            execute(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    function.execute(arg0, arg1);
                    return null;
                }
            });
        }

        @Override
        public void execute(final JCoDestination arg0, final String arg1, final String arg2) throws JCoException {
            execute(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    function.execute(arg0, arg1, arg2);
                    return null;
                }
            });
        }

        @Override
        public JCoParameterList getChangingParameterList() {
            return function.getChangingParameterList();
        }

        @Override
        public AbapException getException(final String arg0) {
            return function.getException(arg0);
        }

        @Override
        public AbapException[] getExceptionList() {
            return function.getExceptionList();
        }

        @Override
        public JCoParameterList getExportParameterList() {
            return function.getExportParameterList();
        }

        @Override
        public JCoFunctionTemplate getFunctionTemplate() {
            return function.getFunctionTemplate();
        }

        @Override
        public JCoParameterList getImportParameterList() {
            return function.getImportParameterList();
        }

        @Override
        public String getName() {
            return function.getName();
        }

        @Override
        public JCoParameterList getTableParameterList() {
            return function.getTableParameterList();
        }

        @Override
        public boolean isAbapClassExceptionEnabled() {
            return function.isAbapClassExceptionEnabled();
        }

        private boolean isAbapException(final JCoException e) {
            return JCoException.JCO_ERROR_ABAP_EXCEPTION == e.getGroup();
        }

        private boolean isCommunicationProblem(final JCoException e) {
            return JCoException.JCO_ERROR_COMMUNICATION == e.getGroup();
        }

        private boolean isFunctionFound(final JCoFunction function) {
            boolean functionExist = false;
            try {
                final String functionName = function.getName();
                if (!isNullorEmpty(functionName)) {
                    functionExist = true;
                }
            } catch (final NullPointerException e) {
                functionExist = false;
            }
            return functionExist;
        }

        public boolean isValidFunction() {
            return validFunction;
        }

        @Override
        public void setAbapClassExceptionMode(final Mode arg0) {
            function.setAbapClassExceptionMode(arg0);
        }

        @Override
        public String toXML() {
            return function.toXML();
        }

    }

}
