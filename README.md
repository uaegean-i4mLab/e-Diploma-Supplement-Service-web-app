# e-Diploma Supplement Service web app


## Introduction

This project was developed with funding form "Transformation of Greek e-Gov Services to eIDAS Crossborder Services", Agreement number: INEA/CEF/ICT/A2015/1147836 | Action No: 2015-EL-IA-0083, by the "Information Management Lab (i4M Lab)", which is part of the research group  "ATLANTIS Group".

It is comprised of a Java WebApp that streamlines and simplifies the integration of The e-Diploma Supplement Service web app to the Greek eIDAS node, in such a way that no
knowledge of the eIDAS SAML  profile is required. Additionally, it provides pre-built UI for guiding the users through eIDAS authentication flow.

It was developed by the "Information Management Lab (i4M Lab)", participant of the Atlantis Group (http://www.atlantis-group.gr/) of the University of the Aegean (https://www.aegean.gr/).

## Project Purpose

The e-Diploma Supplement Service web app complements the ISS 2.0 functionality by containing a pre built UI that the Service Provider (SP) can use and also requires from the SP to build less end points in order to retrieve the identification attributes from the eIDAS node. The retrieved attributes get bundled together as a JWT token that arrives to the SP in the form of a cookie (auth_token).

Important Note: In order to use the e-Diploma Supplement Service web app  for integration with the eIDAS GR node, a fully functional instance of the ISS 2.0 service must be deployed

## Deployment
First let us emphasize that the e-Diploma Supplement Service web app needs to be deployed in the same domain as the SP. This is required because the e-Diploma Supplement  service needs to be able to retrieve the authentication token (that the e-Diploma Supplement Service web app generates) which is transferred as a cookie.

The e-Diploma Supplement Service web app, is implemented by parameterizing a Docker image thus providing platform agnostic deployment. In order to deploy the e-Diploma Supplement Service web app as a docker container, the hosting machine must have a functional Docker engine. For instructions of how to setup Docker please refer to: https://docs.docker.com/install/ and follow the installation instructions depending on your hosting system (linux, windows, mac). Additionally for easier configuration of the container it is assumed that Docker compose is also installed (for instructions on installing docker compose please also refer to: https://docs.docker.com/compose/install/ ) although it is not a requirement. With Docker and Docker Compose set up the only thing required is a configuration file (yml) that will deploy a container from the image endimion13/eidas-gr-isswebapp:latest. Please be advised that at times (depending on updates) no image might be tagged as “latest”. To this end please browse the above repository and select the latest image tag available (at the moment of writing this 1.3).

For the rest of this document we assume that the e-Diploma Supplement Service web app (or simply WebApp) is to be deployed at https://eideusmartclass.aegean.gr/eidasLogin. Finally, the ISS 2.0 instance that the e-Diploma Supplement Service web app connects to is deployed at: https://eidasiss.aegean.gr:8081/ISS2

### ISS 2.0 integration

The WebApp exposes the following endpoints that need to be inserted as configuration parameters to the ISS 2.0 instance (assuming the deployment parameters of the previous paragraph).
- https://eideusmartclass.aegean.gr/eidasLogin/attributeList. This endpoint returns the list of eIDAS attributes required by the SP which are queried by the ISS 2.0
- https://eideusmartclass.aegean.gr/eidasLogin/issResponse. This endpoint parses and handles the response form the ISS 2.0
- https://eideusmartclass.aegean.gr/eidasLogin/authsuccess. This endpoint handles the success redirection from ISS 2.0
- https://eideusmartclass.aegean.gr/eidasLogin/authfail/. This endpoint handles the error redirection from ISS 2.0

Finally, upon setting up the integration with the ISS 2.0 instance an SP identifier should be created for the WebApp.
For the rest of this document we assume that this identifier will be sp1.
For instructions on how these integration parameters are added to the ISS 2.0 configuration please refer to  https://github.com/ellak-monades-aristeias/eIDAS-ISS

### Docker Container Configuration

After configuring the ISS 2.0 instance we are ready for the deployment of the WebApp. The easiest way is by defining a Docker Compose file. This file should follow the structure of the following snippet. We will go over the specific parameters one by one next.
```
version: '2'
services:
  loginWebApp:
   image: endimion13/eidas-gr-isswebapp:1.8.6.0
   ports:
     - 9091:8090
   environment:
     - EIDAS_PROPERTIES=CurrentFamilyName,CurrentGivenName,DateOfBirth,PersonIdentifier
     - SP_FAIL_PAGE=http://eideusmartclass.aegean.gr/authFail
     - SP_SUCCESS_PAGE=http://eideusmartclass.aegean.gr/eIDASSuccess
     - SP_LOGO=/img/logo2.png
     - ISS_URL=https://eidasiss.aegean.gr:8081/ISS2/ValidateToken
     - ISS_PRE_URL=https://eidasiss.aegean.gr:8081/ISS2/ValidateToken
     - SP_SERVER=https://eideusmartclass.aegean.gr
     - SP_ID=sp1
     - SP_SECRET=
     - AUTH_DURATION=43800
     - UAEGEAN_LOGIN=true
     - CLIENT_ID=867kszvon99qp4
     - REDIRECT_URI=https://eideusmartclass.aegean.gr/eIDASSuccess/linkedInResponse
     - LINKED_IN_SECRET=  
     - LINKED_IN=true
     - URL_PREFIX=/eidasLogin
     - UAEGEAN_AP=https://eidasiss.aegean.gr:8081/ISS2/ldap.jsp
     - HTTP_HEADER=true
     - ASYNC_SIGNATURE = true
     - SP_JWT_CERT = path to private key keystore
     - SP_KEY_PASS = password for the certificate;
     - STORE_PASS = password for the keystore
     - CERT_ALIAS = name of the certificate in the keystore
   volumes:
     - ./webappConfig:/webappConfig
```

- Version: denotes the syntax version of the composer file (up to the user for compatibility with the examples provided in this document please use 2 or 3
- Services: denotes the start of the service sector of the compose file
- issLoginWebApp: denotes the name of the e-Diploma Supplement Service web app service name (up to the user to pick a friendly name)
- Image: the image to use to load the container
- Ports: list of host machine ports that will be mapped to container ports (not that you should always map to container port 8090)
- Environment: list of environmental variables that will be added to the container
  - EIDAS_PROPERTIES: comma separated properties the SP requires from eIDAS
  - SP_FAIL_PAGE: e-Diploma Supplement url to redirect to in case of authentication failure
  - SP_SUCCESS_PAGE: e-Diploma Supplement url to redirect to in case of authentication success
  - SP_LOG: url of the logo that will be displayed at the header of the UIs
  - ISS_URL: url of the instance of ISS 2.0 that the WebApp module is connected to pointing to the endpoint ValidateToken of ISS 2.0
  - ISS_PRE_URL: ISS 2.0 url connected to the preproduction eIDAS node for tests (if available, else the same as ISS_URL)
  - SP_SERVER: the url of the SP home page (i.e. https://eideusmartclass.aegean.gr)
  - SP_ID: the id of the SP as that was configured in ISS 2.0
  - SP_SECRET: string that will be used on HS256 signature of the generated assertions that will be propagated to the SP.
  - AUTH_DURATION: integer denoting the duration for which the authentication cookie will be stored in milliseconds (if no value is given the cookie will not expire)
  - UAEGEAN_LOGIN: boolean, denote whether authentication using the UAegean Identity Provider is allowed
  - LINKED_IN: boolean, denotes if LinkedIn authentication is allowed
  - CLIENT_ID: client id if LinkedIn authentication is allowed
  - REDIRECT_URI: url to redirect to the result of a LinkedIn authentication process
  - LINKED_IN_SECRET: the secret share between the LinkedIn and the SP in case of LinkedIn authentication
  - URL_PREFIX: denotes (if available) be a prefix that should be added to the relative urls inside the UI conmponents of e-Diploma Supplement Service web app, in cache of proxy Deployment
  - UAEGEAN_AP: url of the UAegean Identity provider authentication service
  - HTTP_HEADER: boolean, denotes if the JWT should be send as a cookie or as an HTTP authentication header
  - ASYNC_SIGNATURE: boolean, denotes if the siging of the JWT should be made asynchronously (i.e. private/public key with RSA) or synchronously with HS256
  - SP_JWT_CERT: denotes the path to the private keystore containing the private key used to sign the JWT token (if available)
  - SP_KEY_PASS; the password to the aforementioned keystore
  - CERT_ALIAS: denotes the alias of the certificate that is to be used from the keystore to signe the JWT token (if available)
  volumes: denotes a folder that is to be uploaded to the docker container
    - ./webappConfig: a folder that may contain a file called countries.properties, if it exists it should contain a Java Properties file with the country name and code that will be presented in the user UI as possible Countries of Origin (e.g. greece=GR)

Deployment with such a file is simply a matter of starting the services defined in such a compose file, for example: docker-compose -f loginService.yml up
The e-Diploma Supplement Service web app can also be build and deployed as a jar (Spring boot application with embeded tomcat). In such a case the configuration parameters presented here should be passed as environmental variables on the hosting machine. However, docker deployment is strongly advised.

### Integration with UAegean Smart Class
In order for the e-Diploma Supplement to interact with the e-Diploma Supplement Service web app it:
- Upon authentication request, redirect the user to https://eideusmartclass.aegean.gr/eidasLogin/login. This is the URL containing the Country selection form that allows the user to initiate an eIDAS authentication flow.
- Has implemented the endpoint https://eideusmartclass.aegean.gr/eIDASSuccess, that consumes the genereated authentication response

The success endpoint consumes the JSON Web Token (JWT) generated by the WebApp delivered to the SP in the form of a cookie. JSON Web Tokens are an open, industry standard RFC 7519 method for representing claims securely between two parties. In the case of a successful authentication this token will contain the retrieved eIDAS identification attributes of the user. In case of an authentication failure this token will contain the authentication error as that was returned by the ISS 2.0.
The generated authentication token is signed using HS256 (HMAC with SHA-256) with a secret shared between the e-Diploma Supplement service and the WebApp.

Upon receiving the token the SP should validate it and read the identification attributes from its payload. The format of the generated JWT token payload is presented as an example below:
```
{
  "sub": "{\"eid\":\"GR/GR/ERMIS-11076669\",
  \"personIdentifier\":\"GR/GR/ERMIS-11076669\",
  \"dateOfBirth\":\"1980-01-01\",\"currentFamilyName\":\"ΠΕΤΡΟΥ,PETROU\",
  \"currentGivenName\":\"ΑΝΔΡΕΑΣ,ANDREAS\"}",
  "origin": "eIDAS"
}
```
This JWT contains two claimsm the "sub" which contains the actual eIDAS attributes and the "origin", which contains the source of the identification (e.g. eIDAS, LinkedIn etc.)
In case of an error in the authentication process no JWT is generated, but the type of the error is handled internally by the WebApp and an appropriate message is presented to the user.

### Repository Contents
- src, this repository folder contains the micro-service responsible for integrating the UAegean SmartClass WebApp with the ISS 2.0 ws (and thus eventually the Greek eIDAS node)
- docker-compose.yml, contains the actual parametrized docker compose deployment file that was used to deploy the service

### Additional Information
For additional information on the project and its deliverables please visit: https://docs.google.com/document/d/13A2m80xme22Jy35ZLueOwxIeC-NTU1-_k8qFpGemU0A/edit?usp=sharing
