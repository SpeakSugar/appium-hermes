package com.ringcentral.hermes.util;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.ringcentral.hermes.exception.HermesException;
import org.openqa.selenium.WebDriverException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class RetryUtil {

    public static <T> T call(Callable<T> callable, Predicate<T> resultPredicate) {
        try {
            return RetryerBuilder.<T>newBuilder()
                    .retryIfResult(resultPredicate::test)
                    .retryIfExceptionOfType(WebDriverException.class)
                    .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                    .build().call(callable);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (RetryException e) {
            Throwable exceptionCause = e.getLastFailedAttempt().getExceptionCause();
            if (exceptionCause instanceof Error) {
                throw (Error) exceptionCause;
            } else {
                throw (RuntimeException) exceptionCause;
            }
        }
    }

    public static <T> T call(Callable<T> callable, Predicate<T> resultPredicate, long timeOut, HermesException hermesException) {
        try {
            return RetryerBuilder.<T>newBuilder()
                    .retryIfResult(resultPredicate::test)
                    .retryIfExceptionOfType(WebDriverException.class)
                    .retryIfExceptionOfType(AssertionError.class)
                    .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterDelay(timeOut, TimeUnit.SECONDS))
                    .build().call(callable);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (RetryException e) {
            Throwable exceptionCause;
            try {
                exceptionCause = e.getLastFailedAttempt().getExceptionCause();
            } catch (IllegalStateException ex) {
                throw hermesException;
            }
            if (exceptionCause instanceof Error) {
                throw (Error) exceptionCause;
            } else {
                throw (RuntimeException) exceptionCause;
            }
        }
    }

}
