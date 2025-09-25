import { createApp } from "vue";
import App from "./App.vue";
import keycloak from "./keycloak";
import "./style.css";
import { createPinia } from "pinia";

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
    app.mount("#app");
  })
  .catch((error) => {
    console.error("Keycloak initialization failed", error);
  });
