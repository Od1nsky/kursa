package org.kursach.kursach.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.InvestmentAccount;
import org.kursach.kursach.service.InvestmentAccountService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class InvestmentAccountController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private InvestmentAccountService accountService;
    
    private List<InvestmentAccount> accounts;
    private InvestmentAccount account;
    private String ownerFilter;
    private String strategyFilter;
    
    @PostConstruct
    public void init() {
        accounts = accountService.getAllAccounts();
        account = new InvestmentAccount();
        loadFromParam();
    }
    
    private void loadFromParam() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");
        if (idParam != null && !idParam.isBlank()) {
            Long id = Long.valueOf(idParam);
            InvestmentAccount found = accountService.getAccountById(id);
            if (found != null) {
                account = found;
            }
        }
    }
    
    public String save() {
        accountService.saveAccount(account);
        accounts = accountService.getAllAccounts();
        account = new InvestmentAccount();
        return "account?faces-redirect=true";
    }
    
    public String edit(Long id) {
        InvestmentAccount found = accountService.getAccountById(id);
        if (found == null) {
            return "account?faces-redirect=true";
        }
        this.account = found;
        return "account-edit?faces-redirect=true&id=" + id;
    }
    
    public String delete(Long id) {
        accountService.deleteAccount(id);
        accounts = accountService.getAllAccounts();
        return "account?faces-redirect=true";
    }
    
    public void filter() {
        if (ownerFilter != null && !ownerFilter.isBlank()) {
            accounts = accountService.searchAccountsByOwner(ownerFilter);
        } else if (strategyFilter != null && !strategyFilter.isBlank()) {
            accounts = accountService.getAccountsByStrategy(strategyFilter);
        } else {
            accounts = accountService.getAllAccounts();
        }
    }
    
    public void resetFilters() {
        ownerFilter = null;
        strategyFilter = null;
        accounts = accountService.getAllAccounts();
    }
    
    public String prepareNew() {
        account = new InvestmentAccount();
        return "account-edit?faces-redirect=true";
    }
    
    public List<InvestmentAccount> getAccounts() {
        return accounts;
    }

    public InvestmentAccount getAccount() {
        return account;
    }

    public void setAccount(InvestmentAccount account) {
        this.account = account;
    }

    public String getOwnerFilter() {
        return ownerFilter;
    }

    public void setOwnerFilter(String ownerFilter) {
        this.ownerFilter = ownerFilter;
    }

    public String getStrategyFilter() {
        return strategyFilter;
    }

    public void setStrategyFilter(String strategyFilter) {
        this.strategyFilter = strategyFilter;
    }
}