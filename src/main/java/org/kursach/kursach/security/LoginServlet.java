package org.kursach.kursach.security;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Сервлет для обработки аутентификации вручную
 */
@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private LoginController loginController;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("j_username");
        String password = request.getParameter("j_password");
        
        // Простая проверка учетных данных, в реальности лучше использовать хеширование
        boolean isAuthenticated = false;
        String userRole = null;
        
        if ("admin".equals(username) && "admin".equals(password)) {
            isAuthenticated = true;
            userRole = "ADMIN";
        } else if ("teacher".equals(username) && "teacher".equals(password)) {
            isAuthenticated = true;
            userRole = "TEACHER";
        } else if ("student".equals(username) && "student".equals(password)) {
            isAuthenticated = true;
            userRole = "STUDENT";
        }
        
        if (isAuthenticated) {
            // Успешная аутентификация
            
            // 1. Устанавливаем атрибуты сессии
            HttpSession session = request.getSession(true);
            session.setAttribute("authenticated", true);
            session.setAttribute("username", username);
            session.setAttribute("userRole", userRole);
            
            // 2. Обновляем LoginController, если он доступен
            if (loginController != null) {
                loginController.setAuthenticated(true);
                loginController.setUsername(username);
            }
            
            // 3. Пробуем также выполнить контейнерную аутентификацию
            try {
                request.login(username, password);
            } catch (ServletException e) {
                // Игнорируем ошибку, если контейнерная аутентификация не работает
                log("Не удалось выполнить контейнерную аутентификацию: " + e.getMessage());
            }
            
            // Перенаправление на главную страницу
            response.sendRedirect(request.getContextPath() + "/index.xhtml");
        } else {
            // Ошибка аутентификации
            response.sendRedirect(request.getContextPath() + "/login-error.xhtml");
        }
    }
} 