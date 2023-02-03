package org.my.springcloud.producer.filter;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.druid.support.json.JSONUtils;
import org.my.springcloud.base.bean.HumanSession;
import org.my.springcloud.base.bean.ResultInfo;
import org.my.springcloud.producer.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * session过滤器,过滤  "/home/*"
 * @author yindl
 *
 */
public class SessionFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

	private static final Set<String> EXCLUDE_URLS = new HashSet<String>();
	public void destroy(){

	}

	public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri=req.getRequestURI().substring(req.getContextPath().length());
        while(uri.contains("//")){
            uri=uri.replaceAll("//", "/");
        }
        if(EXCLUDE_URLS.contains(uri)){
            chain.doFilter(request, response);
            return;
        }
        if (SessionUtils.humanSessionValid(req)) {
            chain.doFilter(request, response);
        } else {
            if (SessionUtils.humanSessionExist(req)) {
                sessionInvalidHandler(request, response, SessionUtils.getInvalidMsg(req));
            } else {
                sessionInvalidHandler(request, response, "您还未登录，请先登录系统！");
            }
        }
	}
	
	private void sessionInvalidHandler(ServletRequest request, ServletResponse response, String message) throws ServletException, IOException{

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		ResultInfo result = new ResultInfo(false);
		result.setMessage(message);
		result.setCode(HttpServletResponse.SC_UNAUTHORIZED);

		//如果是Ajax请求
		if("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))){
			// 401 session失效
			String jsessionID=req.getSession().getId();
			String token = req.getParameter(SessionUtils.TOKEN_PARAM_NAME);
			result.setData("jsessionID", jsessionID);
			result.setData("token", token);
			String uri=req.getRequestURI().substring(req.getContextPath().length());
			HumanSession human=SessionUtils.getHumanSession(req);
			logger.debug("登录验证失败!uri={},sessionid={},token={},humanSession={}",
					uri, jsessionID, token, human!=null? JSONUtils.toJSONString(human):"null");
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}else{
			//如果是非Ajax请求
			RequestDispatcher rd = request.getRequestDispatcher("/view/exception/message.jsp");
			req.setAttribute("resultInfo", result);
			rd.forward(request, response);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		String excludes = filterConfig.getInitParameter("excludes");
	    if(StringUtils.isNotEmpty(excludes)){
			for(String url : excludes.split(",")){
				EXCLUDE_URLS.add(url.trim());
			}
		}
	}

}
