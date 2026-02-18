-- ============================================
-- SQL ЗАПРОСЫ ДЛЯ ПОЛУЧЕНИЯ ДАННЫХ ИЗ БД
-- База данных: kursach_vlad
-- ============================================

-- ============================================
-- 1. ПРОСТЫЕ SELECT ЗАПРОСЫ
-- ============================================

-- Получить всех эмитентов
SELECT * FROM issuer;

-- Получить все ценные бумаги
SELECT * FROM security;

-- Получить все инвестиционные счета
SELECT * FROM investment_account;

-- Получить все транзакции
SELECT * FROM trade_transaction;

-- ============================================
-- 2. ЗАПРОСЫ С ФИЛЬТРАЦИЕЙ (WHERE)
-- ============================================

-- Получить эмитентов из определенной страны
SELECT * FROM issuer 
WHERE country = 'Россия';

-- Получить эмитентов по сектору
SELECT * FROM issuer 
WHERE sector = 'Энергетика';

-- Получить акции (STOCK)
SELECT * FROM security 
WHERE security_type = 'STOCK';

-- Получить облигации (BOND)
SELECT * FROM security 
WHERE security_type = 'BOND';

-- Получить ценные бумаги с дивидендной доходностью более 5%
SELECT * FROM security 
WHERE dividend_yield > 5.0;

-- Получить инвестиционные счета с определенной стратегией
SELECT * FROM investment_account 
WHERE strategy = 'Агрессивная';

-- Получить транзакции типа BUY (покупка)
SELECT * FROM trade_transaction 
WHERE transaction_type = 'BUY';

-- Получить транзакции типа SELL (продажа)
SELECT * FROM trade_transaction 
WHERE transaction_type = 'SELL';

-- Получить транзакции за последние 7 дней
SELECT * FROM trade_transaction 
WHERE trade_date >= CURRENT_DATE - INTERVAL '7 days';

-- ============================================
-- 3. ЗАПРОСЫ С СОРТИРОВКОЙ (ORDER BY)
-- ============================================

-- Эмитенты, отсортированные по названию
SELECT * FROM issuer 
ORDER BY name;

-- Ценные бумаги, отсортированные по цене (от большей к меньшей)
SELECT * FROM security 
ORDER BY last_price DESC NULLS LAST;

-- Ценные бумаги, отсортированные по дивидендной доходности
SELECT * FROM security 
WHERE dividend_yield IS NOT NULL
ORDER BY dividend_yield DESC;

-- Инвестиционные счета, отсортированные по балансу
SELECT * FROM investment_account 
ORDER BY cash_balance DESC NULLS LAST;

-- Транзакции, отсортированные по дате (новые первыми)
SELECT * FROM trade_transaction 
ORDER BY trade_date DESC NULLS LAST;

-- ============================================
-- 4. ЗАПРОСЫ С JOIN (СВЯЗИ МЕЖДУ ТАБЛИЦАМИ)
-- ============================================

-- Ценные бумаги с информацией об эмитенте
SELECT 
    s.id,
    s.ticker,
    s.name AS security_name,
    s.security_type,
    s.currency,
    s.last_price,
    s.dividend_yield,
    s.maturity_date,
    i.name AS issuer_name,
    i.country,
    i.sector,
    i.rating
FROM security s
LEFT JOIN issuer i ON s.issuer_id = i.id
ORDER BY i.name, s.ticker;

-- Транзакции с информацией о ценной бумаге и эмитенте
SELECT 
    t.id AS transaction_id,
    t.trade_date,
    t.transaction_type,
    t.quantity,
    t.price,
    t.fees,
    s.ticker,
    s.name AS security_name,
    s.security_type,
    i.name AS issuer_name
FROM trade_transaction t
LEFT JOIN security s ON t.security_id = s.id
LEFT JOIN issuer i ON s.issuer_id = i.id
ORDER BY t.trade_date DESC;

