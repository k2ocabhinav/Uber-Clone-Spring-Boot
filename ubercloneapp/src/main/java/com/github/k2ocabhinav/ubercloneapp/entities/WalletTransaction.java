package com.github.k2ocabhinav.ubercloneapp.entities;

import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private TransactionType transactionType;

    private TransactionMethod transactionMethod;

    private String TransactionId;

    @OneToOne
    private Ride ride;

    @ManyToOne
    private Wallet wallet; // One wallet has many transaction

    @CreationTimestamp
    private LocalDateTime timestamp;

}
