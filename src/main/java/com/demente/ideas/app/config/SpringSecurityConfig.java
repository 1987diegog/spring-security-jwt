package com.demente.ideas.app.config;

import com.demente.ideas.app.auth.filter.JWTAuthenticationFilter;
import com.demente.ideas.app.auth.filter.JWTAuthorizationFilter;
import com.demente.ideas.app.auth.service.JWTService;
import com.demente.ideas.app.models.services.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// habilitamos la posibilidad de dar autorizacion a recursos por medio de @Secured
@EnableGlobalMethodSecurity(securedEnabled = true)
// Similar a @Secured tenemos el @PreAuthorize, la habilitacion de esta anotacion es con prePostEnabled
// @EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JpaUserDetailsService jpaUserDetailsService;

    @Autowired
    private JWTService jwtService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtService))
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


//
//        // H2-console config
//        http.authorizeRequests().antMatchers("/h2-console/**").hasAnyRole("ADMIN");
//        // Se deshabilita la proteccion csrf ya que utilizaremos JWT y no el token de csrf
//        // generado en los formularios de spring. Tambien se indica que se trabajara con Stateless
//        http.csrf().disable().sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.headers().frameOptions().disable();
//
//        // spring ejecutrara un filtro (un interceptor), antes de cargar cualquier ruta de nuestro controlador
//        // va a controlar (autorizacion) si el usuario tiene permisos para permitirle continuar, sino,
//        // negara el acceso y redirigira a la pagina de Login.
//        http.authorizeRequests()
//                .antMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
//                .anyRequest()
//                .authenticated()
//                .and().addFilter(new JWTAuthenticationFilter(authenticationManager()));
//
//                /* No se utilizara la seguridad por session, por lo tanto no necesitamos la redireccion
//                   a login o el logout ya que se utilizara JWT y necesitamos un codigo de error en el
//                   response y no que devuelva una pagina HTML como el login si no se puede acceder a
//                   un recurso o no se esta autentificado. Los manejos de errores tambien seran distinos.
//
//                .and()
//                .formLogin()
//                .successHandler(successHandler)
//                .loginPage("/login")
//                .permitAll()
//                .and()
//                .logout().permitAll()
//                .and().exceptionHandling().accessDeniedPage("/error_403_forbidden");*/

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {

        // se configura la forma de acceder a los usuarios (userByUsername) y el encoder de
        // la password de los mismos.
        builder.userDetailsService(jpaUserDetailsService)
                .passwordEncoder(passwordEncoder);

        /**
         *
         * IN MEMORY AUTHENTICATION:

         PasswordEncoder encoder = this.passwordEncoder;
         User.UserBuilder users = User.builder().passwordEncoder(encoder::encode);

         builder.inMemoryAuthentication()
         .withUser(users.username("admin").password("admin").roles("ADMIN", "USER"))
         .withUser(users.username("diegog09").password("123456").roles("USER"));

         */
    }
}
