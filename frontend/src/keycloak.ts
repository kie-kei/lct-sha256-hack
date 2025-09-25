import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
  url: "http://192.168.0.105:8085",
  realm: "sha256",
  clientId: "sha256-frontend",
});

export default keycloak;
