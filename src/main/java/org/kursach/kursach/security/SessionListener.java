package org.kursach.kursach.security;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.logging.Logger;

/**
 * Слушатель событий сессии для отслеживания создания и уничтожения сессий
 */
@WebListener
public class SessionListener implements HttpSessionListener {
    
    private static final Logger logger = Logger.getLogger(SessionListener.class.getName());
    
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        logger.info("Сессия создана: " + session.getId());
        
        // Увеличиваем время жизни сессии для всего приложения (2 часа)
        session.setMaxInactiveInterval(7200);
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        logger.info("Сессия уничтожена: " + session.getId());
        
        // При уничтожении сессии можно выполнить дополнительные действия
        // Например, логирование выхода пользователя
        String username = (String) session.getAttribute("username");
        if (username != null) {
            logger.info("Пользователь " + username + " вышел из системы (сессия закрыта)");
        }
    }
} 