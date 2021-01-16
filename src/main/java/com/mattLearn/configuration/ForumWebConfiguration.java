package com.mattLearn.configuration;

import com.mattLearn.interceptor.LoginRequiredInterceptor;
import com.mattLearn.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Matt
 * @date 2021/1/16 18:21
 *
 * 用于将 写好的 interceptor 注册进项目中
 */

@Component
public class ForumWebConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 在系统初始化的时候添加我们的拦截器
        registry.addInterceptor(passportInterceptor);
        // Note: 第二个需要 进行登录跳转的 interceptor 必须放置在前一个 interceptor 之后
        // 因为其中用到的 hostHolder 是在第一个  interceptor 中进行的 初始化
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}
