package com.simbirsoft.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.List;

@Value
public class EntityResponse {

    @SerializedName("id")
    Integer id;

    @SerializedName("addition")
    AdditionResponse addition;

    @SerializedName("important_numbers")
    List<Integer> importantNumbers;

    @SerializedName("title")
    String title;

    @SerializedName("verified")
    Boolean verified;
}
