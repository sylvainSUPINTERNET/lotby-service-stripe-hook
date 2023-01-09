package com.lotby.webhookstripe.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.lotby.webhookstripe.bot.SalesBot;
import com.lotby.webhookstripe.repositories.NotificationRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class WebhookController {

  Logger logger = LoggerFactory.getLogger(WebhookController.class);


  @Value("${stripe.api.stripeEndpointSecretWebhook}")
  String ENDPOINT_SECRET;

  private NotificationRepository notificationRepository;

  private SalesBot salesBot;

  public WebhookController(NotificationRepository notificationRepository, SalesBot salesBot) {
    this.notificationRepository = notificationRepository;
    this.salesBot = salesBot;
  }
    

    // https://monsterdeveloper.gitbooks.io/writing-telegram-bots-on-java/content/chapter1.html
    // https://dashboard.stripe.com/test/webhooks/create?endpoint_location=local

    @PostMapping("/webhook-stripe")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event = null;
        try {
          event = Webhook.constructEvent(payload, sigHeader, this.ENDPOINT_SECRET);
        } catch (SignatureVerificationException e) {
          this.logger.info("Failed signature verification");
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
          stripeObject = dataObjectDeserializer.getObject().get();


        } else {
          logger.info("Failed to deserialize event data object");
          // Deserialization failed, probably due to an API version mismatch.
          // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
          // instructions on how to handle this case, or return an error here.
        }

        switch (event.getType()) {
          case "payment_intent.succeeded":
            // ...
            logger.info("payment_intent.succeeded triggered");

            Gson gson = new Gson();
            System.out.println(gson.toJson(event.toJson()));

            // yourObject o = gson.fromJson(JSONString,yourObject.class);
          
            // Notifiy all subscribers chat
              
              this.notificationRepository.findAll().forEach(notification -> {
                logger.info("Sending notification to " + notification.getChatId());
                // String url = "https://dashboard.stripe.com/payments/"+stripeObject.getId();

                String msgToNotify = "Payment Succeeded, don't forget to send the email";
                try {
                  this.salesBot.sendNotification(notification.getChatId(), msgToNotify);
                } catch ( TelegramApiException e ) {
                  logger.info("Error", e.getMessage());
                }
              });
            break;
          // case "payment_method.attached":


          //   break;
          //   // ... handle other event types
          default:
            // Unexpected event type
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }


        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

}
