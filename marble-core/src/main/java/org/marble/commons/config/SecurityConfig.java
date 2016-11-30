package org.marble.commons.config;

import org.marble.commons.config.filter.CsrfHeaderFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * This section defines the user accounts which can be used for
     * authentication as well as the roles each user has.
     */

    @Value("${access.guest.username}")
    private String accessGuestUsername;
    @Value("${access.guest.password}")
    private String accessGuestPassword;
    @Value("${access.oper.username}")
    private String accessOperUsername;
    @Value("${access.oper.password}")
    private String accessOperPassword;
    @Value("${access.admin.username}")
    private String accessAdminUsername;
    @Value("${access.admin.password}")
    private String accessAdminPassword;

    static private String ROLE_ADMIN = "ADMIN";
    static private String ROLE_OPER = "OPER";
    static private String ROLE_GUEST = "GUEST";

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser(accessGuestUsername).password(accessGuestPassword).roles(ROLE_GUEST).and()
                .withUser(accessOperUsername).password(accessOperPassword).roles(ROLE_GUEST, ROLE_OPER).and()
                .withUser(accessAdminUsername).password(accessAdminPassword).roles(ROLE_GUEST, ROLE_OPER, ROLE_ADMIN);
    }

    /**
     * This section defines the security policy for the app. - BASIC
     * authentication is supported (enough for this REST-based demo) -
     * /employees is secured using URL security shown below - CSRF headers are
     * disabled since we are only testing the REST interface, not a web one.
     *
     * NOTE: GET is not shown which defaults to permitted.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic().and().authorizeRequests()
                // Particular Permissions
                .antMatchers("/api/generalProperties/**",
                        "/api/twitterApiKeys/**",
                        "/api/corpus/**",
                        "/api/profile/**")
                .hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.GET, "/api/topics/**",
                        "/api/jobs/**",
                        "/api/charts/**",
                        "/api/posts/**",
                        "/api/processedPosts/**")
                .hasRole(ROLE_GUEST)
                .antMatchers("/api/topics/**",
                        "/api/jobs/**",
                        "/api/charts/**",
                        "/api/posts/**",
                        "/api/processedPosts/**")
                .hasRole(ROLE_OPER)
                // General Permissions
                .antMatchers(HttpMethod.POST, "/**").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.PUT, "/**").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.PATCH, "/**").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.DELETE, "/**").hasRole(ROLE_ADMIN)
                .and()
                .rememberMe().and().logout().logoutUrl("/logout").logoutSuccessUrl("/")
                .and()
                .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                .csrf().disable();
        // .csrf().csrfTokenRepository(csrfTokenRepository());
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

}