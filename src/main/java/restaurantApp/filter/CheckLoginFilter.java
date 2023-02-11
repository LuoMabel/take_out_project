package restaurantApp.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import restaurantApp.common.BaseContext;
import restaurantApp.common.R;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Slf4j
@WebFilter(filterName ="checkLoginFilter",urlPatterns = "/*")
public class CheckLoginFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //路径匹配器，支持通配符
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        //设置无需访问的路径
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/backend/page/demo/upload.html",
                "/common/upload",
                "/common/download",
                "/user/login"
        };

        //获取请求路径
        String requestURI = request.getRequestURI();
        log.info("拦截到请求,{}",requestURI);

        if(check(requestURI,urls)){
            log.info("本次请求{}无需处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，放行");
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，放行");
            Long empId = (Long) request.getSession().getAttribute("user");
            BaseContext.setId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //用户没登陆
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    private boolean check(String requestURI,String[] urls){
        for (String url: urls) {
            if(PATH_MATCHER.match(url,requestURI)){
                return true;
            }
        }
        return false;
    }
}
