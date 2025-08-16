package com.bookstore.Filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 过滤所有 /user/** 请求（可改为需要登录验证的路径）
@WebFilter(urlPatterns = {"/*"})
public class LoginCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        System.out.println("过滤器拦截路径: " + uri);

        // 放行登录页和登录接口，静态资源
        if (uri.equals(contextPath + "/login") || uri.equals(contextPath + "/login/")
                || uri.equals(contextPath + "/users/register")
                || uri.equals(contextPath + "/users/save")
                || uri.startsWith(contextPath + "/css/")
                || uri.startsWith(contextPath + "/js/")
                || uri.startsWith(contextPath + "/images/")) {
            chain.doFilter(request, response);
            return;
        }

        Object user = req.getSession().getAttribute("loggedInUser");

        System.out.println("Session中用户对象: " + user);

        if (user == null) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

}
