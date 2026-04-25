package com.simbirsoft.utils;

import com.simbirsoft.dto.AdditionRequest;
import com.simbirsoft.dto.EntityRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestDataGenerator {

    private static final Random random = new Random();

    public static EntityRequest generateValidEntity() {
        return generateValidEntity(
                "Тестовая сущность " + random.nextInt(1000),
                "Доп. сведения " + random.nextInt(10000),
                random.nextInt(999999),
                Arrays.asList(
                        random.nextInt(100),
                        random.nextInt(100),
                        random.nextInt(100)
                ),
                true
        );
    }

    public static EntityRequest generateValidEntity(String title, String addInfo,
                                                    Integer addNumber, List<Integer> numbers,
                                                    Boolean verified) {
        return EntityRequest.builder()
                .addition(
                        AdditionRequest.builder()
                                .additionalInfo(addInfo)
                                .additionalNumber(addNumber)
                                .build()
                )
                .importantNumbers(numbers)
                .title(title)
                .verified(verified)
                .build();
    }
}
