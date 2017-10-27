# DS_ISS_Login


Example of using this as a service (pulling it from dockerhub):

  loginWebApp:
    image: endimion13/eidas-gr-isswebapp:1.1
    ports:
      - 8080:8090
      - 8090:8090
    environment:
       - EIDAS_PROPERTIES=CurrentFamilyName,CurrentGivenName,DateOfBirth,Person$
       - SP_FAIL_PAGE=https://www.google.com
       - SP_SUCCESS_PAGE=http://138.68.103.237/loginSuccess
       - SP_LOGO=http://excellence.minedu.gov.gr/thales/sites/default/files/Log$
       - ISS_URL=http://84.205.248.180/ISSPlus/ValidateToken
       - SP_ID=sp4
    networks:
      hyp-net:
        aliases:
          - loginWeb

