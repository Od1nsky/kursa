package org.kursach.kursach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.kursach.kursach.model.Issuer;
import org.kursach.kursach.repository.IssuerRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssuerServiceTest {

    @Mock
    private IssuerRepository issuerRepository;

    @InjectMocks
    private IssuerService issuerService;

    private Issuer issuer1;
    private Issuer issuer2;

    @BeforeEach
    void setUp() {
        issuer1 = new Issuer("Test Corp", "США", "IT", "A", "Первый эмитент");
        issuer1.setId(1L);
        
        issuer2 = new Issuer("Energy Group", "Россия", "Энергетика", "BBB", "Второй эмитент");
        issuer2.setId(2L);
    }

    @Test
    void testGetAllIssuers() {
        List<Issuer> issuers = Arrays.asList(issuer1, issuer2);
        when(issuerRepository.findAll()).thenReturn(issuers);

        List<Issuer> result = issuerService.getAllIssuers();

        assertEquals(2, result.size());
        assertEquals("Test Corp", result.get(0).getName());
        assertEquals("Energy Group", result.get(1).getName());
        verify(issuerRepository, times(1)).findAll();
    }

    @Test
    void testSearchIssuers() {
        String query = "Test";
        when(issuerRepository.findByNameOrTicker(query)).thenReturn(Arrays.asList(issuer1));

        List<Issuer> result = issuerService.searchIssuers(query);

        assertEquals(1, result.size());
        assertEquals("Test Corp", result.get(0).getName());
        verify(issuerRepository, times(1)).findByNameOrTicker(query);
    }

    @Test
    void testGetIssuerById() {
        when(issuerRepository.findById(1L)).thenReturn(issuer1);

        Issuer result = issuerService.getIssuerById(1L);

        assertNotNull(result);
        assertEquals("Test Corp", result.getName());
        verify(issuerRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveIssuer() {
        issuerService.saveIssuer(issuer1);
        verify(issuerRepository, times(1)).save(issuer1);
    }

    @Test
    void testDeleteIssuer() {
        issuerService.deleteIssuer(1L);
        verify(issuerRepository, times(1)).delete(1L);
    }
} 