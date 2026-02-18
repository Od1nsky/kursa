package org.kursach.kursach.repository;

import java.util.List;

/**
 * Базовый интерфейс для всех репозиториев
 * @param <T> Тип сущности
 * @param <ID> Тип идентификатора
 */
public interface Repository<T, ID> {
    
    /**
     * Найти все сущности
     * @return список всех сущностей
     */
    List<T> findAll();
    
    /**
     * Найти сущность по идентификатору
     * @param id идентификатор
     * @return найденная сущность или null
     */
    T findById(ID id);
    
    /**
     * Сохранить сущность
     * @param entity сущность для сохранения
     */
    void save(T entity);
    
    /**
     * Удалить сущность по идентификатору
     * @param id идентификатор сущности для удаления
     */
    void delete(ID id);
} 