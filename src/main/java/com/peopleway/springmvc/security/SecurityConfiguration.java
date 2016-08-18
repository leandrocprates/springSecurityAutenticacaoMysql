package com.peopleway.springmvc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static String REALM="MY_TEST_REALM";
	
	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		//auth.inMemoryAuthentication().withUser("bill").password("abc123").roles("ADMIN");
		//auth.inMemoryAuthentication().withUser("tom").password("abc123").roles("USER");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
 
	//  http.csrf().disable()
	//  	.authorizeRequests()
	//  	.antMatchers("/user/**").hasRole("ADMIN")
	//	.and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint());
 	
            http.authorizeRequests()
            .antMatchers("/user/**").access("hasRole('ROLE_ADMIN')")
            .and()
            .formLogin().permitAll()
            .and()        
            .httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint());
            
        }
        
	
	@Bean
	public CustomBasicAuthenticationEntryPoint getBasicAuthEntryPoint(){
		return new CustomBasicAuthenticationEntryPoint();
	}
	
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
        }
        
        /*Driver de conexao ao mysql */
        @Bean
        public DriverManagerDataSource dataSource() {
            DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
            driverManagerDataSource.setUrl("jdbc:mysql://192.168.20.52:3306/userbase");
            driverManagerDataSource.setUsername("root");
            driverManagerDataSource.setPassword("AAbb123@");
            return driverManagerDataSource;
        }
        
        /*Efetua a autenticacao no MYSQL*/
        @Autowired
        public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
            auth.jdbcAuthentication().dataSource(dataSource())
            .usersByUsernameQuery(
                "select username,password, enabled from users where username=?")
            .authoritiesByUsernameQuery(
                "select username, role from user_roles where username=?");
        } 
        
        
        
}
