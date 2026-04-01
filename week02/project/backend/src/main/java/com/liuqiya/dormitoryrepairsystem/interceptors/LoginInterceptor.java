package com.liuqiya.dormitoryrepairsystem.interceptors;

import com.liuqiya.dormitoryrepairsystem.utils.JwtUtil;
import com.liuqiya.dormitoryrepairsystem.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.jta.JtaAfterCompletionSynchronization;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler)throws Exception{
        //令牌验证
        String token=request.getHeader("Authorization");
        // 处理 Bearer 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        //验证token
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            //将用户信息存储到ThreadLocal中
            ThreadLocalUtil.set(claims);
            //放行
            return true;
        }catch (Exception e){
            //http响应状态码为401
            response.setStatus(401);
            //不放行
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex)throws Exception{
      ThreadLocalUtil.remove();
    }
}
