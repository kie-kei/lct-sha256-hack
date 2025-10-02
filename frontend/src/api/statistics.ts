// src/api/statistics.ts

import { apiClient } from "./clients";
import type {
  StatisticResponse,
  UUID,
  TimeStep
} from "./types";

const BASE_PATH = "/api/v1/statistics";

export const statisticsApi = {
  /**
   * Get statistics by ITP ID
   */
  getByITP(
    itpId: UUID,
    startDate: string, // ISO 8601
    endDate: string, // ISO 8601
    timeStep: TimeStep
  ): Promise<StatisticResponse> {
    return apiClient
      .get(`${BASE_PATH}/by-itp`, {
        params: { itpId, startDate, endDate, timeStep }
      })
      .then((res) => res.data);
  },

  /**
   * Get statistics by district name
   */
  getByDistrict(
    districtName: string,
    startDate: string, // ISO 8601
    endDate: string, // ISO 8601
    timeStep: TimeStep
  ): Promise<StatisticResponse> {
    return apiClient
      .get(`${BASE_PATH}/by-district`, {
        params: { districtName, startDate, endDate, timeStep }
      })
      .then((res) => res.data);
  },

  /**
   * Get overall statistics across all ITPs
   */
  getOverall(
    startDate: string, // ISO 8601
    endDate: string, // ISO 8601
    timeStep: TimeStep
  ): Promise<StatisticResponse> {
    return apiClient
      .get(`${BASE_PATH}/all`, {
        params: { startDate, endDate, timeStep }
      })
      .then((res) => res.data);
  },
};