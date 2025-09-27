import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    component: () => import("../layouts/MainLayout.vue"),
    children: [
      {
        path: "/test",
        name: "test",
        component: () => import("../pages/TestPage.vue"),
      },
      {
        path: "/map",
        name: "map",
        component: () => import("../pages/MapPage.vue"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});
export const setupRouter = () => {
  return router;
};
export default router;
