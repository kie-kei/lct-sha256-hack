<template>
    <div id="app" class="h-screen w-screen">
        <!-- <h1>Keycloak Authentication with Vue 3 and Pinia</h1>
        <div v-if="!authStore.user">
            <button @click="authStore.login()">Login</button>
        </div>
        <div v-else>
            <p>Welcome, {{ authStore.user.preferred_username }}</p>
            <button @click="authStore.logout()">Logout</button>
        </div> -->
        <div ref="mapRootRef" class="w-64 h-32"></div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import { useAuthStore } from "./store/authStore";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import { fromLonLat } from "ol/proj";
import "ol/ol.css";

const mapRootRef = ref<HTMLElement | null>(null);
const authStore = useAuthStore();

// Центр Москвы (из вашего источника)
const MOSCOW_CENTER_LON = 37.6156; // долгота
const MOSCOW_CENTER_LAT = 55.7522; // широта

// "Радиус" ограничения в градусах (подберите под себя)
const DELTA_LON = 0.15; // ~15-20 км по долготе
const DELTA_LAT = 0.12; // ~15 км по широте

// Рассчитываем границы
const west = MOSCOW_CENTER_LON - DELTA_LON;
const east = MOSCOW_CENTER_LON + DELTA_LON;
const south = MOSCOW_CENTER_LAT - DELTA_LAT;
const north = MOSCOW_CENTER_LAT + DELTA_LAT;

// Конвертируем углы прямоугольника в проекцию EPSG:3857
const extent = [
    fromLonLat([west, south])[0], // minX
    fromLonLat([west, south])[1], // minY
    fromLonLat([east, north])[0], // maxX
    fromLonLat([east, north])[1], // maxY
];

// Центр в проекции карты
const center = fromLonLat([MOSCOW_CENTER_LON, MOSCOW_CENTER_LAT]);

onMounted(async () => {
    await authStore.fetchUser();

    const container = mapRootRef.value;
    if (!container) {
        console.error("Map container not found");
        return;
    }

    new Map({
        target: container,
        layers: [
            new TileLayer({
                source: new OSM(),
            }),
        ],
        view: new View({
            center: center,
            zoom: 11, // хороший зум для города
            constrainResolution: true,
            extent: extent, // ← ключевое: ограничивает перемещение
            enableRotation: false, // необязательно, но удобно
            maxZoom: 18, // можно добавить, если нужно
            minZoom: 9, // чтобы не отдалялись слишком сильно
        }),
    });
});
</script>
<style scoped></style>
