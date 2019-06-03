package com.example.demo.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// This is a file that sets up the applicaition to restrict access. By
// default, if access is not specified, it is denied. You have to
// specifically permit access to each page, directory or group of pages in
// your application. The class you create (SecurityConfiguration) extends
// the WebSecurityConfigurerAdapter, which has all of the methods needed to
// include security in your application.

// @Configuration and @EnableWebSecurity
// These annotations indicates to the compiler that the file is a
// configuration file and Spring Security is enabled for the application.
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public static BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private SSUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new SSUserDetailsService(userRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .authorizeRequests()
                .antMatchers("/","/h2-console/**","/register","/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll() // Must be on it's own line
                .and()
                .logout()
                .logoutRequestMatcher(
                        new AntPathRequestMatcher("/logout"))
                // The user is redirected to the login page after logout
                .logoutSuccessUrl("/login").permitAll().permitAll()
                .and()
                .httpBasic();
        http
                .csrf().disable(); // Only for the H2 console, NOT IN PRODUCTION
        http
                .headers().frameOptions().disable(); // Only for the H2 console, NOT IN PRODUCTION
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {

        // Manually creates Spring Security user
        /*
        auth.inMemoryAuthentication().withUser("dave")
                .password(encoder().encode("password")).authorities("ADMIN");
        */

        // Allows database authentication
        auth.userDetailsService(userDetailsServiceBean())
                .passwordEncoder(encoder());
    }

}