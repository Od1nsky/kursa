package org.kursach.kursach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kursach.kursach.model.InvestmentAccount;
import org.kursach.kursach.repository.InvestmentAccountRepository;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class InvestmentAccountService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private InvestmentAccountRepository investmentAccountRepository;
    
    public List<InvestmentAccount> getAllAccounts() {
        return investmentAccountRepository.findAll();
    }
    
    public List<InvestmentAccount> searchAccountsByOwner(String owner) {
        return investmentAccountRepository.findByOwner(owner);
    }
    
    public InvestmentAccount getAccountById(Long id) {
        return investmentAccountRepository.findById(id);
    }
    
    public List<InvestmentAccount> getAccountsByStrategy(String strategy) {
        return investmentAccountRepository.findByStrategy(strategy);
    }
    
    public void saveAccount(InvestmentAccount account) {
        investmentAccountRepository.save(account);
    }
    
    public void deleteAccount(Long id) {
        investmentAccountRepository.delete(id);
    }
} 