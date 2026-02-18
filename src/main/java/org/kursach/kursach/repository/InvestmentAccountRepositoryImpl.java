package org.kursach.kursach.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.kursach.kursach.model.InvestmentAccount;

import java.util.List;

@ApplicationScoped
public class InvestmentAccountRepositoryImpl implements InvestmentAccountRepository {
    public InvestmentAccountRepositoryImpl() {}
    
    @Inject
    private EntityManager em;
    
    @Override
    public InvestmentAccount findById(Long id) {
        return em.find(InvestmentAccount.class, id);
    }
    
    @Override
    public List<InvestmentAccount> findAll() {
        TypedQuery<InvestmentAccount> query = em.createQuery("SELECT a FROM InvestmentAccount a ORDER BY a.accountNumber",
                InvestmentAccount.class);
        return query.getResultList();
    }
    
    @Override
    public List<InvestmentAccount> findByOwner(String owner) {
        if (owner == null || owner.isBlank()) {
            return findAll();
        }
        String pattern = "%" + owner.toLowerCase() + "%";
        return em.createQuery(
                        "SELECT a FROM InvestmentAccount a WHERE LOWER(a.ownerName) LIKE :owner",
                        InvestmentAccount.class)
                .setParameter("owner", pattern)
                .getResultList();
    }
    
    @Override
    public List<InvestmentAccount> findByStrategy(String strategy) {
        if (strategy == null || strategy.isBlank()) {
            return findAll();
        }
        String pattern = "%" + strategy.toLowerCase() + "%";
        return em.createQuery(
                        "SELECT a FROM InvestmentAccount a WHERE LOWER(a.strategy) LIKE :strategy",
                        InvestmentAccount.class)
                .setParameter("strategy", pattern)
                .getResultList();
    }
    
    @Override
    public void save(InvestmentAccount account) {
        boolean transactionActive = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            if (account.getId() == null) {
                em.persist(account);
            } else {
                account = em.merge(account);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при сохранении инвестиционного счёта", e);
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
            
            InvestmentAccount account = findById(id);
            if (account != null) {
                em.remove(account);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при удалении инвестиционного счёта", e);
        }
    }
} 