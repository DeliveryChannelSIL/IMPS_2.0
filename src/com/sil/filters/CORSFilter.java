package com.sil.filters;

import java.io.IOException;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Singleton
@Provider
public class CORSFilter implements ContainerResponseFilter{

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

		final int ACCESS_CONTROL_MAX_AGE_IN_SECONDS 	= 12 * 60 * 60;
		final String ACCESS_CONTROL_ALLOW_ORIGIN 		= "Access-Control-Allow-Origin";
		final String ACCESS_CONTROL_ALLOW_HEADERS		= "Access-Control-Allow-Headers";
		final String ACCESS_CONTROL_ALLOW_METHODS		= "Access-Control-Allow-Methods";
		final String ACCESS_CONTROL_ALLOW_CREDENTIALS 	= "Access-Control-Allow-Credentials";
		final String ACCESS_CONTROL_MAX_AGE				= "Access-Control-Max-Age";
		
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.add(ACCESS_CONTROL_ALLOW_HEADERS, "origin, content-type, accept, authorization");
        headers.add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        headers.add(ACCESS_CONTROL_MAX_AGE, ACCESS_CONTROL_MAX_AGE_IN_SECONDS);
        
	}

}
