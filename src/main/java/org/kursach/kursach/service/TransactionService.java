package org.kursach.kursach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kursach.kursach.model.InvestmentAccount;
import org.kursach.kursach.model.Security;
import org.kursach.kursach.model.Transaction;
import org.kursach.kursach.model.TransactionType;
import org.kursach.kursach.repository.TransactionRepository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class TransactionService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private TransactionRepository transactionRepository;
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    public List<Transaction> getTransactionsByAccount(InvestmentAccount account) {
        return transactionRepository.findByAccount(account);
    }
    
    public List<Transaction> getTransactionsBySecurity(Security security) {
        return transactionRepository.findBySecurity(security);
    }
    
    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByType(type);
    }
    
    public List<Transaction> getTransactionsByPeriod(LocalDate from, LocalDate to) {
        return transactionRepository.findByPeriod(from, to);
    }
    
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
    
    public void deleteTransaction(Long id) {
        transactionRepository.delete(id);
    }
} 