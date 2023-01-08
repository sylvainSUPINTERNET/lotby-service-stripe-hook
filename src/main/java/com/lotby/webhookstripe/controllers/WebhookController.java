package com.lotby.webhookstripe.controllers;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
public class WebhookController {


    @Value("${stripe.api.stripeEndpointSecretWebhook}")
    String ENDPOINT_SECRET;

    

    // https://monsterdeveloper.gitbooks.io/writing-telegram-bots-on-java/content/chapter1.html
    // https://dashboard.stripe.com/test/webhooks/create?endpoint_location=local

    @PostMapping("/webhook-stripe")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event = null;
        try {
          event = Webhook.constructEvent(payload, sigHeader, this.ENDPOINT_SECRET);
        } catch (SignatureVerificationException e) {
          System.out.println("Failed signature verification");
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // System.out.println(event.toJson());


        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
          stripeObject = dataObjectDeserializer.getObject().get();


        } else {
          // Deserialization failed, probably due to an API version mismatch.
          // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
          // instructions on how to handle this case, or return an error here.
        }

        switch (event.getType()) {
          case "payment_intent.succeeded":
            // ...
            System.out.println("payment_intent.succeeded");
            
            break;
          case "payment_method.attached":
            // ...
            System.out.println("payment_method.attached");

            break;
            // ... handle other event types
          default:
            // Unexpected event type
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }


        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

}
