package org.my.springcloud.producer.utils;


import com.alibaba.cloud.commons.lang.StringUtils;
import org.my.springcloud.base.bean.HumanSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * session 工具类
 * @author yindl
 *
 */
public class SessionUtils {

    private SessionUtils() {}

	private static final Logger logger = LoggerFactory.getLogger(SessionUtils.class);

	public static final String HUMAN_SESSION = "HumanSession";
	// 用于更加方便service层获取人员信息
	public static final ThreadLocal<HumanSession> HUMAN_SESSION_LIST = new ThreadLocal<>();
	public static final String TOKEN_PARAM_NAME = "token";
	// redis中存储人员sessionID的key前缀（用于检查是否多处登录）
	private static final String REDIS_HUMAN_SESSION_KEY_PRE = "TEST:HUMAN:SESSION:";
	/**
	 * Redis session哈希值前缀 @link RedisOperationsSessionRepository  BOUNDED_HASH_KEY_PREFIX
	 */
	private static final String BOUNDED_HASH_KEY_PREFIX = "spring:session:sessions:";
	/**
	 * 最后访问时间关键字  @link RedisOperationsSessionRepository LAST_ACCESSED_ATTR
	 */
	private static final String LAST_ACCESSED_ATTR = "lastAccessedTime";
	private static final String REDIS_HUMANTOKEN_KEY = "HUMAN_TOKEN:";
	// redis中人员sessionID的过期时间，设为1天 （与session过期时间不同）
	private static final long REDIS_HUMAN_SESSION_ID_EXPIRE_TIME = 24 * 3600 * 1000L;


	/**
	 * 认证头名称
	 */
	private static final String TOKEN_HEADER = "Authorization";

	/**
	 * 认证头中token的前缀
	 */
	private static final String TOKEN_HEAD = "Bearer ";

	/**
	 * app/src/main/webapp/library/urban/utils/http.js中的Authorization格式为 Bearer x-token:xxx
	 */
	private static final String X_TOKEN_PREFIX = "x-token:";


	private static Object tokenManager = null;

    private static Boolean sessionOnlyOneFlag = null;


	private static final Map<Integer, Map<String, Map<String, Object>>> queryParamsMap = new ConcurrentHashMap<>();


	/**
	 * 获取HumanSession
	 * @param request
	 * @return
	 */
	public static HumanSession getHumanSession(HttpServletRequest request){
		return (HumanSession) request.getSession().getAttribute(HUMAN_SESSION);
	}

	/**
	 * 获取HumanSession
	 * @param session
	 * @return
	 */
	public static HumanSession getHumanSession(HttpSession session){
		return (HumanSession) session.getAttribute(HUMAN_SESSION);
	}

	/**
	 * 判断是否存在HumanSession
	 * @param request
	 * @return
	 */
	public static boolean humanSessionExist(HttpServletRequest request){
		return SessionUtils.getHumanSession(request) != null;
	}


