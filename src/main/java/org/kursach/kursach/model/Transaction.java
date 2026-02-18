package org.kursach.kursach.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "trade_transaction")
public class Transaction implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "security_id")
    private Security security;
    
    @ManyToOne
    @JoinColumn(name = "account_id")
    private InvestmentAccount account;
    
    @Column(name = "trade_date")
    private LocalDate tradeDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType type;
    
    @Column(name = "quantity", precision = 19, scale = 4)
    private BigDecimal quantity;
    
    @Column(name = "price", precision = 19, scale = 4)
    private BigDecimal price;
    
    @Column(name = "fees", precision = 19, scale = 4)
    private BigDecimal fees;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    public Transaction() {
    }
    
    public Transaction(Security security, InvestmentAccount account, TransactionType type) {
        this.security = security;
        this.account = account;
        this.type = type;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public InvestmentAccount getAccount() {
        return account;
    }

    public void setAccount(InvestmentAccount account) {
        this.account = account;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Transient
    public BigDecimal getGrossValue() {
        if (quantity == null || price == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(price);
    }
    
    @Transient
    public BigDecimal getNetValue() {
        BigDecimal gross = getGrossValue();
        if (fees == null) {
            return gross;
        }
        return TransactionType.BUY.equals(type) ? gross.add(fees) : gross.subtract(fees);
    }
    
    public String getFormattedTradeDate() {
        if (tradeDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return tradeDate.format(formatter);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Transaction that = (Transaction) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", security=" + (security != null ? security.getTicker() : null) +
                ", account=" + (account != null ? account.getAccountNumber() : null) +
                ", type=" + type +
                ", tradeDate=" + tradeDate +
                '}';
    }
} 