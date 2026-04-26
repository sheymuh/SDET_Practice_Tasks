package com.simbirsoft.api;

import com.google.gson.Gson;
import com.simbirsoft.dto.EntityListResponse;
import com.simbirsoft.dto.EntityRequest;
import com.simbirsoft.dto.EntityResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class EntityApiClient extends BaseApiClient {

    private final Gson gson = new Gson();

    @Step("Создание сущности")
    public Integer createEntity(EntityRequest request) {
        acquireLock();
        try {
            Response response = given()
                    .spec(spec)
                    .body(gson.toJson(request))
                    .post("/api/create")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            return Integer.parseInt(response.getBody().asString().trim());
        } finally {
            releaseLock();
        }
    }

    @Step("Получение сущности по ID = {id}")
    public EntityResponse getEntity(Integer id) {
        acquireLock();
        try {
            Response response = given()
                    .spec(spec)
                    .get("/api/get/{id}", id)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            return gson.fromJson(response.getBody().asString(), EntityResponse.class);
        } finally {
            releaseLock();
        }
    }

    @Step("Получение всех сущностей")
    public EntityListResponse getAllEntities() {
        acquireLock();
        try {
            Response response = given()
                    .spec(spec)
                    .get("/api/getAll")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            return gson.fromJson(response.getBody().asString(), EntityListResponse.class);
        } finally {
            releaseLock();
        }
    }

    @Step("Обновление сущности с ID = {id}")
    public void updateEntity(Integer id, EntityRequest request) {
        acquireLock();
        try {
            given()
                    .spec(spec)
                    .body(gson.toJson(request))
                    .patch("/api/patch/{id}", id)
                    .then()
                    .statusCode(204);
        } finally {
            releaseLock();
        }
    }

    @Step("Удаление сущности с ID = {id}")
    public void deleteEntity(Integer id) {
        acquireLock();
        try {
            given()
                    .spec(spec)
                    .delete("/api/delete/{id}", id)
                    .then()
                    .statusCode(204);
        } finally {
            releaseLock();
        }
    }
}
