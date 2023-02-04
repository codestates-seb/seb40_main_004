package com.morakmorak.morak_back_end.service;

public interface UserTransactionContext<Request, Response> {
    Response processWithAddPoint(Request request);
    Response processWithMinusPoint(Request request);
    Response processWithoutPoint(Request request);
}
