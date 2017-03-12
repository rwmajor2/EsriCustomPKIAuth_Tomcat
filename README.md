# EsriCustomPKIAuth_Tomcat
Custom Web Tier Authentication for PKI using Tomcat and Esri Web Adaptor

This assumes that Tomcat is your web server, running on ports 80 and 443.  In addition to this Filter, you need to modify your Tomcat's server.xml to ensure SSL/PKI authentication is enabled.

`
    <Connector port="443" protocol="org.apache.coyote.http11.Http11NioProtocol"<br>
               maxThreads="150" SSLEnabled="true" scheme="https" secure="true"<br>
               clientAuth="true" sslProtocol="TLS" <br>
	       keystoreFile="conf/keystore.jks" keystorePass="changeit" <br>
	       truststoreFile="conf/cacerts.jks" truststorePass="changeit" /><br>
`
