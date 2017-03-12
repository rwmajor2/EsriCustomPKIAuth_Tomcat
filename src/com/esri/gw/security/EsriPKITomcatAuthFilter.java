package com.esri.gw.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import java.security.cert.X509Certificate;

import java.security.cert.CertificateParsingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EsriPKITomcatAuthFilter implements Filter{
	
	// Setup logger which logs to Tomcat default log files...
	private final Logger log = Logger.getLogger("EsriPKITomcatAuthFilter");
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;

		X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
		if (null != certs && certs.length > 0) {
			// If a certificate exists for this request, then pass it on through with
			//   a wrapper defined below that pulls out the email for the username in getRemoteUser()
			chain.doFilter(new EsriPKITomcatAuthFilterWrapper(httpReq), httpRes);
		} else {
			// If no certificate is present, throw a 403 HTTP error.
			log.log(Level.SEVERE, "**** No PKI Certificate found in request ****");
			throw new HTTPException(403);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	public class EsriPKITomcatAuthFilterWrapper extends HttpServletRequestWrapper {

		// Setup logger which logs to Tomcat default log files...
		private final Logger log = Logger.getLogger("EsriPKITomcatAuthFilterWrapper");
		
		public EsriPKITomcatAuthFilterWrapper(HttpServletRequest request) {
			super(request);
		}
		
		@Override
		public String getRemoteUser() {
			
			// This method is what the Web Adaptor calls to determine if Web Tier Authentication has occurred.
			//  If it returns a value, then Portal will do Single Sign On if enableAutomaticAccountCreation is set to true.
			//  This must return a value for this to occur.  If a null value is return, a user will be passed
			//  through and no SSO will occur.
			
			HttpServletRequest req = (HttpServletRequest) super.getRequest();
		        		  
			X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
			X509Certificate userPKI = certs[0];
			
			//  This will do simple logging of the DN to Tomcat logs, for logging/auditing purposes.
			String dn = userPKI.getSubjectDN().getName();
			log.log(Level.INFO, "**** DN name is:  " + dn);
			try {
				// Call function below to get the Subject Alternative Name.
				//  The potential exists for this method to return a null.  One may want to consider that
				//  if no SANs can be extracted, then return a 403 error instead of a null, which passes the 
				//  request on through to Portal.
				String sans = getDNSSubjectAlts(userPKI);
				return sans;
			} catch (CertificateParsingException e) {
				// If an error occurs in method getDNSSubjectAlts, then log it and return null
				log.log(Level.SEVERE, "**** CertificateParsingException ****");
				return null;
			}
		}
		
		private String getDNSSubjectAlts(X509Certificate cert) throws CertificateParsingException {
		    
			// Given a user X509Certificate PKI certificate, attempt to get the Subject
			//   Alternative names on the certificate.
		    Collection c = cert.getSubjectAlternativeNames();
		    if (c != null) {
		        Iterator it = c.iterator();
		        while (it.hasNext()) {
		            List list = (List) it.next();
		            int type = ((Integer) list.get(0)).intValue();
		            log.log(Level.INFO, "**** SAN type is :  " + type);
		            log.log(Level.INFO, "**** SAN name is :  " + list.get(1).toString());
		            
		            // if type == 0, then this returns the OtherName of the SAN list
		            /*
		            if (type == 0)
		            {
		            	try
		            	{
		            		byte[] bytes = (byte[]) list.get(1);
		            		log.log(Level.INFO, "**** SAN name is :  " + new String(bytes));
			            	
		            	} catch (Exception e) {
		    				log.log(Level.SEVERE, "**** ERROR GETTING Subject Alternative Name ****");
		    				return null;
		    			}
		            }
		            */
		            
		            // if type == 1, then this returns the RFC822 of the SAN list
		            if (type == 1)
		            {
		            	try
		            	{
			            	String s = (String) list.get(1);
			            	return s;
		            	} catch (Exception e) {
		    				log.log(Level.SEVERE, "**** ERROR GETTING Subject Alternative Name ****");
		    				return null;
		    			}
		            }
		        }
		    }
		    
		    // Return a null if no SANs exist on the certificate.
		    return null;
		}
	}
}
