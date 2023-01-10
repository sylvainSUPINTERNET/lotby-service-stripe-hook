package com.lotby.webhookstripe.records;

public class NotificationInfo {

    private String msgForTelegramChat;

    private String paymentId;
    
    public NotificationInfo(String msgForTelegramChat) {
        this.msgForTelegramChat = msgForTelegramChat;
    }


    public String getPaymentId() {
        return this.paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }


    public String getMsgForTelegramChat() {
        return this.msgForTelegramChat;
    }

    public void setMsgForTelegramChat(String msgForTelegramChat) {
        this.msgForTelegramChat = msgForTelegramChat;
    }

}