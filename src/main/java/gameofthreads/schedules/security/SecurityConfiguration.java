package gameofthreads.schedules.security;

import gameofthreads.schedules.security.jwt.OAuth2EntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(config ->
                        config
                                .antMatchers("/api/auth/*").permitAll()
                                .antMatchers(HttpMethod.GET, "/api/schedules/").permitAll()
                                .antMatchers(HttpMethod.GET, "/api/schedules/{scheduleId}").permitAll()
                                .antMatchers("/api/schedules/*").hasAuthority("SCOPE_ADMIN")
                                .antMatchers("/api/subscription/add").hasAuthority("SCOPE_ADMIN")
                                .antMatchers("/api/subscription/addByLink").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer()
                .authenticationEntryPoint(new OAuth2EntryPoint())
                .jwt();
    }

}
