import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
  url: "http://wedrochers.ru:8085",
  realm: "sha256",
  clientId: "sha256-frontend",
});

export default keycloak;
