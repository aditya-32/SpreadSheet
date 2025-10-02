package com.splitwise.models;

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