	/**
	 * session是否被被踢掉
	 * @param request
	 * @return
	 */
	public static boolean humanSessionValid(HttpServletRequest request){
        HumanSession humanSession = SessionUtils.getHumanSession(request);
        //此处如果请求中包含token，并且token合法，需要恢复Session
        String token = getRequestToken(request);
        if(StringUtils.isNotEmpty(token)){
            // 这里也会通过redis和数据库中的token（统一用户中心）去恢复会话，统一用户中心的token也会存放在数据库和redis中
            // 见cn.com.egova.mobilebase.misys.service.impl.MiLoginManagerImpl.baseLogin
            renewSession(token, request);
            humanSession = SessionUtils.getHumanSession(request);
        }
        // 若在城管平台通过token恢复session失败，则尝试通过统一用户中心认证token进行恢复session
        // 这里因为依赖的原因没有提前判断是否启用了统一用户中心，但是在实现中已经判断了
        String accessToken = request.getParameter(TOKEN_PARAM_NAME);
        if(StringUtils.isNotEmpty(accessToken) && (humanSession == null || !humanSession.isValidFlag())){
            renewSessionByUnionAuthAccessToken(accessToken, request);
            humanSession = SessionUtils.getHumanSession(request);
        }

		// 从redis判断是否有其他登录
		if (getSessionOnlyOneFlag() &&
                humanSession != null && request.getSession() != null && request.getSession().getId() != null) {
            String redisSessionID = RedisUtils.get(REDIS_HUMAN_SESSION_KEY_PRE + humanSession.getHumanID()).toString();
            String redisToken = RedisUtils.get(REDIS_HUMANTOKEN_KEY + humanSession.getHumanID()).toString();
            boolean validFlag = true;
            if(StringUtils.isEmpty(token)){
                // 与redis中的sessionID不一致，有其他地方的登录
                validFlag  = redisSessionID == null || redisSessionID.equals(request.getSession().getId());
            } else {
                // 与redis中的token不一致，有其他地方的登录
				String normalizedToken = normalizeToken(token);
				validFlag  = normalizedToken.equals(redisToken);
            }
            humanSession.setValidFlag(validFlag);
            if(!validFlag){
                humanSession.setInvalidMsg("您的登录已过期，同一账户在其他地点登录，您被迫下线！");
            }
		}
		if(humanSession == null){
			return false;
		}
		humanSession.setServerIp(request.getLocalAddr());
		return humanSession.isValidFlag();

	}

	private static boolean renewSession(String token, HttpServletRequest request){
		if(tokenManager == null){
			try{
				tokenManager = BeanUtils.getBean("tokenManagerImpl");
			}catch(Exception e){
				tokenManager = "null";
			}
		}
		// 如果没有 token 的服务，不再重复检查
		if("null".equals(tokenManager)){
			return false;
		}
		try{
		    return (Boolean) ReflectAsmUtils.invokeMethod(tokenManager, "renewSessionByToken",
                    new Class<?>[]{String.class, HttpServletRequest.class}, new Object[]{token, request});
		}catch(Exception e){
			logger.error("刷新token发生错误！token={}", token, e);
			// 发生错误时默认通过
			return true;
		}
	}

	/**
	 * 通过AccessToken恢复Session
	 * @param token accessToken信息
	 * @param request 请求信息
	 * @return boolean
	 */
	private static boolean renewSessionByUnionAuthAccessToken(String token, HttpServletRequest request){
		if(tokenManager == null){
			try{
				tokenManager = org.my.springcloud.producer.utils.BeanUtils.getBean("tokenManagerImpl");
			}catch(Exception e){
				tokenManager = "null";
			}
		}
		// 如果没有 token 的服务，不再重复检查
		if("null".equals(tokenManager)){
			return false;
		}
		try{
			return (Boolean) ReflectAsmUtils.invokeMethod(tokenManager, "renewSessionByUnionAuthAccessToken",
					new Class<?>[]{String.class, HttpServletRequest.class}, new Object[]{token, request});
		}catch(Exception e){
			logger.error("刷新token发生错误！token={}", token, e);
			// 发生错误时默认通过
			return true;
		}
	}

	/**
	 * 获取humanID
	 * @param request
	 * @return
	 */
	public static Integer getHumanID(HttpServletRequest request){
		HumanSession humanSession = SessionUtils.getHumanSession(request);
		if(humanSession != null){
			return humanSession.getHumanID();
		}
		return null;
	}

	/**
	 * 获取humanName
	 * @param request
	 * @return
	 */
	public static String getHumanName(HttpServletRequest request){
		HumanSession humanSession = SessionUtils.getHumanSession(request);
		if(humanSession != null){
			return humanSession.getHumanName();
		}
		return null;
	}

	/**
	 * 获取失效信息
	 * @param request
	 * @return
	 */
	public static String getInvalidMsg(HttpServletRequest request){
		HumanSession humanSession = SessionUtils.getHumanSession(request);
		if(humanSession != null){
			return humanSession.getInvalidMsg();
		}
		return null;
	}

