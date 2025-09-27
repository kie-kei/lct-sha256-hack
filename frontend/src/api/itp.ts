// src/api/itp.ts

import { apiClient } from "./clients";
import type {
  ITPCreateRequest,
  ITPResponse,
  ITPUpdateRequest,
  ITPDetailResponse,
  PageITPResponse,
  Pageable,
  UUID,
} from "./types";

const BASE_PATH = "/api/v1/itp";

export const itpApi = {
  create(data: ITPCreateRequest): Promise<ITPResponse> {
    return apiClient.post(BASE_PATH, data).then((res) => res.data);
  },

  getById(id: UUID): Promise<ITPDetailResponse> {
    return apiClient.get(`${BASE_PATH}/${id}`).then((res) => res.data);
  },

  getByIdSimple(id: UUID): Promise<ITPResponse> {
    return apiClient.get(`${BASE_PATH}/${id}/simple`).then((res) => res.data);
  },

  update(id: UUID, data: ITPUpdateRequest): Promise<ITPResponse> {
    return apiClient.put(`${BASE_PATH}/${id}`, data).then((res) => res.data);
  },

  delete(id: UUID): Promise<void> {
    return apiClient.delete(`${BASE_PATH}/${id}`).then(() => undefined);
  },

  getAll(pageable: Pageable): Promise<PageITPResponse> {
    return apiClient
      .get(BASE_PATH, { params: pageable })
      .then((res) => res.data);
  },

  search(number: string, pageable: Pageable): Promise<PageITPResponse> {
    return apiClient
      .get(`${BASE_PATH}/search`, { params: { number, ...pageable } })
      .then((res) => res.data);
  },
};
