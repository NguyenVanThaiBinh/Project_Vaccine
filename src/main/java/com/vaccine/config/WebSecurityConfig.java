package com.vaccine.config;


import com.vaccine.service.admindestination.AdminDestinationService;
import com.vaccine.service.admindestination.IAdminDestinationService;
import com.vaccine.service.user.UserDetailsServiceImpl;
import com.vaccine.service.warehouseVaccine.IWarehouseVaccineService;
import com.vaccine.service.warehouseVaccine.WarehouseVaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;


@Configuration
@EnableWebSecurity

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;



    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
    @Bean
    public IAdminDestinationService adminDestinationService(){
        return new AdminDestinationService();
    }

    @Bean
    public IWarehouseVaccineService warehouseVaccineService(){
        return new WarehouseVaccineService();
    }



    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        // S??t ?????t d???ch v??? ????? t??m ki???m User trong Database.
        // V?? s??t ?????t PasswordEncoder.
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // C??c trang kh??ng y??u c???u login
        http.authorizeRequests().antMatchers( "/","/login","/create", "/form", "/home", "/logout").permitAll();

        // Trang /userInfo y??u c???u ph???i login v???i vai tr?? ROLE_USER ho???c ROLE_ADMIN.
        // N???u ch??a login, n?? s??? redirect t???i trang /login.
        http.authorizeRequests().antMatchers("/user").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");
//          Quy???n b??c s??
//        http.authorizeRequests().antMatchers("/doctor/**").access("hasAnyRole('ROLE_DOCTOR', 'ROLE_ADMIN')");

        // Trang ch??? d??nh cho ADMIN
//        http.authorizeRequests().antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')");

        // Khi ng?????i d??ng ???? login, v???i vai tr?? XX.
        // Nh??ng truy c???p v??o trang y??u c???u vai tr?? YY,
        // Ngo???i l??? AccessDeniedException s??? n??m ra.
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");


        // C???u h??nh cho Login Form.
        http.authorizeRequests().and().formLogin()//
                // Submit URL c???a trang login
                .loginProcessingUrl("/j_spring_security_check") // Submit URL
                .loginPage("/")//
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=huhu")//
                .usernameParameter("CMND")//
                .passwordParameter("password")
                // C???u h??nh cho Logout Page.
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/")
//                 Session het' han~
                .and().sessionManagement().invalidSessionUrl("/");

        // C???u h??nh Remember Me.
        http.authorizeRequests().and() //
                .rememberMe().tokenRepository(this.persistentTokenRepository()) //
                .tokenValiditySeconds(60*9); //9 minute




    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        InMemoryTokenRepositoryImpl memory = new InMemoryTokenRepositoryImpl();
        return memory;
    }
    @Bean
    public SessionRegistry sessionRegistry(){
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }

}
