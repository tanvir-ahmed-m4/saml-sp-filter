Step 1:
-------
Add hosts file entries:
127.0.0.1       sp.local.ru
127.0.0.1       idp.local.ru

Step 2:
-------
Checkout http://cvs.m4.net/webtools/cvs/saml-sp-filter/?root=sandbox
Right-click on saml-sp-filter
  > Run As > maven build... > Goals: clean install
  
Step 3:
-------
Checkout http://cvs.m4.net/webtools/cvs/samlapp1/?root=sandbox
Right-click on samlapp1
  > Run As > maven build... > Goals: clean tomcat7:run

Step 4:
-------
Browse To: http://sp.local.ru:8080/samlapp1/
  > It will show Hello World!
Browse To: http://sp.local.ru:8080/samlapp1/pages/private/page.jsp
  > Start SAML authentication using idp.ssocircle.com Identity Provider
  > For testing with SSO Circle use:
    >> Username: saml-federation
    >> Password: appspot.com

Step 5:
-------
File > Import... > Git > Repositories from GitHub
  > Enter: spring-security-saml > Click: [search]
  > Choose: spring-projects/spring-security-saml
Window > Show View... > Other... > Git > Git Repositories
  > Right Click > Import Projects... > Import as general project
    >> sample > Next > Project name: spring-security-saml2-sample
Right-click on spring-security-saml2-sample
  > Configure > Conver to Maven project
Right-click on spring-security-saml2-sample
  > Run As > maven build... > Goals: clean tomcat7:run

Referfences:
------------
http://docs.spring.io/spring-security-saml/docs/1.0.x/reference/html/chapter-quick-start.htm
http://docs.spring.io/spring-security-saml/docs/1.0.x/reference/html/chapter-sample-app.html
https://saml-federation.appspot.com/saml/discovery?returnIDParam=idp&entityID=saml-federation.appspot.com

https://idp.ssocircle.com/sso/hos/ManageSPMetadata.jsp

https://github.com/spring-projects/spring-security-saml

http://www.codeproject.com/Articles/746569/Sharing-Experience-of-SSO-Integration-via-SAML-Res?display=Print
http://www.eastbanctech.com/sharing-experience-of-sso-integration-via-saml-2-0-resources-2/
https://wiki.shibboleth.net/confluence/display/SHIB2/SLOIssues

http://grepcode.com/file/repository.jboss.org/nexus/content/repositories/releases/org.picketlink/picketlink-web/1.0.2/org/picketlink/identity/federation/web/filters/SPFilter.java#SPFilter.0serviceURL

https://developers.google.com/google-apps/sso/saml_reference_implementation_web
http://docs.forgerock.org/en/openam/10.0.0/admin-guide/index/chap-federation.html
http://www.javaroots.com/2013/05/configure-cas-server-and-client-in-java.html
http://stackoverflow.com/questions/14950023/how-to-implement-sso-on-existing-tomcat-web-application
