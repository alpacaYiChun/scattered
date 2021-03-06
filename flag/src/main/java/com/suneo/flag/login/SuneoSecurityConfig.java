package com.suneo.flag.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SuneoSecurityConfig extends WebSecurityConfigurerAdapter{
    @Autowired
    private PasswordEncoder encoder;
    
    //@Autowired
    //private UserDetailsServiceImpl userDetailsService;
    
    //@Override
    //public void configure(AuthenticationManagerBuilder auth) throws Exception {
    	//auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    //}
    
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
        http.cors().and().csrf().disable();
    	/*
        http.authorizeRequests((authorize) -> authorize
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .anyRequest().permitAll()
        ).formLogin((formLogin) -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/auth/login")
                .permitAll()
        ).logout(LogoutConfigurer::permitAll
        ).rememberMe(rememberMe -> rememberMe
                .rememberMeCookieName("suneo-alpaca-cookie")
                .key("suneo-alpaca-key")
                .tokenValiditySeconds(3600 * 12));
          */      
    }
}
