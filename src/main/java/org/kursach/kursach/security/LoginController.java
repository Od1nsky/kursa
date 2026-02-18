package org.kursach.kursach.security;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serializable;

@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean authenticated;
    private String username;
    private String password;
    
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        
        try {
            request.login(username, password);
            authenticated = true;
            
            // Сохраняем имя пользователя для отображения
            this.username = username;
            
            return "index?faces-redirect=true";
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка входа", "Неверные учетные данные"));
            return null;
        }
    }
    
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        HttpSession session = request.getSession(false);
        
        try {
            // Очищаем атрибуты сессии
            if (session != null) {
                session.removeAttribute("authenticated");
                session.removeAttribute("username");
                session.removeAttribute("userRole");
            }
            
            // Очищаем состояние компонента
            authenticated = false;
            username = null;
            password = null;
            
            // Пробуем выполнить стандартный logout (для контейнерной аутентификации)
            try {
                request.logout();
            } catch (ServletException e) {
                // Игнорируем ошибку, если контейнерная аутентификация не использовалась
            }
            
            // Инвалидируем сессию и создаем новую
            externalContext.invalidateSession();
            externalContext.getSession(true);
            
            return "index?faces-redirect=true";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка выхода", "Не удалось выйти из системы"));
            return null;
        }
    }
    
    // Новый метод для проверки сессии через HttpServletRequest
    public boolean isAuthenticated(HttpServletRequest request) {
        if (authenticated) {
            return true;
        }
        
        // Проверка атрибута сессии
        HttpSession session = request.getSession(false);
        
        if (session != null && session.getAttribute("authenticated") != null) {
            Boolean sessionAuth = (Boolean) session.getAttribute("authenticated");
            if (sessionAuth) {
                // Синхронизируем данные с сессией
                authenticated = true;
                username = (String) session.getAttribute("username");
                return true;
            }
        }
        
        return false;
    }
    
    // Стандартный метод для использования в JSF
    public boolean isAuthenticated() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                return isAuthenticated(request);
            }
        } catch (Exception e) {
            // Логирование ошибки, если нужно
        }
        
        // Если FacesContext недоступен, используем только локальное состояние
        return authenticated;
    }
    
    // Новые методы для проверки ролей с HttpServletRequest
    public boolean isAdmin(HttpServletRequest request) {
        // Проверка метода контейнерной аутентификации
        if (request.isUserInRole("ADMIN")) {
            return true;
        }
        
        // Проверка метода сессионной аутентификации
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userRole = (String) session.getAttribute("userRole");
            return "ADMIN".equals(userRole);
        }
        
        return false;
    }
    
    public boolean isTeacher(HttpServletRequest request) {
        // Проверка метода контейнерной аутентификации
        if (request.isUserInRole("TEACHER")) {
            return true;
        }
        
        // Проверка метода сессионной аутентификации
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userRole = (String) session.getAttribute("userRole");
            return "TEACHER".equals(userRole);
        }
        
        return false;
    }
    
    public boolean isStudent(HttpServletRequest request) {
        // Проверка метода контейнерной аутентификации
        if (request.isUserInRole("STUDENT")) {
            return true;
        }
        
        // Проверка метода сессионной аутентификации
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userRole = (String) session.getAttribute("userRole");
            return "STUDENT".equals(userRole);
        }
        
        return false;
    }
    
    // Обновленные методы JSF для проверки ролей
    public boolean isAdmin() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                return isAdmin(request);
            }
        } catch (Exception e) {
            // Логирование ошибки, если нужно
        }
        return false;
    }
    
    public boolean isTeacher() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                return isTeacher(request);
            }
        } catch (Exception e) {
            // Логирование ошибки, если нужно
        }
        return false;
    }
    
    public boolean isStudent() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                return isStudent(request);
            }
        } catch (Exception e) {
            // Логирование ошибки, если нужно
        }
        return false;
    }
    
    // Геттеры и сеттеры
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 