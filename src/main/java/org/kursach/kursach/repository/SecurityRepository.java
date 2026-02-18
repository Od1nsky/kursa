package org.kursach.kursach.repository;

import org.kursach.kursach.model.Issuer;
import org.kursach.kursach.model.Security;

import java.util.List;

public interface SecurityRepository extends Repository<Security, Long> {
    
    List<Security> findByTickerOrName(String query);
    
    List<Security> findByIssuer(Issuer issuer);
}