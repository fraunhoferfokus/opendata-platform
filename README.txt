
General Information:
------------------------------------------------------------------
This is Version 1.0 of the Open Data platform of Fraunhofer FOKUS.

The Open Data platform has been developed in the course of the EU
Open Cities project (http://opencities.net, Grant agreement: 270896). 


We are glad to accept any type of constructive suggestions.

The standard configuration builds on a CKAN (Comprehensive Knowledge Archive Network) 
backend for storing the metadata and a Liferay based frontend. 
Liferay is a web application portal/server  
running on top of a corresponding application server, mostly Tomcat. 
It allows for developing portlet type of web applications. The software 
here constitutes the belonging portlets and plugins which were developed 
as Liferay extensions.  



How to proceed for Developers:
------------------------------------------------

Prerequisites
-----------------------

  * Install CKAN 1.7.1
  * Install OpenJDK
  * Download from  LifeRay.com
    <http://www.liferay.com/de/downloads/liferay-portal/available-releases>
	  o Liferay Portal 6 bundled with tomcat, unzip
      o Liferay 6 Pugins SDK, don't unzip 
  * download, unzip Eclipse JEE Indigo
      o liferay plugin sdk


Compilation and Deployment of a Development System:
----------------------------------------------------
  * Start CKAN on your local or on a dedicated machine
  * Checkout the portlets/plugins within the Eclipse based Liferay SDK
  * Build and deploy the portlets using the belonging ant scripts (build.xml)
	  o This step would require you to adapt 
	    the ant scripts for your Liferay SDK 
  * Build the middleware and caching libraries and copy them to the 
    oc-ext/docroot/WEB-INF/ext-lib/global
	  o This step would require that you first build the caching component, 
	    then provide it as a library to the middleware component, and finally 
	    compile the middleware component 
  * Adjust the oc-ext/docroot/WEB-INF/ext-impl/src/portal-ext.properties 
    file. The fields are well commented and exaplained within the file
  * Deploy oc-ext using the belonging build.xml
  * Start the Liferay Server from within your Eclipse environment.
  
  