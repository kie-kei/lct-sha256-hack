// src/api/accident.ts

import { apiClient } from "./clients";
import type {
  UUID,
  Pageable,
  AccidentCreateRequest,
  AccidentUpdateRequest,
  AccidentResponse,
  PageAccidentResponse,
  AccidentStatisticsResponse,
  ProbabilityType,
} from "./types";

const BASE_PATH = "/api/v1/accidents";

export const accidentApi = {
  create(data: AccidentCreateRequest): Promise<AccidentResponse> {
    return apiClient.post(BASE_PATH, data).then((res) => res.data);
  },

  getById(id: UUID): Promise<AccidentResponse> {
    return apiClient.get(`${BASE_PATH}/${id}`).then((res) => res.data);
  },

  update(id: UUID, data: AccidentUpdateRequest): Promise<AccidentResponse> {
    return apiClient.put(`${BASE_PATH}/${id}`, data).then((res) => res.data);
  },

  delete(id: UUID): Promise<void> {
    return apiClient.delete(`${BASE_PATH}/${id}`).then(() => undefined);
  },

  getAll(): Promise<AccidentResponse[]> {
    return apiClient.get(BASE_PATH).then((res) => res.data);
  },

  getAllPaged(pageable: Pageable): Promise<PageAccidentResponse> {
    return apiClient
      .get(`${BASE_PATH}/paged`, { params: pageable })
      .then((res) => res.data);
  },

  getByItpId(itpId: UUID): Promise<AccidentResponse[]> {
    return apiClient.get(`${BASE_PATH}/itp/${itpId}`).then((res) => res.data);
  },

  getByItpIdPaged(
    itpId: UUID,
    pageable: Pageable,
  ): Promise<PageAccidentResponse> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/paged`, { params: pageable })
      .then((res) => res.data);
  },

  getByProbability(
    probabilityType: ProbabilityType,
  ): Promise<AccidentResponse[]> {
    return apiClient
      .get(`${BASE_PATH}/probability/${probabilityType}`)
      .then((res) => res.data);
  },

  getByProbabilityPaged(
    probabilityType: ProbabilityType,
    pageable: Pageable,
  ): Promise<PageAccidentResponse> {
    return apiClient
      .get(`${BASE_PATH}/probability/${probabilityType}/paged`, {
        params: pageable,
      })
      .then((res) => res.data);
  },

  getByDateRange(
    startDate: string,
    endDate: string,
  ): Promise<AccidentResponse[]> {
    return apiClient
      .get(`${BASE_PATH}/date-range`, { params: { startDate, endDate } })
      .then((res) => res.data);
  },

  getByDateRangePaged(
    startDate: string,
    endDate: string,
    pageable: Pageable,
  ): Promise<PageAccidentResponse> {
    return apiClient
      .get(`${BASE_PATH}/date-range/paged`, {
        params: { startDate, endDate, ...pageable },
      })
      .then((res) => res.data);
  },

  getByItpAndDateRange(
    itpId: UUID,
    startDate: string,
    endDate: string,
  ): Promise<AccidentResponse[]> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/date-range`, {
        params: { startDate, endDate },
      })
      .then((res) => res.data);
  },

  getByItpAndDateRangePaged(
    itpId: UUID,
    startDate: string,
    endDate: string,
    pageable: Pageable,
  ): Promise<PageAccidentResponse> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/date-range/paged`, {
        params: { startDate, endDate, ...pageable },
      })
      .then((res) => res.data);
  },

  getAnomaliesByItp(
    itpId: UUID,
    filters: {
      isGvsFirstChannelFlowAnomaly?: boolean;
      isGvsSecondChannelFlowAnomaly?: boolean;
      isHvsConsumptionFlowAnomaly?: boolean;
      isHvsGvsConsumptionFlowsAnomaly?: boolean;
      isGvsChannelsFlowsRatioAnomaly?: boolean;
      isGvsChannelsFlowsNegativeRatioAnomaly?: boolean;
    } = {},
  ): Promise<AccidentResponse[]> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/anomalies`, { params: filters })
      .then((res) => res.data);
  },

  getAllWithAnomalies(
    filters: {
      isGvsFirstChannelFlowAnomaly?: boolean;
      isGvsSecondChannelFlowAnomaly?: boolean;
      isHvsConsumptionFlowAnomaly?: boolean;
      isHvsGvsConsumptionFlowsAnomaly?: boolean;
      isGvsChannelsFlowsRatioAnomaly?: boolean;
      isGvsChannelsFlowsNegativeRatioAnomaly?: boolean;
    } = {},
  ): Promise<AccidentResponse[]> {
    return apiClient
      .get(`${BASE_PATH}/anomalies`, { params: filters })
      .then((res) => res.data);
  },

  getStatisticsByItp(itpId: UUID): Promise<AccidentStatisticsResponse> {
    return apiClient
      .get(`${BASE_PATH}/itp/${itpId}/statistics`)
      .then((res) => res.data);
  },
};
