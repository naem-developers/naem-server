package naem.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/user/signup").permitAll()
            .antMatchers("/user/login").permitAll()
            .antMatchers("/user/verify/**").permitAll()
            .antMatchers("/oauth/**").permitAll()
            .antMatchers("/test/user").hasRole("USER")
            .antMatchers("/test/admin").hasRole("ADMIN")
            .antMatchers("/v3/api-docs", "/swagger*/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic();

    }
}
