package com.splitwise.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AmountShare {
    String userId;
    float  shareValue;
}
