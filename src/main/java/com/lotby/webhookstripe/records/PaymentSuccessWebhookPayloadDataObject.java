package com.lotby.webhookstripe.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentSuccessWebhookPayloadDataObject(
    @JsonProperty("id") String id){}