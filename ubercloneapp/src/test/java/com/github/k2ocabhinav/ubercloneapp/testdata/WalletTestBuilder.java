package com.github.k2ocabhinav.ubercloneapp.testdata;

import com.github.k2ocabhinav.ubercloneapp.entities.Wallet;
import com.github.k2ocabhinav.ubercloneapp.entities.User;

public class WalletTestBuilder {
    private Long id = 1L;
    private User user;
    private Double balance = 0.0;

    public WalletTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public WalletTestBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public WalletTestBuilder withBalance(Double balance) {
        this.balance = balance;
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

    public static WalletTestBuilder aWalletWithBalance(Double balance) {
        return aWallet().withBalance(balance);
    }
}
