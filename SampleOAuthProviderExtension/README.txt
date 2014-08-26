How to deploy:
--------------

- create "sampleoauth" directory under /opt/zimbra/lib/ext; copy "oauth-1.4.jar" and "sampleoauthprov.jar" under it

- copy "authorize.jsp" file to /opt/zimbra/jetty/webapps/zimbra/public directory

- run "zmlocalconfig -e zimbra_auth_provider=zimbra,sampleoauth"

- configure Zimbra memcached client:
      zmprov mcf zimbraMemcachedClientServerList <memcached_server_host>:11211

- zmmailboxdctl restart


For Cosumer Apps:
-----------------

- to register a consumer app run "zmprov mcf +zimbraOAuthConsumerCredentials <consumer_key>:<consumer_secret>:<consumer_description>"

- make your consumer app access
  <zimbra_base_url>/service/extension/sampleoauth/req_token, for request token,
  <zimbra_base_url>/service/extension/sampleoauth/authorization, for authorization
  <zimbra_base_url>/service/extension/sampleoauth/access_token, for access token.
  
- make sure to add X-Zimbra-Orig-Url header to signed oAuth requests. 

An example consumer app can be found in this class: com.zimbra.test.oAuthConsumerTestServlet.java

Using sample consumer app:
1) Download and install Apache Tomcat
2) Get source code for ZimbraCommon and SampleOAuthProviderExtension projects
3) Edit SampleOAuthProviderExtension/src/test/web.xml and change values for the following parameters:

   CONSUMER_KEY - set to the same consumer key you configured in the provider       
   CONSUMER_SECRET- set to the same consumer secret you configured in the provider
   REQUEST_TOKEN_ENDPOINT_URL - set to <zimbra_base_url>/service/extension/sampleoauth/req_token
   ACCESS_TOKEN_ENDPOINT_URL - set to <zimbra_base_url>/service/extension/sampleoauth/access_token
   AUTHORIZE_WEBSITE_URL - set to <zimbra_base_url>/service/extension/sampleoauth/authorization
   CALLBACK_URL - set to <your tomcat's instance URL>/oauthConsumer/oauthtest       
       
4) execute ant task "test-war" using SampleOAuthProviderExtension/build.xml ant script. This will produce a war file oauthConsumer.war
5) copy the war file produced by "test-war" to Tomcat's webapps folder and wait couple seconds for Tomcat to instantiate new web app
6) Open <your tomcat's instance URL>/oauthConsumer/oauthtest in your browser and replace "http://ubuntu.local:7070/home/user1/inbox.rss" with the REST URL you want to test, e.g.:
<zimbra_base_url>/home/user1/inbox.rss
7) Click "submit", you will be redirected to Zimbra login screen
8) Enter login information
9) you should see a dump of the REST resource and URL parameters on the returned page