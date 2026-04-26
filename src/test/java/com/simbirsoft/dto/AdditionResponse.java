package com.simbirsoft.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class AdditionResponse {

    @SerializedName("id")
    Integer id;

    @SerializedName("additional_info")
    String additionalInfo;

    @SerializedName("additional_number")
    Integer additionalNumber;
}
