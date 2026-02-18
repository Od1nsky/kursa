package org.kursach.kursach.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "investment_account")
public class InvestmentAccount implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;
    
    @Column(name = "owner_name", nullable = false)
    private String ownerName;
    
    @Column(name = "strategy")
    private String strategy;
    
    @Column(name = "base_currency")
    private String baseCurrency;
    
    @Column(name = "opened_date")
    private LocalDate openedDate;
    
    @Column(name = "cash_balance", precision = 19, scale = 4)
    private BigDecimal cashBalance;
    
    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;
    
    public InvestmentAccount() {
    }
    
    public InvestmentAccount(String accountNumber, String ownerName, String baseCurrency) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.baseCurrency = baseCurrency;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public LocalDate getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(LocalDate openedDate) {
        this.openedDate = openedDate;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getFormattedOpenedDate() {
        if (openedDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return openedDate.format(formatter);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        InvestmentAccount that = (InvestmentAccount) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "InvestmentAccount{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", baseCurrency='" + baseCurrency + '\'' +
                '}';
    }
} 