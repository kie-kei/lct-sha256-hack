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
  id: UUID;
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
