<template>
  <ol-map
    :loadTilesWhileAnimating="true"
    :loadTilesWhileInteracting="true"
    style="height: 100%"
  >
    <ol-view
      ref="view"
      :center="center"
      :rotation="rotation"
      :zoom="zoom"
      :extent="moscowExtent"
      @change:center="centerChanged"
      @change:resolution="resolutionChanged"
      @change:rotation="rotationChanged"
    />

    <ol-tile-layer>
      <ol-source-osm />
    </ol-tile-layer>

    <ol-interaction-cluster-select
      @select="featureSelected"
      :pointRadius="20"
      :featureStyle="featureStyle"
    >
      <!-- style of the marked selected from the cluster items after first click on the cluster itself -->
      <ol-style>
        <ol-style-icon :src="markerIcon" :scale="0.1"></ol-style-icon>
      </ol-style>
    </ol-interaction-cluster-select>

    <ol-animated-cluster-layer :animationDuration="500" :distance="40">
      <ol-source-vector ref="vectorsource">
        <ol-feature
          v-for="(itm, idx) in coordinates"
          :key="idx"
          :properties="{ danger: itm.danger }"
        >
          <ol-geom-point :coordinates="itm.cord"></ol-geom-point>
        </ol-feature>
      </ol-source-vector>

      <ol-style :overrideStyleFunction="overrideStyleFunction">
        <ol-style-stroke color="red" :width="2"></ol-style-stroke>
        <ol-style-fill color="rgba(255,255,255,0.1)"></ol-style-fill>

        <ol-style-circle :radius="20">
          <ol-style-stroke
            color="black"
            :width="15"
            :lineDash="[]"
            lineCap="butt"
          ></ol-style-stroke>
          <ol-style-fill color="black"></ol-style-fill>
        </ol-style-circle>

        <ol-style-text>
          <ol-style-fill color="white"></ol-style-fill>
        </ol-style-text>
      </ol-style>
    </ol-animated-cluster-layer>
  </ol-map>
</template>
<script setup lang="ts">
import { fromLonLat, transformExtent } from "ol/proj";
import "ol/ol.css";
import { ref } from "vue";

import { Circle, Fill, Stroke, Style } from "ol/style";
import markerIcon from "@/assets/apartment-building.png";
import { computed } from "vue";
import { mkdApi } from "@/api/mkd";
import { onMounted } from "vue";
import type { MKDResponse, Pageable, PageMKDResponse } from "@/api/types";
const moscowBounds4326 = [37.3, 55.5, 37.9, 55.9];
const moscowExtent = transformExtent(
  moscowBounds4326,
  "EPSG:4326",
  "EPSG:3857",
);
const projection = ref("EPSG:4326");
const sampleData = [
  {
    cord: [37.72536579931718, 55.7474097],
    danger: true,
  },
  {
    cord: [37.6253657893133, 55.7474097],
    danger: true,
  },
  {
    cord: [37.63536578931718, 55.7474197],
    danger: false,
  },
];
const test = computed(() =>
  sampleData.map((x) => {
    return {
      cord: fromLonLat(x.cord),
      danger: x.danger,
    };
  }),
);
const coordinates = computed(() =>
  mkdList.value.map((x) => {
    return { danger: true, cord: fromLonLat([x.longitude, x.latitude]) };
  }),
);
const loading = ref(false);
const error = ref<string | null>(null);
const mkdList = ref<MKDResponse[]>([]);
const pageable = ref<Pageable>({
  page: 0,
  size: 10,
  sort: ["id", "asc"],
});

// Функция загрузки данных
const loadMkdList = async () => {
  loading.value = true;
  error.value = null;

  try {
    const response: PageMKDResponse = await mkdApi.getAll(pageable.value);
    mkdList.value = response.content; // или [...mkdList.value, ...response.content] для бесконечной прокрутки
    pageable.value = {
      ...pageable.value,
      page: response.number,
      hasNext: response.number < response.totalPages - 1,
    };
  } catch (err) {
    console.error("Ошибка при загрузке MKD:", err);
    error.value = "Не удалось загрузить данные. Попробуйте позже.";
  } finally {
    loading.value = false;
  }
};

// Загрузка при монтировании
onMounted(() => {
  loadMkdList();
});

const center = ref(fromLonLat([37.6173, 55.7558]));

const zoom = ref(10);
const rotation = ref(0);

const currentCenter = ref([37.6173, 55.7558]);
const currentZoom = ref(zoom.value);
const currentRotation = ref(rotation.value);
const currentResolution = ref(0);

function resolutionChanged(event) {
  currentResolution.value = event.target.getResolution();
  currentZoom.value = event.target.getZoom();
}

function centerChanged(event) {
  const center3857 = event.target.getCenter();
  const center4326 = fromLonLat(center3857, "EPSG:3857");
  currentCenter.value = [
    parseFloat(center4326[0].toFixed(6)),
    parseFloat(center4326[1].toFixed(6)),
  ];
}

function rotationChanged(event) {
  currentRotation.value = event.target.getRotation();
}

const featureStyle = () => {
  return [
    new Style({
      stroke: new Stroke({
        color: "#ab34c4",
        width: 2,
        lineDash: [5, 5],
      }),
      image: new Circle({
        radius: 5,
        stroke: new Stroke({
          color: "#ab34c4",
          width: 1,
        }),
        fill: new Fill({
          color: "#ab34c444",
        }),
      }),
    }),
  ];
};

const overrideStyleFunction = (feature, style) => {
  const clusteredFeatures = feature.get("features"); // массив оригинальных фич

  // Проверяем: есть ли хотя бы одна опасная точка?
  const hasDanger = clusteredFeatures.some((f) => f.get("danger") === true);

  const color = hasDanger ? "255,0,0" : "0,128,0"; // красный или зеленый
  const radius = Math.max(8, Math.min(clusteredFeatures.length, 20));

  // Обновляем стиль круга
  style.getImage().getStroke().setColor(`rgba(${color}, 0.5)`);
  style.getImage().getFill().setColor(`rgba(${color}, 1)`);

  // Можно также менять текст или другие элементы
  style.getText().setText(clusteredFeatures.length.toString());
  style.getImage().setRadius(radius);

  return style;
};

const featureSelected = (event) => {
  console.log(event);
};
</script>
