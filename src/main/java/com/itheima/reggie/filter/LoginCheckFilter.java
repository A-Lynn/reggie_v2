package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录的过滤器
 */

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //类型强转
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取到本次request的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求: {}", requestURI);

        //建立String数组，保存不需要拦截的urls
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",    //移动端发送短信
                "/user/login"       //移动端登录
        };
        //判断本次路径是否需要拦截，调用自定义方法check
        boolean result = check(urls, requestURI);
        //不需要处理，直接放行
        if(result){

            log.info("无需处理: {}", requestURI);

            //放行，并结束方法
            filterChain.doFilter(request, response);
            return;
        }
        //4-1 判断网页端登录状态，Session是否为空，不空表示已登录，则放行
        if(request.getSession().getAttribute("employee") != null){

            log.info("已登录，id为: {}", requestURI, request.getSession().getAttribute("employee"));
            Long empID = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentID(empID);

            //放行，并结束方法
            filterChain.doFilter(request, response);
            return;
        }

        //4-2 判断移动端用户登录状态，Session是否为空，不空表示已登录，则放行
        if(request.getSession().getAttribute("user") != null){

            log.info("已登录，id为: {}", requestURI, request.getSession().getAttribute("user"));
            Long userID = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentID(userID);

            //放行，并结束方法
            filterChain.doFilter(request, response);
            return;
        }


        log.info("用户未登录: {}", requestURI);


        //运行到这里表示用户跨级访问，开始拦截，用JSON传参
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 检查本次request是否需要处理
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
