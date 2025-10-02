package com.medianet.video.rtb.models;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Transaction {
    String name = UUID.randomUUID().toString();
    String paidBy;
    String paidTo;
    Float amount;
}
