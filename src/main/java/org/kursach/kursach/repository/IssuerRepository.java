package org.kursach.kursach.repository;

import org.kursach.kursach.model.Issuer;
import java.util.List;

public interface IssuerRepository extends Repository<Issuer, Long> {
    
    List<Issuer> findByNameOrTicker(String query);
}