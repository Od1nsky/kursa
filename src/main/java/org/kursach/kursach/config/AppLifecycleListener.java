package org.kursach.kursach.config;

import jakarta.faces.annotation.FacesConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * Конфигурация жизненного цикла приложения и активация JSF CDI
 */
@WebListener
@FacesConfig
public class AppLifecycleListener implements ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(AppLifecycleListener.class.getName());
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("============================================");
        logger.info("Подсистема работы с ценными бумагами запущена");
        logger.info("============================================");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("============================================");
        logger.info("Подсистема работы с ценными бумагами остановлена");
        logger.info("============================================");
    }
} 