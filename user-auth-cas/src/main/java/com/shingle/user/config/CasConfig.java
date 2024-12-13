package com.shingle.user.config;


import com.shingle.user.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apereo.cas.client.Protocol;
import org.apereo.cas.client.authentication.AuthenticationFilter;
import org.apereo.cas.client.configuration.ConfigurationKeys;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.session.SingleSignOutHttpSessionListener;
import org.apereo.cas.client.util.AbstractCasFilter;
import org.apereo.cas.client.util.AssertionThreadLocalFilter;
import org.apereo.cas.client.util.HttpServletRequestWrapperFilter;
import org.apereo.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.io.IOException;
import java.security.Principal;
import java.util.*;


@Configuration
public class CasConfig {

    private static final Logger logger = LoggerFactory.getLogger(CasConfig.class);

    @Value("${cas.server-url-prefix}")
    private String casServerUrlPrefix;

    @Value("${cas.server-login-url}")
    private String casServerLoginUrl;

    @Value("${cas.client-host-url}")
    private String serverHostUrl;

//    @Bean
//    public FilterRegistrationBean<DelegatingFilterProxy> casFilterRegistration() {
//        FilterRegistrationBean<DelegatingFilterProxy> authnFilter = new FilterRegistrationBean<>();
//        DelegatingFilterProxy targetCasAuthnFilter = new DelegatingFilterProxy();
//        targetCasAuthnFilter.setTargetBeanName("casAuthenticationFilter");
//        authnFilter.setFilter(targetCasAuthnFilter);
//        final Map<String, String> initParameters = new HashMap<>(1);
//        initParameters.put(ConfigurationKeys.CAS_SERVER_URL_PREFIX.getName(), casServerUrlPrefix);
//        initParameters.put(ConfigurationKeys.CAS_SERVER_LOGIN_URL.getName(), casServerLoginUrl);
//        initParameters.put(ConfigurationKeys.SERVER_NAME.getName(), serverHostUrl);
//        authnFilter.setInitParameters(initParameters);
//        authnFilter.setOrder(1);
//        return authnFilter;
//    }

    @Bean
    public FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> casValidationFilter() {
        FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> validationFilter = new FilterRegistrationBean<>();
        Cas30ProxyReceivingTicketValidationFilter targetCasValidationFilter = new Cas30ProxyReceivingTicketValidationFilter();
            initFilter(validationFilter, targetCasValidationFilter, 1, constructInitParams(ConfigurationKeys.CAS_SERVER_URL_PREFIX.getName(), casServerUrlPrefix, serverHostUrl), Collections.emptyList());
        return validationFilter;
    }


//    @Bean
//    public AuthenticationFilter  casAuthenticationFilter() {
//        AuthenticationFilter casAuthenticationFilter = new AuthenticationFilter();
//        casAuthenticationFilter.setCasServerUrlPrefix(casServerUrlPrefix);
//        casAuthenticationFilter.setCasServerLoginUrl(casServerLoginUrl);
//        casAuthenticationFilter.setServerName(serverHostUrl);
//        return casAuthenticationFilter;
//    }

    @Bean
    public FilterRegistrationBean<AuthenticationFilter>  casAuthenticationFilter() {
        FilterRegistrationBean<AuthenticationFilter> authnFilter = new FilterRegistrationBean<>();

        AuthenticationFilter targetCasAuthnFilter = new AuthenticationFilter();
        initFilter(authnFilter, targetCasAuthnFilter, 2, constructInitParams(ConfigurationKeys.CAS_SERVER_LOGIN_URL.getName(), casServerLoginUrl, serverHostUrl), Collections.singletonList("/cas/login"));
        return authnFilter;
    }



    @Bean
    public FilterRegistrationBean<HttpServletRequestWrapperFilter> casHttpServletRequestWrapperFilter() {
        FilterRegistrationBean<HttpServletRequestWrapperFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new HttpServletRequestWrapperFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(3);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AssertionThreadLocalFilter>  casAssertionThreadLocalFilter() {
        FilterRegistrationBean<AssertionThreadLocalFilter> assertionTLFilter = new FilterRegistrationBean<>();
        assertionTLFilter.setFilter(new AssertionThreadLocalFilter());
        assertionTLFilter.setOrder(4);
        return assertionTLFilter;
    }

    @Bean
    public FilterRegistrationBean<CasAssertionFilter> casAssertionFilter() {
        FilterRegistrationBean<CasAssertionFilter> registration = new FilterRegistrationBean<>();
        CasAssertionFilter filter = new CasAssertionFilter(Protocol.CAS3);
       initFilter(registration, filter, 5, constructInitParams(ConfigurationKeys.CAS_SERVER_URL_PREFIX.getName(), casServerUrlPrefix, serverHostUrl), Collections.emptyList());
        return registration;
    }


    @Bean
    public FilterRegistrationBean<SingleSignOutFilter> casSingleSignOutFilter() {
        FilterRegistrationBean<SingleSignOutFilter> singleSignOutFilter = new FilterRegistrationBean<>();
        singleSignOutFilter.setFilter(new SingleSignOutFilter());
        final Map<String, String> initParameters = new HashMap<>(1);
        initParameters.put(ConfigurationKeys.CAS_SERVER_URL_PREFIX.getName(), casServerUrlPrefix);
        singleSignOutFilter.setInitParameters(initParameters);
        singleSignOutFilter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return singleSignOutFilter;
    }

    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> casSingleSignOutListener() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutListener = new ServletListenerRegistrationBean<>();
        singleSignOutListener.setListener(new SingleSignOutHttpSessionListener());
        singleSignOutListener.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return singleSignOutListener;
    }


    private static Map<String, String> constructInitParams(final String casUrlParamName, final String casUrlParamVal, final String clientHostUrlVal) {
        final Map<String, String> initParams = new HashMap<>(2);
        initParams.put(casUrlParamName, casUrlParamVal);
        initParams.put("serverName", clientHostUrlVal);
        return initParams;
    }

    private static <T extends Filter> void initFilter(final FilterRegistrationBean<T> filterRegistrationBean, final T targetFilter, final int filterOrder, final Map<String, String> initParams, final Collection<String> urlPatterns) {

        if (filterRegistrationBean == null || targetFilter == null) {
            logger.error("filterRegistrationBean or targetFilter is null");
            throw new IllegalArgumentException("filterRegistrationBean and targetFilter cannot be null");
        }

        filterRegistrationBean.setFilter(targetFilter);
        filterRegistrationBean.setOrder(filterOrder);

        if (initParams != null) {
            filterRegistrationBean.setInitParameters(initParams);
        } else {
            logger.warn("initParams is null, using default parameters");
        }

        if (urlPatterns != null && !urlPatterns.isEmpty()) {
            filterRegistrationBean.setUrlPatterns(urlPatterns);
        } else {
            logger.warn("urlPatterns is null or empty, no URL patterns set");
        }
    }

    public static class CasAssertionFilter extends AbstractCasFilter {


        protected CasAssertionFilter(Protocol protocol) {
            super(protocol);
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            final Principal principal = request.getUserPrincipal();
            if (principal != null) {
                final HttpSession session = request.getSession(true);
                final User user = new User(principal.getName(), "CAS_PASSWORD"); // 假设密码为CAS_PASSWORD
                session.setAttribute("user", user);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }


}

