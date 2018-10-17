package uob_todo;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;
import java.util.Arrays;

@Configuration
@Profile("secured")
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/api/user", "/login**", "/static/**", "/error**")
                    .permitAll()
                    .and()
                .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                .logout()
                    .logoutUrl("/logout")
                    .and()
                .csrf()
                    .and()
                .addFilterBefore(customOAuthOAuthFilter(), BasicAuthenticationFilter.class)
        ;
    }

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Value("${security.oauth2.httpProxy.host}")
    private String proxyHost;

    @Value("${security.oauth2.httpProxy.port}")
    private Integer proxyPort;

    @Bean
    @ConfigurationProperties("security.oauth2.client")
    public AuthorizationCodeResourceDetails customOAuth() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("security.oauth2.resource")
    public ResourceServerProperties customOAuthResource() {
        return new ResourceServerProperties();
    }

    private Filter customOAuthOAuthFilter() {
        OAuth2RestTemplate customOAuthTemplate = new OAuth2RestTemplate(customOAuth(), oauth2ClientContext);

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (!this.proxyHost.equals("")) {
            httpClientBuilder.setProxy(new HttpHost(this.proxyHost, this.proxyPort));
        }
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClientBuilder.build());

        ClientCredentialsAccessTokenProvider clientCredentialsAccessTokenProvider = new ClientCredentialsAccessTokenProvider();
        clientCredentialsAccessTokenProvider.setRequestFactory(requestFactory);

        AuthorizationCodeAccessTokenProvider authorizationCodeAccessTokenProvider = new AuthorizationCodeAccessTokenProvider();
        authorizationCodeAccessTokenProvider.setRequestFactory(requestFactory);

        ImplicitAccessTokenProvider implicitAccessTokenProvider = new ImplicitAccessTokenProvider();
        implicitAccessTokenProvider.setRequestFactory(requestFactory);

        AccessTokenProvider accessTokenProvider = new AccessTokenProviderChain(Arrays.<AccessTokenProvider> asList(
            authorizationCodeAccessTokenProvider, implicitAccessTokenProvider, clientCredentialsAccessTokenProvider
        ));
        customOAuthTemplate.setAccessTokenProvider(accessTokenProvider);
        customOAuthTemplate.setRequestFactory(requestFactory);

        OAuth2ClientAuthenticationProcessingFilter customOAuthFilter = new OAuth2ClientAuthenticationProcessingFilter("/login");
        customOAuthFilter.setRestTemplate(customOAuthTemplate);

        UserInfoTokenServices tokenServices = new UserInfoTokenServices(customOAuthResource().getUserInfoUri(), customOAuth().getClientId());
        tokenServices.setRestTemplate(customOAuthTemplate);
        customOAuthFilter.setTokenServices(tokenServices);

        return customOAuthFilter;
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }
}
