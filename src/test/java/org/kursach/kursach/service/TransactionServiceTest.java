package org.kursach.kursach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.kursach.kursach.model.InvestmentAccount;
import org.kursach.kursach.model.Security;
import org.kursach.kursach.model.SecurityType;
import org.kursach.kursach.model.Transaction;
import org.kursach.kursach.model.TransactionType;
import org.kursach.kursach.repository.TransactionRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private InvestmentAccount account;
    private Security security;
    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        // Создаем тестовые данные
        account = new InvestmentAccount("ACC-1", "Иван Иванов", "RUB");
        account.setId(1L);

        security = new Security();
        security.setId(1L);
        security.setTicker("TST1");
        security.setName("Test Paper");
        security.setType(SecurityType.STOCK);

        transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setAccount(account);
        transaction1.setSecurity(security);
        transaction1.setType(TransactionType.BUY);
        transaction1.setTradeDate(java.time.LocalDate.now().minusDays(3));

        transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAccount(account);
        transaction2.setSecurity(security);
        transaction2.setType(TransactionType.SELL);
        transaction2.setTradeDate(java.time.LocalDate.now());
    }

    @Test
    void testGetAllTransactions() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionById() {
        when(transactionRepository.findById(1L)).thenReturn(transaction1);

        Transaction result = transactionService.getTransactionById(1L);

        assertNotNull(result);
        assertEquals(TransactionType.BUY, result.getType());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTransactionsByAccount() {
        when(transactionRepository.findByAccount(account)).thenReturn(Arrays.asList(transaction1, transaction2));

        List<Transaction> result = transactionService.getTransactionsByAccount(account);

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findByAccount(account);
    }

    @Test
    void testGetTransactionsBySecurity() {
        when(transactionRepository.findBySecurity(security)).thenReturn(Arrays.asList(transaction1));

        List<Transaction> result = transactionService.getTransactionsBySecurity(security);

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findBySecurity(security);
    }

    @Test
    void testGetTransactionsByType() {
        when(transactionRepository.findByType(TransactionType.BUY)).thenReturn(Arrays.asList(transaction1));

        List<Transaction> result = transactionService.getTransactionsByType(TransactionType.BUY);

        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByType(TransactionType.BUY);
    }

    @Test
    void testGetTransactionsByPeriod() {
        java.time.LocalDate from = java.time.LocalDate.now().minusDays(5);
        java.time.LocalDate to = java.time.LocalDate.now();
        when(transactionRepository.findByPeriod(from, to)).thenReturn(Arrays.asList(transaction1, transaction2));

        List<Transaction> result = transactionService.getTransactionsByPeriod(from, to);

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findByPeriod(from, to);
    }

    @Test
    void testSaveTransaction() {
        transactionService.saveTransaction(transaction1);
        verify(transactionRepository, times(1)).save(transaction1);
    }

    @Test
    void testDeleteTransaction() {
        transactionService.deleteTransaction(1L);
        verify(transactionRepository, times(1)).delete(1L);
    }
} 