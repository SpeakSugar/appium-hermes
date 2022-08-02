package com.ringcentral.hermes.util;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.ringcentral.hermes.exception.HermesException;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class APIHelper {

    public static <T> Response<T> callWithRetry(Call<T> call) {
        try {
            return RetryerBuilder.<Response<T>>newBuilder()
                    .retryIfExceptionOfType(Exception.class)
                    .withWaitStrategy(WaitStrategies.incrementingWait(5, TimeUnit.SECONDS, 5, TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                    .build().call(() -> call.clone().execute());
        } catch (ExecutionException e) {
            throw new HermesException("ExecutionException", e);
        } catch (RetryException e) {
            Throwable exceptionCause = e.getLastFailedAttempt().getExceptionCause();
            if (exceptionCause instanceof Error) {
                throw (Error) exceptionCause;
            } else {
                throw new HermesException("RetryException", exceptionCause);
            }
        }
    }
}
