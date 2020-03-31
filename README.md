# Sedro Chatbot Server

For conversational chatbots, digital assistants and chat interfaces.
Manage, configure and integrate chatbots for the Sedro Chatbot API. 

- Interfaceing with your accounts in Twilio, Twitter and more
- Works from isolated local machine, or in the cloud.
- Provides configuration GUI for managing Sedro Personas, Chatbots and testing with Web Chat.
- Deploy no code chatbots, or extend with Java or your favorite language.

## Sedro Chatbot API documentation
Documentation on Sedro APIs, examples and live tests can be found at www.sedro.xyz
API documents
- http://api.sedro.xyz/api-current#persona
- http://api.sedro.xyz/api-current#chat

RapidAPI Cloud based Sedro APIs
- https://rapidapi.com/WORDAPIS/api/inteligent-chatbots

Sedro Blog series on Chatbots
- http://blog.sedro.xyz/2020/01/21/working-with-persona-forms

### built package (click raw)
- https://github.com/aledbetter/sedrochatbot/blob/master/package/sedrochatbot-1.0.9999-bundle.zip

### building the Server

	1. go to base directory of branch, this will build the package with everything in it
	2. prompt# brew install maven
	3. prompt# mvn clean
	4. prompt# mvn install
	
### Run locally

	1. build the server
	2. prompt# mvn jetty:run
	3. To retain config add a DB info (see Data Base below)
	4. browser: http://localhost:8081
	   

### Deploy in AWS Elastic Beanstalk

	1. build the server
	2. create new EBS application with Java 8 on linux
	3. Add DB to the configuration
	3. upload target/sedrochatbot-1.0.9999-bundle.zip to elastic beanstalk       
		
### Data Base
	
	Environment variables
	RDS_DB_URL 	 - full jdbc url - jdbc:postgresql://<hostname>:<port>/<dbname>
	RDS_USERNAME - username
	RDS_PASSWORD - password
	ENC_KEY      - data encryption key (16 bytes), default key exists
	
### Login
	
	default username: taken from lowercase of rapid api name
	default password: password
	- if not running on https credentials and data are not secure

