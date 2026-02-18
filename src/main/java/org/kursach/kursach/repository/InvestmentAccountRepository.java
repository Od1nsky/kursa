package org.kursach.kursach.repository;

import org.kursach.kursach.model.InvestmentAccount;
import java.util.List;

public interface InvestmentAccountRepository extends Repository<InvestmentAccount, Long> {
    
    List<InvestmentAccount> findByOwner(String owner);
    
    List<InvestmentAccount> findByStrategy(String strategy);
}