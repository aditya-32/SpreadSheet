package com.medianet.video.rtb.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AmountShare {
    String userId;
    float  shareValue;
}
