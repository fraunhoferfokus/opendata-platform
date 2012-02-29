Contacts:

Nikolay Tcholtchev, nikolay.tcholtchev@fokus.fraunhofer.de
Evanela Lapi, evanela.lapi@fokus.fraunhofer.de
Florian Marienfeld, evanela.lapi@fokus.fraunhofer.de


This is an early version (0.51) of the Open Data platform of Fraunhofer FOKUS.
Please consider that the software will be much improved and refactored within
the coming versions.

The Open Data platform has been developed in the course of the EU
Open Cities project (http://opencities.net, Grant agreement: 270896). 


We are glad to accept any type of constructive suggestions.

The standard configuration builds on a CKAN backend for storing the metadata and
a Liferay based frontend. The software here constitutes the belonging portlets 
and plugins which were developed as Liferay extensions. 



Prerequisites
-----------------------------------------------

  * Install CKAN 1.5
  * Install OpenJDK
  * Download from  LifeRay.com
    <http://www.liferay.com/de/downloads/liferay-portal/available-releases>
      o Liferay Portal 6 bundled with tomcat, unzip
      o Liferay 6 Pugins SDK, don't unzip 
  * download, unzip Eclipse JEE Indigo
      o liferay plugin sdk
          + unzip into workspace eg. into workspace/sdk
          + adjust build.properties: create a separate properties file
            named "build.${user.name}.properties" with the properties to
            overwrite:

            #on the mac you might have to switch compilers:
                javac.compiler=modern
                #javac.compiler=org.eclipse.jdt.core.JDTCompilerAdapter
            ...
                #
                # Specify the paths to an unzipped Tomcat bundle.
                #
                app.server.type=tomcat
                app.server.dir=/<path-to>/liferay-portal-6.x.x/tomcat-7.xxx

  * Via marketplace install liferay IDE, subversion plugin
    (subclipse/subversive)
  * in eclipse-preference -> liferay-ides add ide choose workspace/sdk,
    tick "open in eclipse"
  * in servers-view add liferay 6.1 server

    cd workspace
    cd sdk/portlets
    svn co $SVNREPO/data_portal/oc-datasets-portlet/
    svn co $SVNREPO/data_portal/oc-manage-datasets-portlet/
    svn co $SVNREPO/data_portal/oc-persistent-caching-portlet/
    svn co $SVNREPO/data_portal/oc-search-by-category-portlet/
    svn co $SVNREPO/data_portal/org.opencities.berlin.opendata.cache.updater-portlet/
    svn co $SVNREPO/data_portal/org.opencities.berlin.opendata.middleware.adapter-portlet/
    svn co $SVNREPO/data_portal/org.opencities.berlin.opendata.portlet.spring.startpageboxes-portlet/
    svn co $SVNREPO/data_portal/org.opencities.berlin.opendata.portlet.spring.startpagesearch-portlet/
    svn co $SVNREPO/data_portal/org.opencities.berlin.opendata.uploaddata-portlet/
    cd ../themes
    svn co $SVNREPO/themes/oc-standard-theme
    cd ../hooks
    svn co $SVNREPO/data_portal/oc-hook
    cd ../ext
    svn co $SVNREPO/oc-ext
    cd ../.. # i.e. workspace dir
    svn co $SVNREPO/data_portal/org.opencities.berlin.opendata.caching/
    svn co $SVNREPO/data_portal/org.opencities.berlin.opendata.jsp.rssupdater/
    svn co $SVNREPO/data_portal/org.opencities.berlin.opendata.middleware/

  * import -> liferay project from plugins sdk: select sdk and target
    runtime server, tick all projects checked out with svn
  * drag each project build.xml into ant-view



