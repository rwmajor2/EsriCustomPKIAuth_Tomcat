# EsriCustomPKIAuth_Tomcat
Custom Web Tier Authentication for PKI using Tomcat and Esri Web Adaptor

This assumes that Tomcat is your web server, running on ports 80 and 443.  In addition to this Filter, you need to modify your Tomcat's server.xml to ensure SSL/PKI authentication is enabled.

<pre>
    <Connector port="443" protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
               clientAuth="true" sslProtocol="TLS"
	       keystoreFile="conf/keystore.jks" keystorePass="changeit"
	       truststoreFile="conf/cacerts.jks" truststorePass="changeit" />
</pre>
