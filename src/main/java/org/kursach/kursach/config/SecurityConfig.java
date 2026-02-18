package org.kursach.kursach.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Named;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * Настройка безопасности и интеграции JSF с CDI
 */
@Named
@ApplicationScoped
@WebListener
public class SecurityConfig implements ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Явно указываем, что CDI доступен для JSF
        logger.info("Инициализация интеграции JSF с CDI");
        System.setProperty("com.sun.faces.injectionProvider", "com.sun.faces.vendor.WebContainerInjectionProvider");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Нет необходимости в очистке
    }
} 