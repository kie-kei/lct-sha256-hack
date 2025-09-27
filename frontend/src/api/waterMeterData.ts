// src/api/waterMeterData.ts

import { apiClient } from "./clients";
import type {
  WaterMeterDataCreateRequest,
  WaterMeterDataResponse,
  WaterMeterDataUpdateRequest,
  PageWaterMeterDataResponse,
  Pageable,
  UUID,
} from "./types";

const BASE_PATH = "/api/v1/water-meter-data";

export const waterMeterDataApi = {
  // CRUD
  create(data: WaterMeterDataCreateRequest): Promise<WaterMeterDataResponse> {
    return apiClient.post(BASE_PATH, data).then((res) => res.data);
  },

  getById(id: UUID): Promise<WaterMeterDataResponse> {
    return apiClient.get(`${BASE_PATH}/${id}`).then((res) => res.data);
  },

  update(
    id: UUID,
    data: WaterMeterDataUpdateRequest,
  ): Promise<WaterMeterDataResponse> {
    return apiClient.put(`${BASE_PATH}/${id}`, data).then((res) => res.data);
  },

  delete(id: UUID): Promise<void> {
    return apiClient.delete(`${BASE_PATH}/${id}`).then(() => undefined);
  },

  // List
  getAll(pageable: Pageable): Promise<PageWaterMeterDataResponse> {
    return apiClient
      .get(BASE_PATH, { params: pageable })
      .then((res) => res.data);
  },

  // By ITP
  getByITP(itpId: UUID): Promise<WaterMeterDataResponse[]> {
    return apiClient.get(`${BASE_PATH}/itp/${itpId}`).then((res) => res.data);
  },

  getByITPPaged(
    itpId: UUID,
    pageable: Pageable,
  ): Promise<PageWaterMeterDataResponse> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/paged`, { params: pageable })
      .then((res) => res.data);
  },

  getLatestByITP(itpId: UUID): Promise<WaterMeterDataResponse[]> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/latest`)
      .then((res) => res.data);
  },

  getByITPAndPeriod(
    itpId: UUID,
    startDate: string,
    endDate: string,
  ): Promise<WaterMeterDataResponse[]> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/period`, {
        params: { startDate, endDate },
      })
      .then((res) => res.data);
  },

  // Aggregations
  getAverageHvsFlowByITPAndPeriod(
    itpId: UUID,
    startDate: string,
    endDate: string,
  ): Promise<number> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/average/hvs`, {
        params: { startDate, endDate },
      })
      .then((res) => res.data);
  },

  getAverageGvsFlowByITPAndPeriod(
    itpId: UUID,
    startDate: string,
    endDate: string,
  ): Promise<number> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/average/gvs`, {
        params: { startDate, endDate },
      })
      .then((res) => res.data);
  },
};
