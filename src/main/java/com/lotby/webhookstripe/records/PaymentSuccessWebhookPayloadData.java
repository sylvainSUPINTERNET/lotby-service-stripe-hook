package com.lotby.webhookstripe.records;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentSuccessWebhookPayloadData(
    @JsonProperty("object") PaymentSuccessWebhookPayloadDataObject object,
    @JsonProperty("receipt_email") String receiptEmail){}