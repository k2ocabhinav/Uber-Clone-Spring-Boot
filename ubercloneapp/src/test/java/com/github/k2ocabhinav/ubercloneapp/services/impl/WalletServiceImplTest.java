package com.github.k2ocabhinav.ubercloneapp.services.impl;

import com.github.k2ocabhinav.ubercloneapp.entities.Ride;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.Wallet;
import com.github.k2ocabhinav.ubercloneapp.entities.WalletTransaction;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionMethod;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.TransactionType;
import com.github.k2ocabhinav.ubercloneapp.exceptions.ResourceNotFoundException;
import com.github.k2ocabhinav.ubercloneapp.repositories.WalletRepository;
import com.github.k2ocabhinav.ubercloneapp.services.WalletTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionService walletTransactionService;

    @Mock
    private ModelMapper modelMapper;

    private WalletServiceImpl walletService;

    private User testUser;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        walletService = new WalletServiceImpl(walletRepository, walletTransactionService, modelMapper);
        
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
        
        testWallet = Wallet.builder()
                .id(1L)
                .user(testUser)
                .balance(100.0)
                .build();
    }

    @Test
    @DisplayName("Should add money to wallet correctly")
    void shouldAddMoneyToWallet() {
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        
        Wallet result = walletService.addMoneyToWallet(
                testUser, 50.0, "TXN123", null, TransactionMethod.BANKING);
        
        ArgumentCaptor<WalletTransaction> transactionCaptor = 
                ArgumentCaptor.forClass(WalletTransaction.class);
        verify(walletTransactionService).createNewWalletTransaction(transactionCaptor.capture());
        
        WalletTransaction capturedTransaction = transactionCaptor.getValue();
        assertThat(capturedTransaction.getTransactionType()).isEqualTo(TransactionType.CREDIT);
        assertThat(capturedTransaction.getAmount()).isEqualTo(50.0);
        assertThat(testWallet.getBalance()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("Should deduct money from wallet correctly")
    void shouldDeductMoneyFromWallet() {
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        
        Wallet result = walletService.deductMoneyFromWallet(
                testUser, 30.0, "TXN456", null, TransactionMethod.RIDE);
        
        assertThat(testWallet.getBalance()).isEqualTo(70.0);
    }

    @Test
    @DisplayName("Should find wallet by user")
    void shouldFindWalletByUser() {
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.of(testWallet));
        
        Wallet result = walletService.findByUser(testUser);
        
        assertThat(result).isEqualTo(testWallet);
        assertThat(result.getBalance()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should throw exception when wallet not found")
    void shouldThrowExceptionWhenWalletNotFound() {
        when(walletRepository.findByUser(testUser)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> walletService.findByUser(testUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Wallet not found");
    }

    @Test
    @DisplayName("Should create new wallet for user")
    void shouldCreateNewWallet() {
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet wallet = invocation.getArgument(0);
            wallet.setId(2L);
            return wallet;
        });
        
        Wallet result = walletService.createNewWallet(testUser);
        
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getBalance()).isEqualTo(0.0);
        verify(walletRepository).save(any(Wallet.class));
    }
}
