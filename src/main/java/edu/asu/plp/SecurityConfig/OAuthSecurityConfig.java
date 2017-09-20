package edu.asu.plp.SecurityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configurable
@EnableWebSecurity
public class OAuthSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
	@Autowired
	AuthorizationCodeResourceDetails authCodeResourceDetails;
	
	@Autowired
	ResourceServerProperties resourceServerProperties;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		super.configure(auth);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
	}
	
	private OAuth2ClientAuthenticationProcessingFilter filter() {
		OAuth2ClientAuthenticationProcessingFilter oAuth2Filter = new OAuth2ClientAuthenticationProcessingFilter("/google/login");
 
 
		OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(authCodeResourceDetails,
				oauth2ClientContext);
		oAuth2Filter.setRestTemplate(oAuth2RestTemplate);
 
 
		oAuth2Filter.setTokenServices(new UserInfoTokenServices(resourceServerProperties.getUserInfoUri(),
				resourceServerProperties.getClientId()));
 
 
		return oAuth2Filter;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
 
 
		http
		.authorizeRequests()
		.antMatchers("/index.html").permitAll()
		.anyRequest().fullyAuthenticated()//
		.and()
		.logout()
		.logoutSuccessUrl("/")
		.permitAll()
		.and()
		.addFilterAt(filter(), BasicAuthenticationFilter.class)
		.csrf()
		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		
		http.authorizeRequests().antMatchers("/CSS/**","/JS/**","/images/**").permitAll().anyRequest().permitAll();
	}
}