	/**
	 * 登陆
	 * @param request
	 * @param human
	 */
	public static void logon(HttpServletRequest request, HumanSession human){
		if (request.getSession() != null && request.getSession().getId() != null) {
			// 登录时记录humanID与sessionID绑定关系,便于后续通过SessionRepository操作其他session
            RedisUtils.set(REDIS_HUMAN_SESSION_KEY_PRE + human.getHumanID(),
                    request.getSession().getId(), REDIS_HUMAN_SESSION_ID_EXPIRE_TIME);
		}


		HumanSession humanSession = SessionUtils.getHumanSession(request);
		if(humanSession == null){
			request.getSession().setAttribute(SessionUtils.HUMAN_SESSION, human);
			SessionUtils.setHumanSession(human);
		}else{//防止没有注销的情况
			if(humanSession.getHumanID().intValue() != human.getHumanID().intValue()){//使用其他账户同一地点登录，记录注销日志
				if(humanSession.getLogID() != null){
					//加注销记录
				}
				SessionUtils.logout(request);
				request.getSession().setAttribute(SessionUtils.HUMAN_SESSION, human);
			}else{
				human.setFromCas(humanSession.getFromCas());
				human.setFromToken(humanSession.getFromToken());
				request.getSession().setAttribute(SessionUtils.HUMAN_SESSION, human);
			}
		}

	}

	/**
	 * 获取客户端IP地址
	 * @param request
	 * @return
	 */
	public static String getIPAddress(HttpServletRequest request) {
		// 先从nginx自定义配置获取
		String ip = request.getHeader("X-Real-IP");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("x-forwarded-for");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 退出登录session失效
	 * @param request
	 */
	public static void logout(HttpServletRequest request){
		if (getSessionOnlyOneFlag()) {
			// redis 中删除key
			HumanSession humanSession = getHumanSession(request);
			if(humanSession != null){
				RedisUtils.del(REDIS_HUMAN_SESSION_KEY_PRE + humanSession.getHumanID());
			}
		}
		HttpSession session = request.getSession();
		SessionUtils.removeHumanList();
		if(session != null){
			session.removeAttribute(HUMAN_SESSION);
			session.invalidate();
		}
	}
	public static void setHumanSession(HumanSession humanSession){
		HUMAN_SESSION_LIST.set(humanSession);
	}

	public static void setHumanSession(HttpServletRequest request, HumanSession humanSession){
		if(request.getSession() != null) {
			request.getSession().setAttribute(SessionUtils.HUMAN_SESSION, humanSession);
			SessionUtils.setHumanSession(humanSession);
		}
	}


	public static void removeHumanList(){
		HUMAN_SESSION_LIST.remove();
	}

	public static HumanSession getHumanSession(){
		return HUMAN_SESSION_LIST.get();
	}

	/**
	 * 设置Session登陆标识
	 * @param request 请求信息
	 * @param logID log标识
	 */
	public static void setLogID(HttpServletRequest request, int logID){
		HumanSession humanSession = getHumanSession(request);
		if(humanSession != null){
			humanSession.setLogID(logID);
		}
	}


	/**
	 * 是否允许同一账号多处登录
	 */
	private static boolean getSessionOnlyOneFlag(){
		if (sessionOnlyOneFlag == null) {
			String flag = "false";
			sessionOnlyOneFlag = "false".equals(flag);
		}
		return sessionOnlyOneFlag;
	}



	private static String getRequestToken(HttpServletRequest request) {
		String headerValue = request.getHeader(TOKEN_HEADER);
		if(StringUtils.isNotEmpty(headerValue) && headerValue.startsWith(TOKEN_HEAD)) {
			return headerValue.substring(TOKEN_HEAD.length());
		}
		return request.getParameter(TOKEN_PARAM_NAME);
	}


	/**
	 * 与redis中的token比较前，使token规范化
	 */
	private static String normalizeToken(String token) {
		if (StringUtils.isEmpty(token)) {
			return "";
		}
		if (token.startsWith(X_TOKEN_PREFIX)) {
			return token.substring(X_TOKEN_PREFIX.length());
		}
		return token;
	}
}
