package org.kursach.kursach.repository;

import org.kursach.kursach.model.InvestmentAccount;
import org.kursach.kursach.model.Security;
import org.kursach.kursach.model.Transaction;
import org.kursach.kursach.model.TransactionType;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends Repository<Transaction, Long> {
    
    List<Transaction> findByAccount(InvestmentAccount account);
    
    List<Transaction> findBySecurity(Security security);
    
    List<Transaction> findByPeriod(LocalDate from, LocalDate to);
    
    List<Transaction> findByType(TransactionType type);
}