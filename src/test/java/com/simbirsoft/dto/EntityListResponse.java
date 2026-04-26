package com.simbirsoft.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.List;

@Value
public class EntityListResponse {

    @SerializedName("entity")
    List<EntityResponse> entity;
}
