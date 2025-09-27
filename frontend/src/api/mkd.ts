// src/api/mkd.ts

import { apiClient } from "./clients";
import type {
  MKDCreateRequest,
  MKDResponse,
  MKDUpdateRequest,
  PageMKDResponse,
  Pageable,
  UUID,
} from "./types";

const BASE_PATH = "/api/v1/mkd";

export const mkdApi = {
  create(itpId: UUID, data: MKDCreateRequest): Promise<MKDResponse> {
    return apiClient
      .post(`${BASE_PATH}/itp/${itpId}`, data)
      .then((res) => res.data);
  },

  getById(id: UUID): Promise<MKDResponse> {
    return apiClient.get(`${BASE_PATH}/${id}`).then((res) => res.data);
  },

  update(id: UUID, data: MKDUpdateRequest): Promise<MKDResponse> {
    return apiClient.put(`${BASE_PATH}/${id}`, data).then((res) => res.data);
  },

  delete(id: UUID): Promise<void> {
    return apiClient.delete(`${BASE_PATH}/${id}`).then(() => undefined);
  },

  getAll(pageable: Pageable): Promise<PageMKDResponse> {
    return apiClient
      .get(BASE_PATH, { params: pageable })
      .then((res) => res.data);
  },

  getByItpId(itpId: UUID): Promise<MKDResponse> {
    return apiClient.get(`${BASE_PATH}/itp/${itpId}`).then((res) => res.data);
  },

  search(address: string, pageable: Pageable): Promise<PageMKDResponse> {
    return apiClient
      .get(`${BASE_PATH}/search`, { params: { address, ...pageable } })
      .then((res) => res.data);
  },

  getNearby(
    latitude: number,
    longitude: number,
    radius?: number,
  ): Promise<MKDResponse[]> {
    const params: Record<string, any> = { latitude, longitude };
    if (radius != null) params.radius = radius;
    return apiClient
      .get(`${BASE_PATH}/nearby`, { params })
      .then((res) => res.data);
  },
};
