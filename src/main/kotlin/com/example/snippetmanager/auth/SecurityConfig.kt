package com.example.snippetmanager.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    @Value("\${auth0.audience}")
    val audience: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    val issuer: String,
) {
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000", "http://localhost:3001", "https://snippet-searcher.southafricanorth.cloudapp.azure.com/app", "https://snippet-searcher-prod.southafricanorth.cloudapp.azure.com/app")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("DNT","User-Agent","X-Requested-With","If-Modified-Since","Cache-Control","Content-Type","Range","Authorization")
        configuration.exposedHeaders = listOf("Content-Length","Content-Range")
        val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration)
        return urlBasedCorsConfigurationSource
    }

    @Bean
    fun securityWebFilterChain(serverSecurity: ServerHttpSecurity): SecurityWebFilterChain = serverSecurity
        .authorizeExchange {
            it
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/snippet/*").authenticated()
                .pathMatchers("/snippet").authenticated()
                .pathMatchers("/snippet/*/*").authenticated()
                .anyExchange().denyAll()
        }
        .oauth2ResourceServer { it.jwt(withDefaults()) }
        .cors(withDefaults())
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .build()

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)

        val jwtDecoder = ReactiveJwtDecoders.fromOidcIssuerLocation(issuer) as NimbusReactiveJwtDecoder
        jwtDecoder.setJwtValidator(withAudience)

        return jwtDecoder
    }
}