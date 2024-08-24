package com.github.k2ocabhinav.ubercloneapp.entities;

import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(indexes = {
        @Index(name = "idx_wallet_transaction_wallet", columnList = "wallet_id"),
        @Index(name = "idx_wallet_transaction_ride", columnList = "ride_id")
})
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private TransactionType transactionType;

    private TransactionMethod transactionMethod;

    private String transactionId;

    @ManyToOne
    private Ride ride;

    @ManyToOne
    private Wallet wallet; // One wallet has many transaction

    @CreationTimestamp
    private LocalDateTime timestamp;

}
