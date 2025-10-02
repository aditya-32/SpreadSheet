package com.medianet.video.rtb.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode
public class User {
    String id;
    String email;
    String conNo;
}
