package org.kursach.kursach.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.InvestmentAccount;
import org.kursach.kursach.model.Security;
import org.kursach.kursach.model.Transaction;
import org.kursach.kursach.model.TransactionType;
import org.kursach.kursach.service.InvestmentAccountService;
import org.kursach.kursach.service.SecurityService;
import org.kursach.kursach.service.TransactionService;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class TransactionController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private TransactionService transactionService;
    
    @Inject
    private SecurityService securityService;
    
    @Inject
    private InvestmentAccountService accountService;
    
    private List<Transaction> transactions;
    private List<Security> securities;
    private List<InvestmentAccount> accounts;
    private Transaction transaction;
    private Long securityId;
    private Long accountId;
    private Long filterSecurityId;
    private Long filterAccountId;
    private TransactionType typeFilter;
    private LocalDate fromDate;
    private LocalDate toDate;
    
    @PostConstruct
    public void init() {
        refreshReferenceData();
        transaction = new Transaction();
        loadFromParam();
    }
    
    private void refreshReferenceData() {
        transactions = transactionService.getAllTransactions();
        securities = securityService.getAllSecurities();
        accounts = accountService.getAllAccounts();
    }
    
    private void loadFromParam() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");
        if (idParam != null && !idParam.isBlank()) {
            Long id = Long.valueOf(idParam);
            Transaction found = transactionService.getTransactionById(id);
            if (found != null) {
                transaction = found;
                securityId = found.getSecurity() != null ? found.getSecurity().getId() : null;
                accountId = found.getAccount() != null ? found.getAccount().getId() : null;
            }
        }
    }
    
    public String save() {
        if (securityId != null) {
            transaction.setSecurity(securityService.getSecurityById(securityId));
        }
        if (accountId != null) {
            transaction.setAccount(accountService.getAccountById(accountId));
        }
        transactionService.saveTransaction(transaction);
        refreshReferenceData();
        transaction = new Transaction();
        securityId = null;
        accountId = null;
        return "transaction?faces-redirect=true";
    }
    
    public String edit(Long id) {
        Transaction found = transactionService.getTransactionById(id);
        if (found == null) {
            return "transaction?faces-redirect=true";
        }
        this.transaction = found;
        this.securityId = found.getSecurity() != null ? found.getSecurity().getId() : null;
        this.accountId = found.getAccount() != null ? found.getAccount().getId() : null;
        return "transaction-edit?faces-redirect=true&id=" + id;
    }
    
    public String delete(Long id) {
        transactionService.deleteTransaction(id);
        refreshReferenceData();
        return "transaction?faces-redirect=true";
    }
    
    public void applyFilters() {
        if (fromDate != null && toDate != null) {
            transactions = transactionService.getTransactionsByPeriod(fromDate, toDate);
        } else if (typeFilter != null) {
            transactions = transactionService.getTransactionsByType(typeFilter);
        } else if (filterAccountId != null) {
            InvestmentAccount account = accountService.getAccountById(filterAccountId);
            transactions = transactionService.getTransactionsByAccount(account);
        } else if (filterSecurityId != null) {
            Security security = securityService.getSecurityById(filterSecurityId);
            transactions = transactionService.getTransactionsBySecurity(security);
        } else {
            transactions = transactionService.getAllTransactions();
        }
    }
    
    public void resetFilters() {
        typeFilter = null;
        fromDate = null;
        toDate = null;
        filterSecurityId = null;
        filterAccountId = null;
        refreshReferenceData();
    }
    
    public String prepareNew() {
        transaction = new Transaction();
        securityId = null;
        accountId = null;
        return "transaction-edit?faces-redirect=true";
    }
    
    public TransactionType[] getTransactionTypes() {
        return TransactionType.values();
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public List<InvestmentAccount> getAccounts() {
        return accounts;
    }

    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public TransactionType getTypeFilter() {
        return typeFilter;
    }

    public void setTypeFilter(TransactionType typeFilter) {
        this.typeFilter = typeFilter;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Long getFilterSecurityId() {
        return filterSecurityId;
    }

    public void setFilterSecurityId(Long filterSecurityId) {
        this.filterSecurityId = filterSecurityId;
    }

    public Long getFilterAccountId() {
        return filterAccountId;
    }

    public void setFilterAccountId(Long filterAccountId) {
        this.filterAccountId = filterAccountId;
    }
}