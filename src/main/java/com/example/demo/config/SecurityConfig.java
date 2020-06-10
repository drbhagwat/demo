package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
/*@EnableWebSecurity
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)*/
public class SecurityConfig extends WebSecurityConfigurerAdapter {
/*
  @Autowired
  private Environment env;
*/

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests(a -> a
            .antMatchers("/", "/css/**", "/images/**", "/logout", "/error", "/webjars/**").permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(e -> e
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        )
        .oauth2Login()
        .and()
        .logout(l -> l
            .logoutSuccessUrl("/").permitAll()
        );
    /*
        .loginPage("/login")
        .authorizationEndpoint()
        .baseUri("/oauth2/authorize")
        .authorizationRequestRepository(authorizationRequestRepository())
        .and()
        .redirectionEndpoint()
        .baseUri("http://localhost:8080/loginSuccess")
        .and()
        .tokenEndpoint()
        .accessTokenResponseClient(accessTokenResponseClient())
        .and()
        .defaultSuccessUrl("/loginSuccess", true)
        .failureUrl("/loginSuccess");
*/
//    http.csrf().disable();
  }

/*
  @Bean
  public PrincipalExtractor githubPrincipalExtractor() {
    return new GithubPrincipalExtractor();
  }

  @Bean
  public AuthoritiesExtractor githubAuthoritiesExtractor() {
    return new GithubAuthoritiesExtractor();
  }
*/
/*
  @Bean
  public AuthorizationRequestRepository<OAuth2AuthorizationRequest>
  authorizationRequestRepository() {
    return new HttpSessionOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
  accessTokenResponseClient() {
    DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
    return accessTokenResponseClient;
  }

  private static List<String> clients = Arrays.asList("google", "facebook");

  public ClientRegistrationRepository clientRegistrationRepository() {
    List<ClientRegistration> registrations = clients.stream()
        .map(c -> getRegistration(c))
        .filter(registration -> registration != null)
        .collect(Collectors.toList());

    return new InMemoryClientRegistrationRepository(registrations);
  }

  private static String CLIENT_PROPERTY_KEY
      = "spring.security.oauth2.client.registration.";

  private ClientRegistration getRegistration(String client) {
    String clientId = env.getProperty(
        CLIENT_PROPERTY_KEY + client + ".client-id");

    if (clientId == null) {
      return null;
    }

    String clientSecret = env.getProperty(
        CLIENT_PROPERTY_KEY + client + ":client-secret");

    if (client.equals("google")) {
      return CommonOAuth2Provider.GOOGLE.getBuilder(client)
          .clientId(clientId).clientSecret(clientSecret).build();
    }
    if (client.equals("facebook")) {
      return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
          .clientId(clientId).clientSecret(clientSecret).build();
    }
    return null;
  }
*/
}