-- Транзакции с полной информацией (ценная бумага, эмитент, счет)
SELECT 
    t.id AS transaction_id,
    t.trade_date,
    t.transaction_type,
    t.quantity,
    t.price,
    t.fees,
    s.ticker,
    s.name AS security_name,
    s.security_type,
    s.last_price AS current_price,
    i.name AS issuer_name,
    i.sector AS issuer_sector,
    acc.account_number,
    acc.owner_name,
    acc.strategy AS account_strategy
FROM trade_transaction t
LEFT JOIN security s ON t.security_id = s.id
LEFT JOIN issuer i ON s.issuer_id = i.id
LEFT JOIN investment_account acc ON t.account_id = acc.id
ORDER BY t.trade_date DESC;

-- Инвестиционные счета с количеством транзакций
SELECT 
    acc.id,
    acc.account_number,
    acc.owner_name,
    acc.strategy,
    acc.base_currency,
    acc.opened_date,
    acc.cash_balance,
    COUNT(t.id) AS transaction_count
FROM investment_account acc
LEFT JOIN trade_transaction t ON acc.id = t.account_id
GROUP BY acc.id, acc.account_number, acc.owner_name, acc.strategy, acc.base_currency, acc.opened_date, acc.cash_balance
ORDER BY transaction_count DESC;

-- ============================================
-- 5. ЗАПРОСЫ С АГРЕГАЦИЕЙ (GROUP BY, COUNT, SUM, AVG)
-- ============================================

-- Количество ценных бумаг по типам
SELECT 
    security_type,
    COUNT(*) AS count
FROM security
WHERE security_type IS NOT NULL
GROUP BY security_type
ORDER BY count DESC;

-- Количество эмитентов по секторам
SELECT 
    sector,
    COUNT(*) AS issuer_count
FROM issuer
WHERE sector IS NOT NULL
GROUP BY sector
ORDER BY issuer_count DESC;

-- Количество эмитентов по странам
SELECT 
    country,
    COUNT(*) AS issuer_count
FROM issuer
WHERE country IS NOT NULL
GROUP BY country
ORDER BY issuer_count DESC;

-- Средняя цена ценных бумаг по типам
SELECT 
    security_type,
    COUNT(*) AS count,
    AVG(last_price) AS avg_price,
    MIN(last_price) AS min_price,
    MAX(last_price) AS max_price
FROM security
WHERE security_type IS NOT NULL AND last_price IS NOT NULL
GROUP BY security_type
ORDER BY avg_price DESC;

-- Статистика транзакций по типам
SELECT 
    transaction_type,
    COUNT(*) AS transaction_count,
    SUM(quantity * price) AS total_volume,
    AVG(quantity * price) AS avg_volume,
    SUM(fees) AS total_fees
FROM trade_transaction
WHERE transaction_type IS NOT NULL
GROUP BY transaction_type
ORDER BY transaction_count DESC;

-- Общий объем транзакций по счетам
SELECT 
    acc.account_number,
    acc.owner_name,
    COUNT(t.id) AS transaction_count,
    SUM(t.quantity * t.price) AS total_volume,
    SUM(t.fees) AS total_fees
FROM investment_account acc
LEFT JOIN trade_transaction t ON acc.id = t.account_id
GROUP BY acc.id, acc.account_number, acc.owner_name
HAVING COUNT(t.id) > 0
ORDER BY total_volume DESC NULLS LAST;

-- Общий объем транзакций по эмитентам
SELECT 
    i.name AS issuer_name,
    i.sector,
    COUNT(t.id) AS transaction_count,
    SUM(t.quantity * t.price) AS total_volume
FROM issuer i
LEFT JOIN security s ON i.id = s.issuer_id
LEFT JOIN trade_transaction t ON s.id = t.security_id
GROUP BY i.id, i.name, i.sector
HAVING COUNT(t.id) > 0
ORDER BY total_volume DESC NULLS LAST;

