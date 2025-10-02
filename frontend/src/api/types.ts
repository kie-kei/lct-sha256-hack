// src/api/types.ts

export type UUID = string;

export interface Pageable {
  page: number;
  size: number;
  sort?: string[];
}

export interface SortObject {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface PageableObject {
  sort: SortObject;
  offset: number;
  paged: boolean;
  pageNumber: number;
  pageSize: number;
  unpaged: boolean;
}

export interface Page<T> {
  totalElements: number;
  totalPages: number;
  sort: SortObject;
  first: boolean;
  last: boolean;
  size: number;
  content: T[];
  number: number;
  numberOfElements: number;
  pageable: PageableObject;
  empty: boolean;
}

// --- Water Meter Data ---
export interface WaterMeterDataCreateRequest {
  itpId: UUID;
  heatMeterIdentifier?: UUID;
  firstChannelFlowmeterIdentifier?: UUID;
  secondChannelFlowmeterIdentifier?: UUID;
  gvsFirstChannelFlowValue?: number;
  gvsSecondChannelFlowValue?: number;
  gvsConsumptionFlowValue?: number;
  waterMeterIdentifier?: UUID;
  hvsFlowValue?: number;
  measurementTimestamp: string; // ISO 8601
}

export interface WaterMeterDataUpdateRequest {
  heatMeterIdentifier?: UUID;
  firstChannelFlowmeterIdentifier?: UUID;
  secondChannelFlowmeterIdentifier?: UUID;
  gvsFirstChannelFlowValue?: number;
  gvsSecondChannelFlowValue?: number;
  gvsConsumptionFlowValue?: number;
  waterMeterIdentifier?: UUID;
  hvsFlowValue?: number;
  measurementTimestamp: string;
}

export interface WaterMeterDataResponse {
  id: UUID;
  itpId: UUID;
  heatMeterIdentifier?: UUID;
  firstChannelFlowmeterIdentifier?: UUID;
  secondChannelFlowmeterIdentifier?: UUID;
  gvsFirstChannelFlowValue?: number;
  gvsSecondChannelFlowValue?: number;
  gvsConsumptionFlowValue?: number;
  waterMeterIdentifier?: UUID;
  hvsFlowValue?: number;
  measurementTimestamp: string;
  createdAt: string;
  updatedAt: string;
}

// --- MKD ---
export interface MKDCreateRequest {
  address: string;
  fias: string;
  unom: string;
  latitude?: number;
  longitude?: number;
}

export interface MKDUpdateRequest {
  address: string;
  fias: string;
  unom: string;
  latitude?: number;
  longitude?: number;
}

export interface MKDResponse {
  id: UUID;
  address: string;
  fias: string;
  unom: string;
  latitude: number;
  longitude: number;
  itpId: UUID;
  createdAt: string;
  updatedAt: string;
}

// --- ITP ---
export interface ITPCreateRequest {
  id?: UUID;
  number: string;
  mkd?: MKDCreateRequest;
}

export interface ITPUpdateRequest {
  number: string;
}

export interface ITPResponse {
  id: UUID;
  number: string;
  createdAt: string;
  updatedAt: string;
}

export interface ITPDetailResponse {
  id: UUID;
  number: string;
  mkd: MKDResponse;
  waterMeterData: WaterMeterDataResponse[];
  createdAt: string;
  updatedAt: string;
}

// --- Pages ---
export type PageWaterMeterDataResponse = Page<WaterMeterDataResponse>;
export type PageMKDResponse = Page<MKDResponse>;
export type PageITPResponse = Page<ITPResponse>;

// --- Accident ---
export type ProbabilityType = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export interface AccidentCreateRequest {
  itpId: UUID;
  measurementTimestamp: string; // ISO 8601
  probabilityType?: ProbabilityType;
  isGvsFirstChannelFlowAnomaly?: boolean;
  gvsStandardFirstChannelFlowValue?: number;
  gvsActualFirstChannelFlowValue?: number;
  isGvsSecondChannelFlowAnomaly?: boolean;
  gvsStandardSecondChannelFlowValue?: number;
  gvsActualSecondChannelFlowValue?: number;
  isHvsConsumptionFlowAnomaly?: boolean;
  hvsStandardConsumptionFlowValue?: number;
  hvsActualConsumptionFlowValue?: number;
  isHvsGvsConsumptionFlowsAnomaly?: boolean;
  hvsGvsConsumptionFlowsDelta?: number;
  isGvsChannelsFlowsRatioAnomaly?: boolean;
  gvsChannelsFlowsRatio?: number;
  isGvsChannelsFlowsNegativeRatioAnomaly?: boolean;
  gvsChannelsFlowsNegativeRatio?: number;
}

export interface AccidentUpdateRequest {
  measurementTimestamp?: string;
  probabilityType?: ProbabilityType;
  isGvsFirstChannelFlowAnomaly?: boolean;
  gvsStandardFirstChannelFlowValue?: number;
  gvsActualFirstChannelFlowValue?: number;
  isGvsSecondChannelFlowAnomaly?: boolean;
  gvsStandardSecondChannelFlowValue?: number;
  gvsActualSecondChannelFlowValue?: number;
  isHvsConsumptionFlowAnomaly?: boolean;
  hvsStandardConsumptionFlowValue?: number;
  hvsActualConsumptionFlowValue?: number;
  isHvsGvsConsumptionFlowsAnomaly?: boolean;
  hvsGvsConsumptionFlowsDelta?: number;
  isGvsChannelsFlowsRatioAnomaly?: boolean;
  gvsChannelsFlowsRatio?: number;
  isGvsChannelsFlowsNegativeRatioAnomaly?: boolean;
  gvsChannelsFlowsNegativeRatio?: number;
}

export interface AccidentResponse {
  id: UUID;
  itpId: UUID;
  itpNumber: string;
  measurementTimestamp: string;
  probabilityType: ProbabilityType;
  isGvsFirstChannelFlowAnomaly: boolean;
  gvsStandardFirstChannelFlowValue: number;
  gvsActualFirstChannelFlowValue: number;
  isGvsSecondChannelFlowAnomaly: boolean;
  gvsStandardSecondChannelFlowValue: number;
  gvsActualSecondChannelFlowValue: number;
  isHvsConsumptionFlowAnomaly: boolean;
  hvsStandardConsumptionFlowValue: number;
  hvsActualConsumptionFlowValue: number;
  isHvsGvsConsumptionFlowsAnomaly: boolean;
  hvsGvsConsumptionFlowsDelta: number;
  isGvsChannelsFlowsRatioAnomaly: boolean;
  gvsChannelsFlowsRatio: number;
  isGvsChannelsFlowsNegativeRatioAnomaly: boolean;
  gvsChannelsFlowsNegativeRatio: number;
  createdAt: string;
  updatedAt: string;
}

export interface AccidentStatisticsResponse {
  itpId: UUID;
  itpNumber: string;
  totalAccidents: number;
  accidentsByProbability: Record<ProbabilityType, number>;
}

// --- Pages ---
export type PageAccidentResponse = Page<AccidentResponse>;

// --- Statistics ---
export type TimeStep = "HOUR" | "DAY" | "WEEK" | "MONTH";

export interface FlowDifferenceResponse {
  timestamp: string; // ISO 8601
  gvsConsumption: number;
  hvsFlow: number;
  difference: number;
}

export interface GvsFlowGraphDataResponse {
  timestamp: string; // ISO 8601
  supply: number;
  returnFlow: number;
  consumption: number;
}

export interface HvsFlowGraphDataResponse {
  timestamp: string; // ISO 8601
  consumption: number;
}

export interface StatisticResponse {
  gvsFlowGraph: GvsFlowGraphDataResponse[];
  hvsFlowGraph: HvsFlowGraphDataResponse[];
  flowDifferences: FlowDifferenceResponse[];
  totalGvsConsumption: number;
  totalGvsSupply: number;
  totalGvsReturn: number;
  totalHvsFlow: number;
  averageGvsSupply: number;
  averageGvsReturn: number;
  averageGvsConsumption: number;
  averageHvsFlow: number;
  startDate: string; // ISO 8601
  endDate: string; // ISO 8601
  timeStep: TimeStep;
}
