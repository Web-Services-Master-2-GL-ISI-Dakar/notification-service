package sn.ondmoney.notification.service;

public interface SmsService {
    void sendMessage(String to, String text);
}
