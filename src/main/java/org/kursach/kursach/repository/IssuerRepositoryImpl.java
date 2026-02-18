package org.kursach.kursach.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.kursach.kursach.model.Issuer;

import java.util.List;

@ApplicationScoped
public class IssuerRepositoryImpl implements IssuerRepository {
    public IssuerRepositoryImpl() {}
    
    @Inject
    private EntityManager em;
    
    @Override
    public Issuer findById(Long id) {
        return em.find(Issuer.class, id);
    }
    
    @Override
    public List<Issuer> findAll() {
        TypedQuery<Issuer> query = em.createQuery("SELECT i FROM Issuer i ORDER BY i.name", Issuer.class);
        return query.getResultList();
    }
    
    @Override
    public List<Issuer> findByNameOrTicker(String queryText) {
        if (queryText == null || queryText.trim().isEmpty()) {
            return findAll();
        }
        String pattern = "%" + queryText.toLowerCase() + "%";
        TypedQuery<Issuer> query = em.createQuery(
                "SELECT i FROM Issuer i WHERE LOWER(i.name) LIKE :text OR LOWER(i.sector) LIKE :text OR LOWER(i.rating) LIKE :text",
                Issuer.class);
        query.setParameter("text", pattern);
        return query.getResultList();
    }
    
    @Override
    public void save(Issuer issuer) {
        boolean transactionActive = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            if (issuer.getId() == null) {
                em.persist(issuer);
            } else {
                issuer = em.merge(issuer);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при сохранении эмитента", e);
        }
    }
    
    @Override
    public void delete(Long id) {
        boolean transactionActive = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            Issuer issuer = findById(id);
            if (issuer != null) {
                em.remove(issuer);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при удалении эмитента", e);
        }
    }
} 