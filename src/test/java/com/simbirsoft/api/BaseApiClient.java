package com.simbirsoft.api;

import com.simbirsoft.config.Configuration;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;

import java.util.concurrent.locks.ReentrantLock;

public class BaseApiClient {

    protected static final Configuration config = ConfigFactory.create(Configuration.class, System.getenv());

    // Глобальный лок для последовательного выполнения HTTP-запросов (без него падает сервер)
    private static final ReentrantLock requestLock = new ReentrantLock();

    protected static final RequestSpecification spec = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setBaseUri(config.baseUrl())
            .setBasePath(config.basePath())
            .addFilter(new AllureRestAssured())
            .log(LogDetail.ALL)
            .build();

    protected static void acquireLock() {
        requestLock.lock();
    }

    protected static void releaseLock() {
        requestLock.unlock();
    }
}
