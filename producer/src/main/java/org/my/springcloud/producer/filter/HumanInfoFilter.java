package org.my.springcloud.producer.filter;
import org.my.springcloud.base.bean.HumanSession;
import org.my.springcloud.producer.utils.SessionUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class HumanInfoFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession httpSession = request.getSession(false);
        SessionUtils.removeHumanList();
        // 没有创建过session就不创建了
        if(httpSession != null) {
            HumanSession humanSession = SessionUtils.getHumanSession(request);
            SessionUtils.setHumanSession(humanSession);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
