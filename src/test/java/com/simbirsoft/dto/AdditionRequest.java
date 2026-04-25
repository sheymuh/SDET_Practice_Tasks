package com.simbirsoft.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AdditionRequest {

    @SerializedName("additional_info")
    String additionalInfo;

    @SerializedName("additional_number")
    Integer additionalNumber;
}
