package org.kursach.kursach.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.kursach.kursach.model.InvestmentAccount;
import org.kursach.kursach.model.Security;
import org.kursach.kursach.model.Transaction;
import org.kursach.kursach.model.TransactionType;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class TransactionRepositoryImpl implements TransactionRepository {
    public TransactionRepositoryImpl() {}
    
    @Inject
    private EntityManager em;
    
    @Override
    public Transaction findById(Long id) {
        return em.find(Transaction.class, id);
    }
    
    @Override
    public List<Transaction> findAll() {
        TypedQuery<Transaction> query = em.createQuery("SELECT t FROM Transaction t ORDER BY t.tradeDate DESC",
                Transaction.class);
        return query.getResultList();
    }
    
    @Override
    public List<Transaction> findByAccount(InvestmentAccount account) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.account = :account ORDER BY t.tradeDate DESC",
                        Transaction.class)
                .setParameter("account", account)
                .getResultList();
    }
    
    @Override
    public List<Transaction> findBySecurity(Security security) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.security = :security ORDER BY t.tradeDate DESC",
                        Transaction.class)
                .setParameter("security", security)
                .getResultList();
    }
    
    @Override
    public List<Transaction> findByPeriod(LocalDate from, LocalDate to) {
        return em.createQuery(
                        "SELECT t FROM Transaction t WHERE t.tradeDate BETWEEN :from AND :to ORDER BY t.tradeDate DESC",
                        Transaction.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
    
    @Override
    public List<Transaction> findByType(TransactionType type) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.type = :type ORDER BY t.tradeDate DESC",
                        Transaction.class)
                .setParameter("type", type)
                .getResultList();
    }
    
    @Override
    public void save(Transaction transaction) {
        boolean transactionActive = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            if (transaction.getId() == null) {
                em.persist(transaction);
            } else {
                transaction = em.merge(transaction);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при сохранении операции", e);
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
            
            Transaction transaction = findById(id);
            if (transaction != null) {
                em.remove(transaction);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при удалении операции", e);
        }
    }
} 