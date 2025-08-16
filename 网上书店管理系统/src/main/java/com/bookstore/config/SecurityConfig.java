package com.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用不必要的安全功能
                .httpBasic().disable()  // 禁用HTTP Basic弹窗
                .formLogin().disable()   // 禁用表单登录
                .logout().disable()      // 禁用默认/logout端点
                .sessionManagement().disable() // 禁用会话管理（无状态API）

                // 授权配置
                .authorizeRequests()
                .antMatchers("/**").permitAll() // 开放所有端点
                .and()

                // 安全防护配置
                .cors().configurationSource(corsConfigurationSource()) // 启用CORS
                .and()
                .csrf().disable()       // 关闭CSRF
                .headers()
                .frameOptions().disable() // 允许嵌入iframe（如H2 Console）
                .xssProtection().block(false); // 可选：关闭XSS保护（根据前端需求）
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 更灵活的域名匹配
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("X-Custom-Header")); // 暴露自定义头
        configuration.setMaxAge(3600L); // 预检请求缓存时间
        configuration.setAllowCredentials(true); // 允许凭证（如cookies）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}