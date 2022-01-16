package org.pegadaian.dev;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpClientConfigurer;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;

public class InsecureHTTP4Config extends RouteBuilder{
	
	@Override
	public void configure() throws Exception {
		HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
        TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
        X509ExtendedTrustManager extendedTrustManager = new InsecureX509TrustManager();
        trustManagersParameters.setTrustManager(extendedTrustManager);
 
        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setTrustManagers(trustManagersParameters);
        httpComponent.setSslContextParameters(sslContextParameters);
        httpComponent.setX509HostnameVerifier(new AllowAllHostnameVerifier());
        httpComponent.setHttpClientConfigurer(new DefaultHttpClientConfig());
	}
	
	/*	
	 * Fixes
	 * Avoid Circular Redirect Exception
	 * Avoid Content-Length header already present
	*/	
	private static class DefaultHttpClientConfig implements HttpClientConfigurer {
		@Override
		public void configureHttpClient(HttpClientBuilder clientBuilder) {
			clientBuilder.setDefaultRequestConfig(
					RequestConfig.custom()
						.setCircularRedirectsAllowed(true).build());
		}
	}
	 
    private static class InsecureX509TrustManager extends X509ExtendedTrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
 
        }
 
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
 
        }
 
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
 
        }
 
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
 
        }
 
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
 
        }
 
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
 
        }
 
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
