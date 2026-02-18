package org.kursach.kursach.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.kursach.kursach.model.Issuer;
import org.kursach.kursach.model.Security;

import java.util.List;

@ApplicationScoped
public class SecurityRepositoryImpl implements SecurityRepository {
    public SecurityRepositoryImpl() {}
    
    @Inject
    private EntityManager em;
    
    @Override
    public Security findById(Long id) {
        return em.find(Security.class, id);
    }
    
    @Override
    public List<Security> findAll() {
        TypedQuery<Security> query = em.createQuery("SELECT s FROM Security s ORDER BY s.ticker", Security.class);
        return query.getResultList();
    }
    
    @Override
    public List<Security> findByTickerOrName(String queryText) {
        if (queryText == null || queryText.trim().isEmpty()) {
            return findAll();
        }
        String pattern = "%" + queryText.toLowerCase() + "%";
        return em.createQuery(
                        "SELECT s FROM Security s WHERE LOWER(s.ticker) LIKE :pattern OR LOWER(s.name) LIKE :pattern",
                        Security.class)
                .setParameter("pattern", pattern)
                .getResultList();
    }
    
    @Override
    public List<Security> findByIssuer(Issuer issuer) {
        return em.createQuery("SELECT s FROM Security s WHERE s.issuer = :issuer", Security.class)
                 .setParameter("issuer", issuer)
                 .getResultList();
    }
    
    @Override
    public void save(Security security) {
        boolean transactionActive = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            if (security.getId() == null) {
                em.persist(security);
            } else {
                security = em.merge(security);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при сохранении бумаги", e);
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
            
            Security security = findById(id);
            if (security != null) {
                em.remove(security);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при удалении бумаги", e);
        }
    }
} 