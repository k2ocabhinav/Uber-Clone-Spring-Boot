package com.github.k2ocabhinav.ubercloneapp.testdata;

import com.github.k2ocabhinav.ubercloneapp.entities.Wallet;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import java.math.BigDecimal;

public class WalletTestBuilder {
    private Long id = 1L;
    private User user;
    private BigDecimal balance = BigDecimal.ZERO;

    public WalletTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public WalletTestBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public WalletTestBuilder withBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public WalletTestBuilder withBalance(Double balance) {
        this.balance = BigDecimal.valueOf(balance);
        return this;
    }

    public Wallet build() {
        return Wallet.builder()
                .id(id)
                .user(user)
                .balance(balance)
                .build();
    }

    public static WalletTestBuilder aWallet() {
        return new WalletTestBuilder();
    }

    public static WalletTestBuilder aWalletWithBalance(BigDecimal balance) {
        return aWallet().withBalance(balance);
    }

    public static WalletTestBuilder aWalletWithBalance(Double balance) {
        return aWallet().withBalance(balance);
    }
}
