package com.github.sadcenter.core;

import java.util.concurrent.CompletableFuture;

public interface AsyncHttpAuthenticator extends Authenticator {

    <T> CompletableFuture<T> get(String url, Class<T> type);

    <T> CompletableFuture<T> post(String url, Object content, Class<T> type);

}
