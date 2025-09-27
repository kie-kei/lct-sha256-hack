import { createApp } from "vue";
import App from "./App.vue";
import keycloak from "./keycloak";
import "../index.css";
import { createPinia } from "pinia";
import OpenLayersMap from "vue3-openlayers";
import { setupRouter } from "./router";
// import { Map, Layers, Sources } from "vue3-openlayers";
import { Layers } from "vue3-openlayers";
const app = createApp(App);
const pinia = createPinia();
keycloak
  .init({
    onLoad: "login-required",
    pkceMethod: "S256",
    checkLoginIframe: false,
  })
  .then((authenticated) => {
    if (authenticated) {
      console.log("User authenticated");
    } else {
      window.location.reload();
    }

    app.use(pinia);
    app.use(setupRouter());
    app.use(OpenLayersMap);

    // app.use(Map);

    // app.use(Sources);
    app.mount("#app");
  })
  .catch((error) => {
    console.error("Keycloak initialization failed", error);
  });
