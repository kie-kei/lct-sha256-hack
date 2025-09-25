import { defineStore } from "pinia";
import { ref } from "vue";
import keycloak from "../keycloak";
import type { KeycloakTokenParsed } from "keycloak-js";

export const useAuthStore = defineStore("auth", () => {
  const user = ref<KeycloakTokenParsed | null>(null);
  const token = ref<string | null>(null);

  const login = (): Promise<void> => {
    return keycloak.login();
  };

  const logout = () => {
    keycloak.logout();
  };

  const fetchUser = async () => {
    if (keycloak.authenticated && keycloak.tokenParsed && keycloak.token) {
      user.value = keycloak.tokenParsed;
      token.value = keycloak.token;
    }
  };

  return {
    user,
    token,
    login,
    logout,
    fetchUser,
  };
});
