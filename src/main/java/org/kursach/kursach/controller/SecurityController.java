package org.kursach.kursach.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.Issuer;
import org.kursach.kursach.model.Security;
import org.kursach.kursach.model.SecurityType;
import org.kursach.kursach.service.IssuerService;
import org.kursach.kursach.service.SecurityService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class SecurityController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private SecurityService securityService;
    
    @Inject
    private IssuerService issuerService;
    
    private List<Security> securities;
    private List<Issuer> issuers;
    private Security security;
    private Long issuerId;
    private String searchTerm;
    
    @PostConstruct
    public void init() {
        securities = securityService.getAllSecurities();
        issuers = issuerService.getAllIssuers();
        security = new Security();
        loadFromParam();
    }
    
    private void loadFromParam() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");
        if (idParam != null && !idParam.isBlank()) {
            Long id = Long.valueOf(idParam);
            security = securityService.getSecurityById(id);
            if (security != null && security.getIssuer() != null) {
                issuerId = security.getIssuer().getId();
            }
        }
    }
    
    public String save() {
        if (issuerId != null) {
            security.setIssuer(issuerService.getIssuerById(issuerId));
        }
        securityService.saveSecurity(security);
        securities = securityService.getAllSecurities();
        security = new Security();
        issuerId = null;
        return "security?faces-redirect=true";
    }
    
    public String edit(Long id) {
        Security found = securityService.getSecurityById(id);
        if (found == null) {
            return "security?faces-redirect=true";
        }
        this.security = found;
        this.issuerId = found.getIssuer() != null ? found.getIssuer().getId() : null;
        return "security-edit?faces-redirect=true&id=" + id;
    }
    
    public String delete(Long id) {
        securityService.deleteSecurity(id);
        securities = securityService.getAllSecurities();
        return "security?faces-redirect=true";
    }
    
    public void search() {
        securities = securityService.searchSecurities(searchTerm);
    }
    
    public void resetSearch() {
        searchTerm = null;
        securities = securityService.getAllSecurities();
    }
    
    public String prepareNew() {
        security = new Security();
        issuerId = null;
        return "security-edit?faces-redirect=true";
    }
    
    public List<Security> getSecurities() {
        return securities;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Long getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Long issuerId) {
        this.issuerId = issuerId;
    }

    public List<Issuer> getIssuers() {
        return issuers;
    }

    public SecurityType[] getSecurityTypes() {
        return SecurityType.values();
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}