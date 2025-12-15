package sn.ondmoney.notification.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Conteneur de données brutes pour le templating.
 * Doit contenir tous les champs potentiellement nécessaires pour les templates TXE et AUTH.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPayload implements Serializable {

    // --- Champs Financiers (TXE) ---
    private String transactionId;
    private String transactionStatus; // SUCCESS, FAILED, PENDING
    private String transactionType; // TRANSFER, MERCHANT_PAYMENT, BILL_PAYMENT, AIRTIME, BANK_TO_WALLET, WALLET_TO_BANK etc.
    private BigDecimal amount;
    private BigDecimal fees;
    private String currency; // XOF

    // --- Champs Financiers (BANK2WALLET & WALLET2BANK) ---
    private String bankName;
    private String bankAccountNumber;
    private String bankTransactionCorrelation;
    /// The Ond Account balance after the transaction
    private BigDecimal clientBalance;

    /// For `Auth` like OTP code or Email Verification Link Requests, he's the `user`
    /// For `Merchant & Bill Payments`, he's the `payer`.
    /// For `Transfer`, he's the `sender`
    /// For `AIRTIME`, he's the `buyer`
    /// For `Bank2Wallet` or `Wallet2Bank` he's the `bank client`
    private String senderPhone;
    private String senderName;
    /// The `sender balance` **after** the `transaction`
    private BigDecimal senderBalance;

    /// For `Transfer`, he's the `receiver`
    /// For `AIRTIME`, he's the `beneficiary`, the `beneficiary` could be also the `buyer` or anyone else.
    /// For `Merchant & Bill Payments`, he's the `merchant`(Boutiques, Supermarchés) or the `biller`(SENEAU, SENELEC, etc.).
    private String receiverPhone;
    private String receiverName;
    /// The `receiver balance` **after** the `transaction`
    private BigDecimal receiverBalance;

    private Instant transactionDate; // 2026-12-31T14:50:25.123Z

    /// Represents the Merchant & or Bill Payment business reference
    private String commandRef;

    /// Particularly for Auth, it could be an OTP code or an Email Verification link
    private String verificationCode;
    /// The expires time for the verification code
    private Integer verificationCodeExpiryInMinutes;
    /// Useful for tracking the device's IP address that tries to log in and prevent the user of that activity
    private String ipAddress;
    /// Devices are registered in a database linked to their owner to keep track sessions on all of their devices
    private String deviceId;

    /// Any additional data that could be useful for templating but not covered by the main fields
    private Map<String, String> additionalData;
}