-- ============================================
-- 6. ЗАПРОСЫ С УСЛОВИЯМИ И ФИЛЬТРАЦИЕЙ
-- ============================================

-- Эмитенты с рейтингом BBB и выше
SELECT * FROM issuer 
WHERE rating IN ('BBB', 'BBB+', 'A-', 'A', 'A+', 'AA-', 'AA', 'AA+', 'AAA')
ORDER BY rating;

-- Ценные бумаги с дивидендной доходностью более 7%
SELECT 
    s.*,
    i.name AS issuer_name
FROM security s
LEFT JOIN issuer i ON s.issuer_id = i.id
WHERE s.dividend_yield > 7.0
ORDER BY s.dividend_yield DESC;

-- Инвестиционные счета с балансом более 1 миллиона
SELECT * FROM investment_account 
WHERE cash_balance > 1000000
ORDER BY cash_balance DESC;

-- Транзакции на сумму более 100000
SELECT 
    t.*,
    s.ticker,
    s.name AS security_name,
    acc.account_number,
    acc.owner_name
FROM trade_transaction t
LEFT JOIN security s ON t.security_id = s.id
LEFT JOIN investment_account acc ON t.account_id = acc.id
WHERE (t.quantity * t.price) > 100000
ORDER BY (t.quantity * t.price) DESC;

-- ============================================
-- 7. ЗАПРОСЫ С ПОДЗАПРОСАМИ
-- ============================================

-- Эмитенты, у которых есть ценные бумаги с дивидендной доходностью
SELECT * FROM issuer 
WHERE id IN (
    SELECT DISTINCT issuer_id 
    FROM security 
    WHERE dividend_yield IS NOT NULL AND dividend_yield > 0
);

-- Ценные бумаги, по которым были транзакции
SELECT * FROM security 
WHERE id IN (
    SELECT DISTINCT security_id 
    FROM trade_transaction
);

-- Инвестиционные счета, у которых есть транзакции типа BUY
SELECT * FROM investment_account 
WHERE id IN (
    SELECT DISTINCT account_id 
    FROM trade_transaction 
    WHERE transaction_type = 'BUY'
);

-- ============================================
-- 8. ЗАПРОСЫ ДЛЯ ПОИСКА (LIKE, ILIKE)
-- ============================================

-- Поиск эмитентов по названию (регистр не важен)
SELECT * FROM issuer 
WHERE name ILIKE '%банк%'
   OR name ILIKE '%газ%'
ORDER BY name;

-- Поиск ценных бумаг по тикеру
SELECT * FROM security 
WHERE ticker ILIKE '%SBER%'
   OR ticker ILIKE '%GAZP%';

-- Поиск инвестиционных счетов по имени владельца
SELECT * FROM investment_account 
WHERE owner_name ILIKE '%Иван%'
   OR owner_name ILIKE '%Мария%';

-- ============================================
-- 9. ЗАПРОСЫ С ОГРАНИЧЕНИЕМ (LIMIT)
-- ============================================

-- Топ-5 самых дорогих ценных бумаг
SELECT * FROM security 
WHERE last_price IS NOT NULL
ORDER BY last_price DESC 
LIMIT 5;

-- Топ-5 эмитентов с наибольшим количеством ценных бумаг
SELECT 
    i.name,
    COUNT(s.id) AS security_count
FROM issuer i
LEFT JOIN security s ON i.id = s.issuer_id
GROUP BY i.id, i.name
ORDER BY security_count DESC
LIMIT 5;

-- Последние 10 транзакций
SELECT * FROM trade_transaction 
ORDER BY trade_date DESC NULLS LAST, id DESC
LIMIT 10;

-- ============================================
-- 10. ЗАПРОСЫ С ВЫЧИСЛЯЕМЫМИ ПОЛЯМИ
-- ============================================

