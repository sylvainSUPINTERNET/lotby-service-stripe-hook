package com.lotby.webhookstripe.controllers;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
public class WebhookController {


    @Value("${stripe.api.stripeEndpointSecretWebhook}")
    String ENDPOINT_SECRET;
    

    // https://monsterdeveloper.gitbooks.io/writing-telegram-bots-on-java/content/chapter1.html
    // https://dashboard.stripe.com/test/webhooks/create?endpoint_location=local

    @PostMapping("/webhook-stripe")
    public ResponseEntity<Map<String, String>> postMethodName(HttpServletRequest request, @RequestHeader(value="Stripe-Signature") String stripeSignature ) {

        System.out.println(stripeSignature);
        
        if ( this.ENDPOINT_SECRET.isEmpty() ) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", "ENDPOINT_SECRET is empty"));
        }

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String payloadJson = gson.fromJson(request.getReader().lines().reduce("",String::concat), String.class);
            
            Event ev = Webhook.constructEvent(payloadJson, stripeSignature, this.ENDPOINT_SECRET);
            EventDataObjectDeserializer dataObjectDeserializer = ev.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
                return ResponseEntity.ok(Map.of("message", stripeObject.toJson()));
            }

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", "Fail to deserialize"));

        } catch (SignatureVerificationException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", e.getMessage()));
        }

    }

}
