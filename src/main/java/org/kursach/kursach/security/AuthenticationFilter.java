package org.kursach.kursach.security;

import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Фильтр, который обеспечивает целостность аутентификации между запросами
 */
@WebFilter(urlPatterns = {"*.xhtml"})
public class AuthenticationFilter implements Filter {

    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

    @Inject
    private LoginController loginController;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация не требуется
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        // Получаем путь запрашиваемой страницы
        String requestURI = httpRequest.getRequestURI();
        
        // Логируем запрос для отладки
        logger.info("Обработка запроса: " + requestURI);
        
        // Если это страница логина или логаут, пропускаем проверки
        if (requestURI.contains("/login.xhtml") || requestURI.contains("/login-error.xhtml") || 
            requestURI.contains("/login-simple.html") || requestURI.contains("/loginServlet") ||
            requestURI.contains("/resources/") || requestURI.contains("/javax.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }
        
        boolean isAuthenticated = false;
        
        try {
            // Проверяем существование сессии
            if (session == null) {
                logger.info("Сессия отсутствует, создаем новую");
                session = httpRequest.getSession(true);
            }
            
            // Синхронизация данных между сессией и LoginController
            Boolean authenticated = (Boolean) session.getAttribute("authenticated");
            String username = (String) session.getAttribute("username");
            String userRole = (String) session.getAttribute("userRole");
            
            if (authenticated != null && authenticated) {
                isAuthenticated = true;
                // Если пользователь аутентифицирован в сессии, но не в LoginController
                if (!loginController.isAuthenticated(httpRequest)) {
                    loginController.setAuthenticated(true);
                    loginController.setUsername(username);
                    // Тут можно также установить другие атрибуты
                }
            } else if (loginController.isAuthenticated(httpRequest)) {
                isAuthenticated = true;
                // Если пользователь аутентифицирован в LoginController, но не в сессии
                session.setAttribute("authenticated", true);
                session.setAttribute("username", loginController.getUsername());
                // Определяем роль
                if (loginController.isAdmin(httpRequest)) {
                    session.setAttribute("userRole", "ADMIN");
                } else if (loginController.isTeacher(httpRequest)) {
                    session.setAttribute("userRole", "TEACHER");
                } else if (loginController.isStudent(httpRequest)) {
                    session.setAttribute("userRole", "STUDENT");
                }
            }
        } catch (Exception e) {
            logger.warning("Ошибка при обработке аутентификации: " + e.getMessage());
        }
        
        // Проверка авторизации и перенаправление на страницу логина, если пользователь не авторизован
        if (!isAuthenticated) {
            logger.info("Пользователь не авторизован, перенаправление на страницу входа");
            String contextPath = httpRequest.getContextPath();
            httpResponse.sendRedirect(contextPath + "/login.xhtml");
            return;
        }
        
        // Продолжаем цепочку фильтров
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Очистка не требуется
    }
} 