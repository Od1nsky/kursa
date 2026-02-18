package org.kursach.kursach.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import org.kursach.kursach.model.Issuer;
import org.kursach.kursach.model.Security;
import org.kursach.kursach.model.SecurityType;
import org.kursach.kursach.model.InvestmentAccount;
import org.kursach.kursach.model.Transaction;
import org.kursach.kursach.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class DataInitializer {
    
    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());
    
    @Inject
    private EntityManager em;
    
    @PostConstruct
    public void init() {
        try {
            // Проверяем, есть ли уже данные
            Long issuerCount = em.createQuery("SELECT COUNT(i) FROM Issuer i", Long.class).getSingleResult();
            
            if (issuerCount == 0) {
                logger.info("Инициализация тестовых данных...");
                
                // Создаем 10 эмитентов
                Issuer[] issuers = new Issuer[10];
                issuers[0] = new Issuer("Газпром", "Россия", "Энергетика", "BBB+", "Крупнейшая газовая компания России");
                issuers[1] = new Issuer("Сбербанк", "Россия", "Финансы", "BBB", "Крупнейший банк России");
                issuers[2] = new Issuer("Лукойл", "Россия", "Энергетика", "BBB+", "Одна из крупнейших нефтяных компаний");
                issuers[3] = new Issuer("Яндекс", "Россия", "Технологии", "BB+", "Крупнейшая IT-компания России");
                issuers[4] = new Issuer("Норникель", "Россия", "Металлургия", "BBB", "Крупнейший производитель никеля");
                issuers[5] = new Issuer("Роснефть", "Россия", "Энергетика", "BBB", "Крупнейшая нефтяная компания");
                issuers[6] = new Issuer("ВТБ", "Россия", "Финансы", "BB+", "Второй по величине банк России");
                issuers[7] = new Issuer("МТС", "Россия", "Телекоммуникации", "BB", "Крупнейший оператор связи");
                issuers[8] = new Issuer("Магнит", "Россия", "Ритейл", "BB", "Крупнейшая сеть магазинов");
                issuers[9] = new Issuer("Аэрофлот", "Россия", "Транспорт", "B+", "Национальный авиаперевозчик");
                
                em.getTransaction().begin();
                for (Issuer issuer : issuers) {
                    em.persist(issuer);
                }
                em.getTransaction().commit();
                
                // Обновляем эмитентов, чтобы получить их ID
                em.clear();
                for (int i = 0; i < issuers.length; i++) {
                    issuers[i] = em.find(Issuer.class, issuers[i].getId());
                }
                
                // Создаем 10 ценных бумаг
                Security[] securities = new Security[10];
                securities[0] = new Security(issuers[0], "GAZP", "Акции Газпром", SecurityType.STOCK, "RUB");
                securities[0].setLastPrice(new BigDecimal("180.50"));
                securities[0].setDividendYield(new BigDecimal("5.2"));
                
                securities[1] = new Security(issuers[1], "SBER", "Акции Сбербанк", SecurityType.STOCK, "RUB");
                securities[1].setLastPrice(new BigDecimal("285.30"));
                securities[1].setDividendYield(new BigDecimal("8.1"));
                
                securities[2] = new Security(issuers[2], "LKOH", "Акции Лукойл", SecurityType.STOCK, "RUB");
                securities[2].setLastPrice(new BigDecimal("7450.00"));
                securities[2].setDividendYield(new BigDecimal("6.5"));
                
                securities[3] = new Security(issuers[3], "YNDX", "Акции Яндекс", SecurityType.STOCK, "RUB");
                securities[3].setLastPrice(new BigDecimal("3120.50"));
                securities[3].setDividendYield(new BigDecimal("0.0"));
                
                securities[4] = new Security(issuers[4], "GMKN", "Акции Норникель", SecurityType.STOCK, "RUB");
                securities[4].setLastPrice(new BigDecimal("15800.00"));
                securities[4].setDividendYield(new BigDecimal("9.2"));
                
                securities[5] = new Security(issuers[0], "GAZP-2025", "Облигации Газпром 2025", SecurityType.BOND, "RUB");
                securities[5].setLastPrice(new BigDecimal("100.25"));
                securities[5].setMaturityDate(LocalDate.now().plusYears(1));
                
                securities[6] = new Security(issuers[1], "SBER-ETF", "ETF Сбербанк", SecurityType.ETF, "RUB");
                securities[6].setLastPrice(new BigDecimal("285.00"));
                
                securities[7] = new Security(issuers[6], "VTBR", "Акции ВТБ", SecurityType.STOCK, "RUB");
                securities[7].setLastPrice(new BigDecimal("0.045"));
                securities[7].setDividendYield(new BigDecimal("7.8"));
                
                securities[8] = new Security(issuers[7], "MTSS", "Акции МТС", SecurityType.STOCK, "RUB");
                securities[8].setLastPrice(new BigDecimal("285.75"));
                securities[8].setDividendYield(new BigDecimal("10.5"));
                
                securities[9] = new Security(issuers[2], "LKOH-2026", "Облигации Лукойл 2026", SecurityType.BOND, "RUB");
                securities[9].setLastPrice(new BigDecimal("101.50"));
                securities[9].setMaturityDate(LocalDate.now().plusYears(2));
                
                em.getTransaction().begin();
                for (Security security : securities) {
                    em.persist(security);
                }
                em.getTransaction().commit();
                
                // Обновляем ценные бумаги, чтобы получить их ID
                em.clear();
                for (int i = 0; i < securities.length; i++) {
                    securities[i] = em.find(Security.class, securities[i].getId());
                }
                for (int i = 0; i < issuers.length; i++) {
                    issuers[i] = em.find(Issuer.class, issuers[i].getId());
                }
                
                // Создаем 10 инвестиционных счетов
                InvestmentAccount[] accounts = new InvestmentAccount[10];
                accounts[0] = new InvestmentAccount("ACC-001", "Иван Петров", "RUB");
                accounts[0].setStrategy("Консервативная");
                accounts[0].setOpenedDate(LocalDate.now().minusMonths(12));
                accounts[0].setCashBalance(new BigDecimal("500000.00"));
                
                accounts[1] = new InvestmentAccount("ACC-002", "Мария Сидорова", "RUB");
                accounts[1].setStrategy("Агрессивная");
                accounts[1].setOpenedDate(LocalDate.now().minusMonths(6));
                accounts[1].setCashBalance(new BigDecimal("1200000.00"));
                
                accounts[2] = new InvestmentAccount("ACC-003", "Алексей Смирнов", "RUB");
                accounts[2].setStrategy("Сбалансированная");
                accounts[2].setOpenedDate(LocalDate.now().minusMonths(24));
                accounts[2].setCashBalance(new BigDecimal("800000.00"));
                
                accounts[3] = new InvestmentAccount("ACC-004", "Елена Козлова", "RUB");
                accounts[3].setStrategy("Консервативная");
                accounts[3].setOpenedDate(LocalDate.now().minusMonths(3));
                accounts[3].setCashBalance(new BigDecimal("300000.00"));
                
                accounts[4] = new InvestmentAccount("ACC-005", "Дмитрий Волков", "RUB");
                accounts[4].setStrategy("Агрессивная");
                accounts[4].setOpenedDate(LocalDate.now().minusMonths(18));
                accounts[4].setCashBalance(new BigDecimal("2000000.00"));
                
                accounts[5] = new InvestmentAccount("ACC-006", "Анна Новикова", "RUB");
                accounts[5].setStrategy("Сбалансированная");
                accounts[5].setOpenedDate(LocalDate.now().minusMonths(9));
                accounts[5].setCashBalance(new BigDecimal("750000.00"));
                
                accounts[6] = new InvestmentAccount("ACC-007", "Сергей Морозов", "RUB");
                accounts[6].setStrategy("Консервативная");
                accounts[6].setOpenedDate(LocalDate.now().minusMonths(15));
                accounts[6].setCashBalance(new BigDecimal("600000.00"));
                
                accounts[7] = new InvestmentAccount("ACC-008", "Ольга Павлова", "RUB");
                accounts[7].setStrategy("Агрессивная");
                accounts[7].setOpenedDate(LocalDate.now().minusMonths(2));
                accounts[7].setCashBalance(new BigDecimal("1500000.00"));
                
                accounts[8] = new InvestmentAccount("ACC-009", "Николай Соколов", "RUB");
                accounts[8].setStrategy("Сбалансированная");
                accounts[8].setOpenedDate(LocalDate.now().minusMonths(30));
                accounts[8].setCashBalance(new BigDecimal("950000.00"));
                
                accounts[9] = new InvestmentAccount("ACC-010", "Татьяна Лебедева", "RUB");
                accounts[9].setStrategy("Консервативная");
                accounts[9].setOpenedDate(LocalDate.now().minusMonths(7));
                accounts[9].setCashBalance(new BigDecimal("400000.00"));
                
                em.getTransaction().begin();
                for (InvestmentAccount account : accounts) {
                    em.persist(account);
                }
                em.getTransaction().commit();
                
                // Обновляем счета, чтобы получить их ID
                em.clear();
                for (int i = 0; i < accounts.length; i++) {
                    accounts[i] = em.find(InvestmentAccount.class, accounts[i].getId());
                }
                for (int i = 0; i < securities.length; i++) {
                    securities[i] = em.find(Security.class, securities[i].getId());
                }
                
                // Создаем 10 транзакций
                Transaction[] transactions = new Transaction[10];
                transactions[0] = new Transaction(securities[0], accounts[0], TransactionType.BUY);
                transactions[0].setTradeDate(LocalDate.now().minusDays(5));
                transactions[0].setQuantity(new BigDecimal("100"));
                transactions[0].setPrice(new BigDecimal("180.50"));
                transactions[0].setFees(new BigDecimal("50.00"));
                
                transactions[1] = new Transaction(securities[1], accounts[1], TransactionType.BUY);
                transactions[1].setTradeDate(LocalDate.now().minusDays(3));
                transactions[1].setQuantity(new BigDecimal("200"));
                transactions[1].setPrice(new BigDecimal("285.30"));
                transactions[1].setFees(new BigDecimal("100.00"));
                
                transactions[2] = new Transaction(securities[2], accounts[2], TransactionType.BUY);
                transactions[2].setTradeDate(LocalDate.now().minusDays(10));
                transactions[2].setQuantity(new BigDecimal("50"));
                transactions[2].setPrice(new BigDecimal("7450.00"));
                transactions[2].setFees(new BigDecimal("200.00"));
                
                transactions[3] = new Transaction(securities[0], accounts[0], TransactionType.SELL);
                transactions[3].setTradeDate(LocalDate.now().minusDays(1));
                transactions[3].setQuantity(new BigDecimal("30"));
                transactions[3].setPrice(new BigDecimal("182.00"));
                transactions[3].setFees(new BigDecimal("30.00"));
                
                transactions[4] = new Transaction(securities[3], accounts[3], TransactionType.BUY);
                transactions[4].setTradeDate(LocalDate.now().minusDays(7));
                transactions[4].setQuantity(new BigDecimal("150"));
                transactions[4].setPrice(new BigDecimal("3120.50"));
                transactions[4].setFees(new BigDecimal("150.00"));
                
                transactions[5] = new Transaction(securities[4], accounts[4], TransactionType.BUY);
                transactions[5].setTradeDate(LocalDate.now().minusDays(4));
                transactions[5].setQuantity(new BigDecimal("20"));
                transactions[5].setPrice(new BigDecimal("15800.00"));
                transactions[5].setFees(new BigDecimal("250.00"));
                
                transactions[6] = new Transaction(securities[5], accounts[5], TransactionType.BUY);
                transactions[6].setTradeDate(LocalDate.now().minusDays(6));
                transactions[6].setQuantity(new BigDecimal("1000"));
                transactions[6].setPrice(new BigDecimal("100.25"));
                transactions[6].setFees(new BigDecimal("100.00"));
                
                transactions[7] = new Transaction(securities[1], accounts[1], TransactionType.DIVIDEND);
                transactions[7].setTradeDate(LocalDate.now().minusDays(2));
                transactions[7].setQuantity(new BigDecimal("200"));
                transactions[7].setPrice(new BigDecimal("23.10"));
                transactions[7].setFees(BigDecimal.ZERO);
                
                transactions[8] = new Transaction(securities[6], accounts[6], TransactionType.BUY);
                transactions[8].setTradeDate(LocalDate.now().minusDays(8));
                transactions[8].setQuantity(new BigDecimal("500"));
                transactions[8].setPrice(new BigDecimal("285.00"));
                transactions[8].setFees(new BigDecimal("120.00"));
                
                transactions[9] = new Transaction(securities[7], accounts[7], TransactionType.BUY);
                transactions[9].setTradeDate(LocalDate.now().minusDays(9));
                transactions[9].setQuantity(new BigDecimal("10000"));
                transactions[9].setPrice(new BigDecimal("0.045"));
                transactions[9].setFees(new BigDecimal("50.00"));
                
                em.getTransaction().begin();
                for (Transaction transaction : transactions) {
                    em.persist(transaction);
                }
                em.getTransaction().commit();
                
                logger.info("Тестовые данные успешно инициализированы");
            } else {
                logger.info("База данных уже содержит данные, инициализация пропущена");
            }
        } catch (Exception e) {
            logger.severe("Ошибка при инициализации тестовых данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
