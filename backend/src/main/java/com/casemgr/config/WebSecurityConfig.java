package com.casemgr.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.casemgr.security.CmAuthenticationProvider;
import com.casemgr.security.JWTAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

//	@Autowired
//    private PasswordEncoder passwordEncoder;

//	private AuthenticationManager authenticationManager;
	
//	@Autowired
//	private UserServiceImpl userService;
//	private JWTService jwtService;

//	private JWTAuthenticationFilter jwtAuthenticationFilter;

//	@Autowired
//    private AuthService authService;

//	@Autowired
//    private MpUnauthorizedHandler unauthorizedHandler;
//	@Autowired
//    private MpAccessDeniedHandler accessDeniedHandler;
	
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	@Bean
	public JWTAuthenticationFilter jwtAuthenticationTokenFilter() {
		return new JWTAuthenticationFilter();
	}
	
	@Bean
	public CmAuthenticationProvider jwtAuthenticationProvider() {
		return new CmAuthenticationProvider();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

//	@Bean
//    WebSecurityCustomizer webSecurityCustomizer() {
//        // 单独处理一下 h2-console 如果放在 WHITE_LIST_URLS 中将不起作用 原因是上面的过滤走的是 MvcRequestMatcher 匹配器
//        // 而 h2-console 不属于 DispatcherServlet 的一部分
//        // reference: https://stackoverflow.com/questions/75367159/how-can-i-access-the-h2-console-while-using-spring-security-spring-boot-3-0-2/75367690
//        return web -> web.ignoring()
//                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
//    }
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 設置允許的來源
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // 設置允許的 HTTP 方法
        configuration.setAllowedHeaders(List.of("*")); // 設置允許的頭信息
        configuration.setAllowCredentials(true); // 設置是否允許憑證（如 Cookie）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 應用於所有路徑
        return source;
    }
	
	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
//        		.cors(withDefaults())
        		.cors(cors -> cors.configurationSource(corsConfigurationSource())) // 啟用 CORS
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                		.requestMatchers("/api/admin/**").hasAnyRole("ADMIN")
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/v3/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("/chat.html").permitAll()
                        .requestMatchers("/css/*").permitAll()
                        .requestMatchers("/img/*").permitAll()
                        .requestMatchers("/js/*").permitAll()
                        .requestMatchers("/messages/**").permitAll()
                        .requestMatchers("/chat/**").permitAll()
                        .requestMatchers("/app/**").permitAll()
                        .requestMatchers("/queue/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
//                        .requestMatchers("/api/files/**").permitAll()
//                        .requestMatchers("/api/toppicks/**").permitAll()
//                        .requestMatchers("/api/industries/**").permitAll()
//                        .requestMatchers("/api/systemlists/**").permitAll()
//                        .requestMatchers("/api/industries/**").permitAll()

//                        .requestMatchers(HttpMethod.PUT, "/api/auth/**").permitAll()
//                        .requestMatchers("/api/**").authenticated()
//                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
//                        .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
//                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
//                        .requestMatchers("/api/**").hasAnyRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN")
        				.anyRequest().authenticated());

//                .formLogin(login -> login
//                        .loginPage("/login"))
                
		// 禁用缓存
		httpSecurity.headers().cacheControl();
		httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//		httpSecurity.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
//		httpSecurity.exceptionHandling().accessDeniedHandler(accessDeniedHandler);
		return httpSecurity.build();
	}
}
