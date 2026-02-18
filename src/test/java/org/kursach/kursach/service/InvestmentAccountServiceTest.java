package org.kursach.kursach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.kursach.kursach.model.InvestmentAccount;
import org.kursach.kursach.repository.InvestmentAccountRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvestmentAccountServiceTest {

    @Mock
    private InvestmentAccountRepository accountRepository;

    @InjectMocks
    private InvestmentAccountService accountService;

    private InvestmentAccount account1;
    private InvestmentAccount account2;

    @BeforeEach
    void setUp() {
        account1 = new InvestmentAccount("BRK-01", "Иван Иванов", "RUB");
        account1.setId(1L);
        account1.setStrategy("Долгосрочные акции");
        
        account2 = new InvestmentAccount("BRK-02", "Петр Петров", "USD");
        account2.setId(2L);
        account2.setStrategy("Облигации");
    }

    @Test
    void testGetAllAccounts() {
        List<InvestmentAccount> accounts = Arrays.asList(account1, account2);
        when(accountRepository.findAll()).thenReturn(accounts);

        List<InvestmentAccount> result = accountService.getAllAccounts();

        assertEquals(2, result.size());
        assertEquals("BRK-01", result.get(0).getAccountNumber());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testGetAccountById() {
        when(accountRepository.findById(1L)).thenReturn(account1);

        InvestmentAccount result = accountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals("BRK-01", result.getAccountNumber());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testSearchAccountsByOwner() {
        when(accountRepository.findByOwner("Иван")).thenReturn(Arrays.asList(account1));

        List<InvestmentAccount> result = accountService.searchAccountsByOwner("Иван");

        assertEquals(1, result.size());
        assertEquals("Иван Иванов", result.get(0).getOwnerName());
        verify(accountRepository, times(1)).findByOwner("Иван");
    }

    @Test
    void testGetAccountsByStrategy() {
        when(accountRepository.findByStrategy("Облигации")).thenReturn(Arrays.asList(account2));

        List<InvestmentAccount> result = accountService.getAccountsByStrategy("Облигации");

        assertEquals(1, result.size());
        assertEquals("BRK-02", result.get(0).getAccountNumber());
        verify(accountRepository, times(1)).findByStrategy("Облигации");
    }

    @Test
    void testSaveAccount() {
        accountService.saveAccount(account1);
        verify(accountRepository, times(1)).save(account1);
    }

    @Test
    void testDeleteAccount() {
        accountService.deleteAccount(1L);
        verify(accountRepository, times(1)).delete(1L);
    }
} 