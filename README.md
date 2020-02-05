build and run locally stand alone

	1. go to base directory of branch, this will build the package with everything in it
	3. prompt# brew install maven                                          ==> install maven
	4. prompt# mvn package -Dmaven.test.skip=true                          ==> build and package
	5. prompt# java -jar target/dependency/jetty-runner.jar target/*.war   ==> should run (classpath issue common for corenlp..)
       prompt# mvn exec:java                                               ==> run as server
       prompt# mvn jetty:run                                               ==> run as reader

build distribution

	1. mvn clean
	2. mvn install
	2. upload xxx.zip to elastic beanstalk       

run distribution

	1) cd target
	2) unzip sedroChatbot-1.0.9999-bundle.zip 
	3) java -Xmx3G -jar jetty-runner.jar ./sedroChatbot-1.0.9999.war
	
Server pages

	localhost:8080                 ==> mangement page
	localhost:8080/msg             ==> Web chat page
	        
      
Eclipse: Steps to get development

	1. Start eclipse - Add path to jetty plugins
	   -- http://eclipse-jetty.sourceforge.net/update/
	   
	2. install Jetty, Maven m2e, web, javascript, web dev
		  
	3. Import from Git, use the repo of choice (sedro)	
		https://github.com/aledbetter/sedro.git

	4. convert to a maven project (Configure-> convert to maven)
	
	5. run with Jetty (this will configure the project via maven and download all dependencies)
		
	7. Project->properties->Project Facets->Java->set to 1.8
	
	8. Project->Properties->Java Build Path tab "Order and Export"
		Ensure that your JRE is higher up then Maven Dependencies
		
	9. Project->properties->Java Build Path->Add Folder... select ./src
	   - if there is a different path selected, remove it here AND delete it from the project source on the left 
	     then select project in left THEN project->Replace with Head and sync the code (because we deleted the files)
	     then add the new folder and apply 
		
	10. Project->Properties->Run/Debug->(project name)->Edit
		- SET: src/main/webapp for webapp location
		- Add enviroment variables to the run setup (or it will not work)
	
	11. Add all config params into the project Run As->Run Configuration->Environment
	    database user/password
	
	12. Run with Jetty for server
	