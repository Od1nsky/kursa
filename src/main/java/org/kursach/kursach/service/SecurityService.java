package org.kursach.kursach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kursach.kursach.model.Issuer;
import org.kursach.kursach.model.Security;
import org.kursach.kursach.repository.SecurityRepository;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class SecurityService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private SecurityRepository securityRepository;
    
    public List<Security> getAllSecurities() {
        return securityRepository.findAll();
    }
    
    public List<Security> searchSecurities(String query) {
        return securityRepository.findByTickerOrName(query);
    }
    
    public Security getSecurityById(Long id) {
        return securityRepository.findById(id);
    }
    
    public List<Security> getSecuritiesByIssuer(Issuer issuer) {
        return securityRepository.findByIssuer(issuer);
    }
    
    public void saveSecurity(Security security) {
        securityRepository.save(security);
    }
    
    public void deleteSecurity(Long id) {
        securityRepository.delete(id);
    }
} 