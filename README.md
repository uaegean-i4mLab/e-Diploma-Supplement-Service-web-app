# Thin eIDAS WebApp 

## Introduction

This project was developed with funding form "Transformation of Greek e-Gov Services to eIDAS Crossborder Services", Agreement number: INEA/CEF/ICT/A2015/1147836 | Action No: 2015-EL-IA-0083, by the "Information Management Lab (i4M Lab)", which is part of the research group  "ATLANTIS Group".

It is comprised of a Java WebApp that streamlines and simplifies the integration of a Service Provider to the Greek eIDAS node, in such a way that no
knowledge of the eIDAS SAML  profile is required. Additionally, it provides pre-built UI for guiding the users through eIDAS authentication flow. 

It was developed by the "Information Management Lab (i4M Lab)", participant of the Atlantis Group (http://www.atlantis-group.gr/) of the University of the Aegean (https://www.aegean.gr/).

## Project Purpose

The Thin WebApp complements the ISS 2.0 functionality by containing a pre built UI that the SPs can use and also requires from the SPs to build less end points in order to retrieve the identification attributes from the eIDAS node. The retrieved attributes get bundled together as a JWT token that arrives to the SP in the form of a cookie (auth_token).

Important Note: In order to use the Thin WebApp for integration with the eIDAS GR node, a fully functional instance of the ISS 2.0 service must be deployed

## Deployment
First let us emphasise that the Thin WebApp needs to be deployed in the same domain as the SP. This is required because the SP needs be able to retrieve the authentication token (that the Thin WebApp generates) which is transferred as a cookie.

The Thin WebApp is offered as a Docker image thus providing platform agnostic deployment. In order to deploy the ThinWebApp as a docker container, the hosting machine must have a functional Docker engine. For instructions of how to setup Docker please refer to: https://docs.docker.com/install/ and follow the installation instructions depending on your hosting system (linux, windows, mac). Additionally for easier configuration of the container it is assumed that Docker compose is also installed (for instructions on installing docker compose please also refer to: https://docs.docker.com/compose/install/ ) although it is not a requirement. With Docker and Docker Compose set up the only thing required is a configuration file (yml) that will deploy a container from the image endimion13/eidas-gr-isswebapp:latest. Please be advised that at times (depending on updates) no image might be tagged as “latest”. To this end please browse the above repository and select the latest image tag available (at the moment of writing this 1.3).

For the rest of this document we assume that the Thin WebApp is to be deployed at http://www.host/com . Also, we assume that the Thin WebApp will be available through the port 8090 of the hosting machine. Finally, we assume that the ISS 2.0 instance is deployed at http://www.host.com/ISSPlus

### ISS 2.0 integration

The Thin WebApp exposes the following endpoints that need to be inserted as configuration parameters to the ISS 2.0 instance (assuming the deployment parameters of the previous paragraph).
- http://www.host.com:8090/attributeList. This endpoint returns the list of eIDAS attributes required by the SP which are queried by the ISS 2.0
- http://www.host.com:8090/issResponse. This endpoint parses and handles the response form the ISS 2.0
- http://www.host.com:8090/authsuccess. This endpoint handles the success redirection from ISS 2.0
- http://www.host.com:8090/authfail/. This endpoint handles the error redirection from ISS 2.0

Finally, upon setting up the integration with the ISS 2.0 instance an SP identifier should be created for the Thin WebApp.
For the rest of this document we assume that this identifier will be sp2. 
For instructions on how these integration parameters are added to the ISS 2.0 configuration please refer to  https://github.com/ellak-monades-aristeias/eIDAS-ISS

### Docker Container Configuration

After configuring the ISS 2.0 instance we are ready for the deployment of the Thin WebApp. The easiest way is by defining a Docker Compose file. This file should follow the structure of the following snippet. We will go over the specific parameters one by one next.
```
version: '2'
services: 
  issLoginWebApp: 
    container_name: issLoginWebApp 
    image: endimion13/eidas-gr-isswebapp:1.0 
    expose: - 8090 
    ports: 
      - 8090:8090 
    environment: 
      -EIDAS_PROPERTIES=http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName, http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName, http://eidas.europa.eu/attributes/naturalperson/DateOfBirth, http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier 
      - SP_FAIL_PAGE=https://www.google.com
      - SP_SUCCESS_PAGE=http://138.68.103.237/loginSuccess
      - SP_LOGO=http://trc.aiest.org/wp-content/uploads/2013/04/university-of-the-aegean.png
      - ISS_URL=http://84.205.248.180/ISSPlus/ValidateToken
      - SP_ID=sp2
      - SP_SECRET=secret
      - AUTH_DURATION=1800
```

- Version: denotes the syntax version of the composer file (up to the user for compatibility with the examples provided in this document please use 2 or 3
- Services: denotes the start of the service sector of the compose file
- issLoginWebApp: denotes the name of the Thin WebApp 2.0 service name (up to the user to pick a friendly name)
- Container_name: the name of the container (pick a friendly name again)
- Image: the image to use to load the container
- expose: list of ports that the container will expose to other container even though they might not be in the same docker network
- Ports: list of host machine ports that will be mapped to container ports (not that you should always map to container port 8090)
- Environment: list of environmental variables that will be added to the container
- EIDAS_PROPERTIES: comma separated properties the SP requires from eIDAS
- SP_FAIL_PAGE: SP url to redirect to in case of authentication failure
- SP_SUCCESS_PAGE: SP url to redirect to in case of authentication success
- SP_URL: url of the instance of ISS 2.0 that the Thin WebApp module is connected to pointing to the endpoint ValidateToken of ISS 2.0
- SP_ID: the id of the SP as that was configured in ISS 2.0
- SP_SECRET: string that will be used on HS256 signature of the generated assertions that will be propagated to the SP.
- AUTH_DURATION: integer denoting the duration for which the authentication cookie will be stored

Deployment with such a file is simply a matter of starting the services defined in such a compose file, for example: docker-compose -f loginService.yml up

### Integration
In order for the SP to interact with the Thin WebApp it needs to follow the next steps (in the rest of this section we assume that the Thin WebApp is deployed at http://www.host.com:8090):
- Upon authentication request, redirect the user to
- Build an endpoint (e.g. ) that the Thin WebApp will redirect to in case of authentication success
- (optionally) Build an endpoint (e.g. ) that the Thin WebApp will redirect to in case of authentication failure

The success and fail endpoints will consume a JSON Web Token (JWT) generated by the Thin WebApp delivered to the SP in the form of a cookie. JSON Web Tokens are an open, industry standard RFC 7519 method for representing claims securely between two parties. For this reason the Thin WebApp needs to be deployed in the same domain as the SP. In the case of a successful authentication this token will contain the retrieved eIDAS identification attributes of the user. In case of an authentication failure this token will contain the authentication error as that was returned by the ISS 2.0.
The generated authentication token is signed using HS256 (HMAC with SHA-256) with a secret shared between the SP and the Thin WebApp. Upon receiving the token the SP should validate it and read the identification attributes from its payload. The format of the generated JWT token payload is presented is identical to the presented https://github.com/ellak-monades-aristeias/eIDAS-SP-WebApp



# Additional Env variables, will be added to documentation correctly;
Env.EIDAS_PROPERTIES=CurrentFamilyName,CurrentGivenName,DateOfBirth,PersonIdentifier
Env.SP_FAIL_PAGE=https://www.google.com
Env.SP_SUCCESS_PAGE=http://138.68.103.237/loginSuccess
Env.SP_LOGO=http://communities-i4mlab.aegean.gr:8080/loginapp/img/logo2.png
Env.ISS_URL=http://84.205.248.180/ISSPlus/ValidateToken
Env.SP_ID=2
Env.UAEGEAN_LOGIN=true
Env.SP_SECRET=secret
Env.CLIENT_ID=linkedinClientId
Env.REDIRECT_URI=http://eideusmartclass.aegean.gr:9091/linkedInResponse
Env.LINKED_IN_SECRET=linkeinsecret
Env.LINKED_IN=true
Env.HTTP_HEADER=true