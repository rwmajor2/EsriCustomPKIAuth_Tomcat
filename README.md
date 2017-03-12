# EsriCustomPKIAuth_Tomcat
Custom Web Tier Authentication for PKI using Tomcat and Esri Web Adaptor

```
Set up a new Eclipse Dynamic Web Project.
Add servlet-api.jar to the Eclipse project from your Tomcat lib folder as an External JAR reference.
Add bouncycastle JAR to Eclipse project found in this repo; can be downloaded from internet also.
Implement a new Package called com.esri.gw.security.
Copy/paste source code into a new class.
Compile source code to a JAR file.
Deploy the JAR file to /webapps/portal/WEB-INF/lib folder.
    *** Must also deploy bouncycastle JAR file to this same folder.
```

In the Source code, you will need to make a decision on which Subject Alternative name to extract.  Type 0 will come from the "OtherName" SAN and Type 1 will come from the RFC822 specification name.  Alter the code accordingly for your purposes, i.e. comment out a section in favor of the other.

![pki SAN](https://github.com/rwmajor2/EsriCustomPKIAuth_Tomcat/blob/master/pkicert.png)

Near the beginning of the Web Adaptor's web.xml, add the following after the line `<display-name>`:
```xml
   ...
	<display-name>ArcGIS Web Adaptor</display-name>
	<filter>
		<filter-name>EsriPKIAuthFilter</filter-name>
		<filter-class>com.esri.gw.security.EsriPKITomcatAuthFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>EsriPKIAuthFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	...
```

Add the following to the Web Adaptor's web.xml file right below the closing `</web-app>` tag.

```xml
    <security-constraint>
        <web-resource-collection>
            <web-resource-name>ArcGIS Web Adapter</web-resource-name>
            <url-pattern>/*</url-pattern>
        </web-resource-collection>
        <user-data-constraint>
            <transport-guarantee>CONFIDENTIAL</transport-guarantee>
        </user-data-constraint>
    </security-constraint>
    <login-config>
        <auth-method>CLIENT-CERT</auth-method>
        <realm-name>ArcGIS Web Adapter</realm-name>
    </login-config>
```

This assumes that Tomcat is your web server, running on ports 80 and 443.  In addition to this Filter, you need to modify your Tomcat's server.xml to ensure SSL/PKI authentication is enabled.

```xml
    <Connector port="443" protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
               clientAuth="true" sslProtocol="TLS"
	       keystoreFile="conf/keystore.jks" keystorePass="changeit"
	       truststoreFile="conf/cacerts.jks" truststorePass="changeit" />
```
