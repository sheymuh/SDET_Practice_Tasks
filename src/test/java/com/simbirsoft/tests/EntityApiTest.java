package com.simbirsoft.tests;

import com.simbirsoft.api.EntityApiClient;
import com.simbirsoft.dto.EntityListResponse;
import com.simbirsoft.dto.EntityRequest;
import com.simbirsoft.dto.EntityResponse;
import com.simbirsoft.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("API Тестирование")
@Feature("Сущности")
public class EntityApiTest {

    private EntityApiClient apiClient;
    private final List<Integer> createdIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        apiClient = new EntityApiClient();
        createdIds.clear();
    }

    @AfterEach
    void tearDown() {
        for (Integer id : createdIds) {
            apiClient.deleteEntity(id);
        }
    }

    @Test
    @DisplayName("TC-01: Создание сущности (POST /api/create)")
    @Description("Проверка успешного создания сущности и возврата её ID")
    @Severity(SeverityLevel.CRITICAL)
    void createEntityTest() {
        EntityRequest testEntity = TestDataGenerator.generateValidEntity();
        Integer createdEntityId = apiClient.createEntity(testEntity);
        createdIds.add(createdEntityId);

        assertNotNull(createdEntityId, "ID созданной сущности не должен быть null");
        assertTrue(createdEntityId > 0, "ID должен быть положительным числом: " + createdEntityId);
    }

    @Test
    @DisplayName("TC-02: Получение сущности по ID (GET /api/get/{id})")
    @Description("Проверка получения созданной сущности по её ID")
    @Severity(SeverityLevel.CRITICAL)
    void getEntityByIdTest() {
        EntityRequest testEntity = TestDataGenerator.generateValidEntity();
        Integer createdEntityId = apiClient.createEntity(testEntity);
        createdIds.add(createdEntityId);

        EntityResponse response = apiClient.getEntity(createdEntityId);

        assertAll("Проверка EntityResponse",
                () -> assertEquals(createdEntityId, response.getId(), "ID не совпадает"),
                () -> assertEquals(testEntity.getTitle(), response.getTitle(), "Title не совпадает"),
                () -> assertEquals(testEntity.getVerified(), response.getVerified(), "Verified не совпадает"),
                () -> assertNotNull(response.getAddition(), "Addition не должен быть null"),
                () -> assertEquals(
                        testEntity.getAddition().getAdditionalInfo(),
                        response.getAddition().getAdditionalInfo(),
                        "AdditionalInfo не совпадает"
                ),
                () -> assertEquals(
                        testEntity.getAddition().getAdditionalNumber(),
                        response.getAddition().getAdditionalNumber(),
                        "AdditionalNumber не совпадает"
                ),
                () -> assertEquals(
                        testEntity.getImportantNumbers(),
                        response.getImportantNumbers(),
                        "ImportantNumbers не совпадают"
                )
        );
    }

    @Test
    @DisplayName("TC-03: Получение всех сущностей (GET /api/getAll)")
    @Description("Проверка, что метод getAll возвращает список сущностей")
    @Severity(SeverityLevel.CRITICAL)
    void getAllEntitiesTest() {
        EntityRequest testEntity = TestDataGenerator.generateValidEntity();
        Integer createdEntityId = apiClient.createEntity(testEntity);
        createdIds.add(createdEntityId);

        EntityListResponse response = apiClient.getAllEntities();

        assertNotNull(response.getEntity(), "Список entity не должен быть null");
        assertFalse(response.getEntity().isEmpty(), "Список сущностей не должен быть пустым");

        boolean found = response.getEntity().stream()
                .anyMatch(e -> e.getId().equals(createdEntityId));

        assertTrue(found, "Созданная сущность с ID=" + createdEntityId + " должна быть в списке");
    }

    @Test
    @DisplayName("TC-04: Обновление сущности (PATCH /api/patch/{id})")
    @Description("Проверка частичного обновления сущности")
    @Severity(SeverityLevel.CRITICAL)
    void updateEntityTest() {
        EntityRequest testEntity = TestDataGenerator.generateValidEntity();
        Integer createdEntityId = apiClient.createEntity(testEntity);
        createdIds.add(createdEntityId);

        String newTitle = "Обновлённый заголовок " + System.currentTimeMillis();
        EntityRequest updateRequest = TestDataGenerator.generateValidEntity(
                newTitle,
                "Обновлённые сведения",
                77777,
                Arrays.asList(1, 2, 3),
                false
        );

        assertDoesNotThrow(
                () -> apiClient.updateEntity(createdEntityId, updateRequest),
                "Обновление должно выполниться без ошибок"
        );

        EntityResponse updatedResponse = apiClient.getEntity(createdEntityId);

        assertAll("Проверка обновлённой сущности",
                () -> assertEquals(createdEntityId, updatedResponse.getId(), "ID не должен измениться"),
                () -> assertEquals(newTitle, updatedResponse.getTitle(), "Title не обновился"),
                () -> assertFalse(updatedResponse.getVerified(), "Verified должен быть false"),
                () -> assertEquals("Обновлённые сведения",
                        updatedResponse.getAddition().getAdditionalInfo(),
                        "AdditionalInfo не обновился"),
                () -> assertEquals(Integer.valueOf(77777),
                        updatedResponse.getAddition().getAdditionalNumber(),
                        "AdditionalNumber не обновился"),
                () -> assertEquals(Arrays.asList(1, 2, 3),
                        updatedResponse.getImportantNumbers(),
                        "ImportantNumbers не обновились")
        );
    }

    @Test
    @DisplayName("TC-05: Удаление сущности (DELETE /api/delete/{id})")
    @Description("Проверка успешного удаления сущности")
    @Severity(SeverityLevel.CRITICAL)
    void deleteEntityTest() {
        EntityRequest testEntity = TestDataGenerator.generateValidEntity();
        Integer idToDelete = apiClient.createEntity(testEntity);

        assertDoesNotThrow(
                () -> apiClient.deleteEntity(idToDelete),
                "Удаление должно выполниться без ошибок"
        );
    }
}
