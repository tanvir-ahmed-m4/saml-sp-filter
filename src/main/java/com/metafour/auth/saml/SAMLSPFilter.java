package com.metafour.auth.saml;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.common.binding.SAMLMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metafour.auth.saml.store.SAMLSessionManager;
import com.metafour.auth.saml.util.OpenSamlBootstrap;
import com.metafour.auth.saml.util.SAMLUtils;

public class SAMLSPFilter implements Filter {
	public static final String SAML_AUTHN_RESPONSE_PARAMETER_NAME = "SAMLResponse";
	private static Logger log = LoggerFactory.getLogger(SAMLSPFilter.class);
	private FilterConfig filterConfig;
	private SAMLResponseVerifier checkSAMLResponse;
	private SAMLRequestSender samlRequestSender;

	@Override
	public void init(javax.servlet.FilterConfig config) throws ServletException {
		OpenSamlBootstrap.init();
		filterConfig = new FilterConfig(config);
		checkSAMLResponse = new SAMLResponseVerifier();
		samlRequestSender = new SAMLRequestSender();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		/*
		 * Step 1: Ignore requests that are not addressed to the filter
		 */
		if (!isFilteredRequest(request)) {
			log.debug("According to {} configuration parameter request is ignored + {}",
					new Object[] { FilterConfig.EXCLUDED_URL_PATTERN_PARAMETER, request.getRequestURI() });
			chain.doFilter(servletRequest, servletResponse);
			return;
		}

		/*
		 * Step 2: If the answer comes from Shibboleth idP, we handle it
		 */
		log.debug("Attempt to secure resource  is intercepted : {}", ((HttpServletRequest) servletRequest).getRequestURL().toString());
		/*
		 * Check if response message is received from identity provider; In case
		 * of successful response system redirects user to relayState (initial)
		 * request
		 */
		String responseMessage = servletRequest.getParameter(SAML_AUTHN_RESPONSE_PARAMETER_NAME);
		if (responseMessage != null) {
			log.debug("Response from Identity Provider is received");
			try {
				log.debug("Decoding of SAML message");
				SAMLMessageContext samlMessageContext = SAMLUtils.decodeSamlMessage((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
				log.debug("SAML message has been decoded successfully");
				samlMessageContext.setLocalEntityId(filterConfig.getSpProviderId());
				// XXX: Where is getInitialRequestedResource()?
//				String relayState = getInitialRequestedResource(samlMessageContext);
				String relayState = samlMessageContext.getRelayState();
				checkSAMLResponse.verify(samlMessageContext);
				log.debug("Starting and store SAML session..");
				SAMLSessionManager.getInstance().createSAMLSession(request.getSession(), samlMessageContext);
				log.debug("User has been successfully authenticated in idP. Redirect to initial requested resource {}", relayState);
				response.sendRedirect(relayState);
				return;
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
		/*
		 * Step 3: If you receive a request for logout, delete the local session
		 */
		if (getCorrectURL(request).equals(filterConfig.getLogoutUrl())) {
			log.debug("Logout action: destroying SAML session.");
			SAMLSessionManager.getInstance().destroySAMLSession(request.getSession());
			chain.doFilter(request, response);
			return;
		}
		/*
		 * Step 4: If the user has already been authenticated, then we can grant access to a resource
		 */
		if (SAMLSessionManager.getInstance().isSAMLSessionValid(request.getSession())) {
		     log.debug("SAML session exists and valid: grant access to secure resource");
		     chain.doFilter(request, response);
		     return;
		}
		/*
		 * Step 5: Create a SAML запрос на аутентификацию and send the user to Shibboleth idP
		 */
		log.debug("Sending authentication request to idP");
		try {
			samlRequestSender.sendSAMLAuthRequest(request, response, filterConfig.getSpProviderId(), filterConfig.getAcsUrl(), filterConfig.getIdpSSOUrl());
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	// We add method to the filter class that will check if the request needs to be handled
	private boolean isFilteredRequest(HttpServletRequest request) {
		return !(filterConfig.getExcludedUrlPattern() != null && getCorrectURL(request).matches(filterConfig.getExcludedUrlPattern()));
	}

	// Also add the auxiliary method for receiving the correct URL
	private String getCorrectURL(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String requestUri = request.getRequestURI();
		int contextBeg = requestUri.indexOf(contextPath);
		int contextEnd = contextBeg + contextPath.length();
		String slash = "/";
		String url = (contextBeg < 0 || contextEnd == (requestUri.length() - 1)) ? requestUri : requestUri.substring(contextEnd);
		if (!url.startsWith(slash)) {
			url = slash + url;
		}
		return url;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}