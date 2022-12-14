package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileFilter;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 *
 * 自定义过滤器
 */

//启动类上加入注解@ServletComponentScan

@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        // 1、获取本次请求的URI
        String requestURI = request.getRequestURI(); //   /backend/index.html


        //记录日志
        log.info("拦截到请求:{}",requestURI);

        //定义不需要处理的请求路径   直接放行
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",  // 移动端发送短信
                "/user/login",    // 移动端登录
                "/doc.html",  //swagger访问
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        // 2、判断本次请求是否需要处理(该次访问是否处于登录状态)
        boolean check = check(urls, requestURI);

        // 3、如果不需要处理，直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        // 4-1、判断登录状态(session中含有employee的登录信息)，如果已经登录，则直接放行（后台登录）
        if(request.getSession().getAttribute("employee") !=null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }


        // 4-2、判断登录状态(session中含有employee的登录信息)，如果已经登录，则直接放行(移动端登录)
        if(request.getSession().getAttribute("user") !=null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        // 5、如果未登录，则返回未登录的结果,通过输出流 向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        // 返回结果需要是 backend/js/request.js 中写好的返回结果

        log.info("拦截到请求：{}",request.getRequestURI());
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }

}
