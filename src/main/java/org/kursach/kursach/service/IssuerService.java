package org.kursach.kursach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kursach.kursach.model.Issuer;
import org.kursach.kursach.repository.IssuerRepository;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class IssuerService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private IssuerRepository issuerRepository;
    
    public List<Issuer> getAllIssuers() {
        return issuerRepository.findAll();
    }
    
    public List<Issuer> searchIssuers(String query) {
        return issuerRepository.findByNameOrTicker(query);
    }
    
    public Issuer getIssuerById(Long id) {
        return issuerRepository.findById(id);
    }
    
    public void saveIssuer(Issuer issuer) {
        issuerRepository.save(issuer);
    }
    
    public void deleteIssuer(Long id) {
        issuerRepository.delete(id);
    }
} 