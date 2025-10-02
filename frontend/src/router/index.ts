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
        meta: {
          breadcrumb: "Карта",
          backPath: "/map",
        },
      },
      {
        path: "/itp",
        name: "itp",
        component: () => import("@/pages/ItpPage.vue"),
        meta: {
          breadcrumb: "ИТП и МКД",
          backPath: "/itp",
        },
      },
      {
        path: "/itp/:uuid/water_meter",
        name: "water_meter",
        component: () => import("@/pages/ItpDetailPage.vue"),
        props: true,
        meta: {
          breadcrumb: "ИТП и МКД",
          backPath: "/itp",
        },
      },
      {
        path: "/accidents",
        name: "accidents",
        component: () => import("@/pages/AccidentPage.vue"),
        meta: {
          breadcrumb: "Аварии",
          backPath: "/accidents",
        },
      },
      {
        path: "/statistics",
        name: "statistics",
        component: () => import("@/pages/StatisticsDashboard.vue"),
        meta: {
          breadcrumb: "Статистика",
          backPath: "/statistics",
        },
      },
      {
        path: "/water",
        name: "water",
        component: () => import("@/pages/WaterMetersPage.vue"),
        meta: {
          breadcrumb: "Данные ГВС и ХВС",
          backPath: "/water",
        },
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