-- Транзакции с расчетом общей стоимости
SELECT 
    t.id,
    t.trade_date,
    t.transaction_type,
    s.ticker,
    s.name AS security_name,
    t.quantity,
    t.price,
    (t.quantity * t.price) AS gross_value,
    t.fees,
    CASE 
        WHEN t.transaction_type = 'BUY' 
        THEN (t.quantity * t.price) + COALESCE(t.fees, 0)
        ELSE (t.quantity * t.price) - COALESCE(t.fees, 0)
    END AS net_value
FROM trade_transaction t
LEFT JOIN security s ON t.security_id = s.id
ORDER BY t.trade_date DESC;

-- Ценные бумаги с расчетом годовой дивидендной выплаты (примерно)
SELECT 
    s.ticker,
    s.name,
    s.last_price,
    s.dividend_yield,
    (s.last_price * s.dividend_yield / 100) AS estimated_annual_dividend
FROM security s
WHERE s.dividend_yield IS NOT NULL AND s.dividend_yield > 0
ORDER BY estimated_annual_dividend DESC;

-- Инвестиционные счета с расчетом возраста (в днях)
SELECT 
    account_number,
    owner_name,
    opened_date,
    (CURRENT_DATE - opened_date) AS account_age_days,
    cash_balance
FROM investment_account
WHERE opened_date IS NOT NULL
ORDER BY account_age_days DESC;

-- ============================================
-- 11. СЛОЖНЫЕ АНАЛИТИЧЕСКИЕ ЗАПРОСЫ
-- ============================================

-- Портфель по каждому счету (суммарная стоимость позиций)
SELECT 
    acc.account_number,
    acc.owner_name,
    s.ticker,
    s.name AS security_name,
    i.name AS issuer_name,
    SUM(CASE WHEN t.transaction_type = 'BUY' THEN t.quantity ELSE -t.quantity END) AS total_quantity,
    s.last_price AS current_price,
    SUM(CASE WHEN t.transaction_type = 'BUY' THEN t.quantity ELSE -t.quantity END) * s.last_price AS portfolio_value
FROM investment_account acc
LEFT JOIN trade_transaction t ON acc.id = t.account_id
LEFT JOIN security s ON t.security_id = s.id
LEFT JOIN issuer i ON s.issuer_id = i.id
WHERE t.transaction_type IN ('BUY', 'SELL')
GROUP BY acc.account_number, acc.owner_name, s.ticker, s.name, i.name, s.last_price
HAVING SUM(CASE WHEN t.transaction_type = 'BUY' THEN t.quantity ELSE -t.quantity END) > 0
ORDER BY acc.account_number, portfolio_value DESC;

-- Статистика по эмитентам (количество ценных бумаг, средняя цена, общий объем транзакций)
SELECT 
    i.name AS issuer_name,
    i.sector,
    i.rating,
    COUNT(DISTINCT s.id) AS security_count,
    AVG(s.last_price) AS avg_security_price,
    COUNT(t.id) AS transaction_count,
    SUM(t.quantity * t.price) AS total_transaction_volume
FROM issuer i
LEFT JOIN security s ON i.id = s.issuer_id
LEFT JOIN trade_transaction t ON s.id = t.security_id
GROUP BY i.id, i.name, i.sector, i.rating
ORDER BY total_transaction_volume DESC NULLS LAST;

-- Топ ценных бумаг по объему торгов
SELECT 
    s.ticker,
    s.name AS security_name,
    i.name AS issuer_name,
    COUNT(t.id) AS transaction_count,
    SUM(t.quantity) AS total_quantity,
    SUM(t.quantity * t.price) AS total_volume,
    AVG(t.price) AS avg_price
FROM security s
LEFT JOIN issuer i ON s.issuer_id = i.id
LEFT JOIN trade_transaction t ON s.id = t.security_id
GROUP BY s.id, s.ticker, s.name, i.name
HAVING COUNT(t.id) > 0
ORDER BY total_volume DESC;



