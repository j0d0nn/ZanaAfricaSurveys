package org.zanaafrica.surveys.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Spring MVC interceptor class, responsible for ensuring that users are logged in and optionally app administrators.
 * 
 * @author jodonnell
 */
public class LoginInterceptor extends HandlerInterceptorAdapter
{
    private boolean requireLogin;
    private boolean requireAdmin;
    
    public boolean isRequireLogin()
    {
        return requireLogin;
    }
    public void setRequireLogin(boolean requireLogin)
    {
        this.requireLogin = requireLogin;
    }
    public boolean isRequireAdmin()
    {
        return requireAdmin;
    }
    public void setRequireAdmin(boolean requireAdmin)
    {
        this.requireAdmin = requireAdmin;
    }
    
    /**
     * If the interceptor requires a login and the user isn't logged in, sends the user to a login page that will redirect back here
     * afterwards.  Same deal if the user is an administrator and the interceptor requires that.
     */
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler) throws Exception
    {
        final UserService svc = UserServiceFactory.getUserService();
        if (!svc.isUserLoggedIn() && this.isRequireLogin())
        {
            requireLogin(request, response);
            return false;
        }
        if (!svc.isUserAdmin() && this.isRequireAdmin())
        {
            
            requireLogin(request, response);
            return false;
        }
        return true;
    }
    
    private void requireLogin(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException
    {
        response.sendRedirect(
            UserServiceFactory.getUserService().createLoginURL(request.getRequestURL().toString())
        );
    }
    
}
