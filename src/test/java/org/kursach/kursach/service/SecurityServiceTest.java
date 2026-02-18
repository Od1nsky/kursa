package org.kursach.kursach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.kursach.kursach.model.Issuer;
import org.kursach.kursach.model.Security;
import org.kursach.kursach.model.SecurityType;
import org.kursach.kursach.repository.SecurityRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    @Mock
    private SecurityRepository securityRepository;

    @InjectMocks
    private SecurityService securityService;

    private Issuer issuer;
    private Security security1;
    private Security security2;

    @BeforeEach
    void setUp() {
        // Создаем тестовые данные
        issuer = new Issuer("Test Corp", "США", "IT", "A", "Описание");
        issuer.setId(1L);
        
        security1 = new Security();
        security1.setId(1L);
        security1.setIssuer(issuer);
        security1.setTicker("TST1");
        security1.setName("Test Paper 1");
        security1.setType(SecurityType.STOCK);
        security1.setCurrency("USD");
        
        security2 = new Security();
        security2.setId(2L);
        security2.setIssuer(issuer);
        security2.setTicker("TST2");
        security2.setName("Test Paper 2");
        security2.setType(SecurityType.BOND);
        security2.setCurrency("USD");
    }

    @Test
    void testGetAllSecurities() {
        List<Security> securities = Arrays.asList(security1, security2);
        when(securityRepository.findAll()).thenReturn(securities);

        List<Security> result = securityService.getAllSecurities();

        assertEquals(2, result.size());
        assertEquals("Test Paper 1", result.get(0).getName());
        assertEquals("Test Paper 2", result.get(1).getName());
        verify(securityRepository, times(1)).findAll();
    }

    @Test
    void testGetSecurityById() {
        when(securityRepository.findById(1L)).thenReturn(security1);

        Security result = securityService.getSecurityById(1L);

        assertNotNull(result);
        assertEquals("Test Paper 1", result.getName());
        verify(securityRepository, times(1)).findById(1L);
    }

    @Test
    void testSearchSecurities() {
        String query = "Test";
        when(securityRepository.findByTickerOrName(query)).thenReturn(Arrays.asList(security1));

        List<Security> result = securityService.searchSecurities(query);

        assertEquals(1, result.size());
        assertEquals("Test Paper 1", result.get(0).getName());
        verify(securityRepository, times(1)).findByTickerOrName(query);
    }

    @Test
    void testGetSecuritiesByIssuer() {
        List<Security> securities = Arrays.asList(security1, security2);
        when(securityRepository.findByIssuer(issuer)).thenReturn(securities);

        List<Security> result = securityService.getSecuritiesByIssuer(issuer);

        assertEquals(2, result.size());
        verify(securityRepository, times(1)).findByIssuer(issuer);
    }

    @Test
    void testSaveSecurity() {
        securityService.saveSecurity(security1);
        verify(securityRepository, times(1)).save(security1);
    }

    @Test
    void testDeleteSecurity() {
        securityService.deleteSecurity(1L);
        verify(securityRepository, times(1)).delete(1L);
    }
} 