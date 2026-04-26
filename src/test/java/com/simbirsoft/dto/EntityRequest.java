package com.simbirsoft.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class EntityRequest {

    @SerializedName("addition")
    AdditionRequest addition;

    @SerializedName("important_numbers")
    List<Integer> importantNumbers;

    @SerializedName("title")
    String title;

    @SerializedName("verified")
    Boolean verified;
}
