<template>
  <ol-map :loadTilesWhileAnimating="true" :loadTilesWhileInteracting="true">
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
      <ol-style>
        <ol-style-icon :src="markerIcon" :scale="0.1"></ol-style-icon>
      </ol-style>
    </ol-interaction-cluster-select>

    <ol-animated-cluster-layer :animationDuration="500" :distance="40">
      <ol-source-vector ref="vectorsource">
        <ol-feature
          v-for="(itm, idx) in coordinates"
          :key="idx"
          :properties="{ danger: itm.danger, id: itm.id }"
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

const { mkdList } = defineProps<{ mkdList: MKDResponse[] }>();

import { Circle, Fill, Stroke, Style } from "ol/style";
import markerIcon from "@/assets/apartment-building.png";
import { computed } from "vue";
import type { MKDResponse } from "@/api/types";
import type { Coordinate } from "ol/coordinate";
const moscowBounds4326 = [37.3, 55.5, 37.9, 55.9];
const moscowExtent = transformExtent(
  moscowBounds4326,
  "EPSG:4326",
  "EPSG:3857",
);

const coordinates = computed(() =>
  mkdList.map((x) => {
    return {
      danger: true,
      cord: fromLonLat([x.longitude, x.latitude]),
      id: x.id,
    };
  }),
);

const center = ref(fromLonLat([37.6173, 55.7558]));

const zoom = ref(10);
const rotation = ref(0);

const currentCenter = ref([37.6173, 55.7558]);
const currentZoom = ref(zoom.value);
const currentRotation = ref(rotation.value);
const currentResolution = ref(0);

function resolutionChanged(event: any) {
  currentResolution.value = event.target.getResolution();
  currentZoom.value = event.target.getZoom();
}

function centerChanged(event: any) {
  const center3857 = event.target.getCenter();
  const center4326: Coordinate = fromLonLat(center3857, "EPSG:3857");
  if (center4326[0] === undefined || center4326[1] === undefined) return;
  currentCenter.value = [
    parseFloat(center4326[0].toFixed(6)),
    parseFloat(center4326[1].toFixed(6)),
  ];
}

function rotationChanged(event: any) {
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

const overrideStyleFunction = (feature: any, style: any) => {
  const clusteredFeatures = feature.get("features");

  const hasDanger = clusteredFeatures.some(
    (f: any) => f.get("danger") === true,
  );

  const color = hasDanger ? "255,0,0" : "0,128,0";
  const radius = Math.max(8, Math.min(clusteredFeatures.length, 20));

  style.getImage().getStroke().setColor(`rgba(${color}, 0.5)`);
  style.getImage().getFill().setColor(`rgba(${color}, 1)`);

  style.getText().setText(clusteredFeatures.length.toString());
  style.getImage().setRadius(radius);

  return style;
};
const emits = defineEmits(["selected"]);
const featureSelected = (event: any) => {
  const allMkd = event.target.features_.array_;
  const selected = allMkd.map((x: any) => {
    const clusteredFeatures = x.get("features");
    return clusteredFeatures.find((f: any) => f.get("id")).values_.id;
  });
  console.log("selected", selected);
  emits("selected", selected);
};
</script>
