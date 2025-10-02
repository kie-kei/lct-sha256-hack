# OpenAPI Documentation

## General Info

*   **Title:** OpenAPI definition
*   **Version:** v0
*   **Server:** `http://158.160.147.123:8083`

## Paths

### /api/v1/accidents/{id}

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAccidentById
*   **Description:** Get an accident by its ID.
*   **Parameters:**
    *   `id` (path, required): The UUID of the accident.
*   **Responses:**
    *   `200`: OK - Returns `AccidentResponse`.

#### PUT
*   **Tags:** accident-controller
*   **Operation ID:** updateAccident
*   **Description:** Update an existing accident.
*   **Parameters:**
    *   `id` (path, required): The UUID of the accident.
*   **Request Body:** `application/json`, required, schema `AccidentUpdateRequest`.
*   **Responses:**
    *   `200`: OK - Returns `AccidentResponse`.

#### DELETE
*   **Tags:** accident-controller
*   **Operation ID:** deleteAccident
*   **Description:** Delete an accident by its ID.
*   **Parameters:**
    *   `id` (path, required): The UUID of the accident.
*   **Responses:**
    *   `200`: OK

### /api/v1/accidents

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAllAccidents
*   **Description:** Get all accidents.
*   **Responses:**
    *   `200`: OK - Returns an array of `AccidentResponse`.

#### POST
*   **Tags:** accident-controller
*   **Operation ID:** createAccident
*   **Description:** Create a new accident.
*   **Request Body:** `application/json`, required, schema `AccidentCreateRequest`.
*   **Responses:**
    *   `200`: OK - Returns `AccidentResponse`.

### /api/v1/accidents/probability/{probabilityType}

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAccidentsByProbability
*   **Description:** Get accidents filtered by probability type.
*   **Parameters:**
    *   `probabilityType` (path, required): The probability type (LOW, MEDIUM, HIGH, CRITICAL).
*   **Responses:**
    *   `200`: OK - Returns an array of `AccidentResponse`.

### /api/v1/accidents/probability/{probabilityType}/paged

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAccidentsByProbabilityPaged
*   **Description:** Get accidents filtered by probability type with pagination.
*   **Parameters:**
    *   `probabilityType` (path, required): The probability type (LOW, MEDIUM, HIGH, CRITICAL).
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageAccidentResponse`.

### /api/v1/accidents/paged

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAllAccidents_1
*   **Description:** Get all accidents with pagination.
*   **Parameters:**
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageAccidentResponse`.

### /api/v1/accidents/itp/{itpId}

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAccidentsByItp
*   **Description:** Get accidents for a specific ITP.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
*   **Responses:**
    *   `200`: OK - Returns an array of `AccidentResponse`.

### /api/v1/accidents/itp/{itpId}/statistics

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getStatisticsByItp
*   **Description:** Get accident statistics for a specific ITP.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
*   **Responses:**
    *   `200`: OK - Returns `AccidentStatisticsResponse`.

### /api/v1/accidents/itp/{itpId}/paged

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAccidentsByItpPaged
*   **Description:** Get accidents for a specific ITP with pagination.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageAccidentResponse`.

### /api/v1/accidents/itp/{itpId}/date-range

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAccidentsByItpAndDateRange
*   **Description:** Get accidents for a specific ITP within a date range.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
*   **Responses:**
    *   `200`: OK - Returns an array of `AccidentResponse`.

### /api/v1/accidents/itp/{itpId}/date-range/paged

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAccidentsByItpAndDateRange_1
*   **Description:** Get accidents for a specific ITP within a date range with pagination.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageAccidentResponse`.

### /api/v1/accidents/itp/{itpId}/anomalies

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAnomaliesByItp
*   **Description:** Get accidents for a specific ITP filtered by anomaly types.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
    *   `isGvsFirstChannelFlowAnomaly` (query, optional): Filter by GVS first channel flow anomaly.
    *   `isGvsSecondChannelFlowAnomaly` (query, optional): Filter by GVS second channel flow anomaly.
    *   `isHvsConsumptionFlowAnomaly` (query, optional): Filter by HVS consumption flow anomaly.
    *   `isHvsGvsConsumptionFlowsAnomaly` (query, optional): Filter by HVS/GVS consumption flows anomaly.
    *   `isGvsChannelsFlowsRatioAnomaly` (query, optional): Filter by GVS channels flows ratio anomaly.
    *   `isGvsChannelsFlowsNegativeRatioAnomaly` (query, optional): Filter by GVS channels flows negative ratio anomaly.
*   **Responses:**
    *   `200`: OK - Returns an array of `AccidentResponse`.

### /api/v1/accidents/date-range

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAccidentsByDateRange
*   **Description:** Get accidents within a date range.
*   **Parameters:**
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
*   **Responses:**
    *   `200`: OK - Returns an array of `AccidentResponse`.

### /api/v1/accidents/date-range/paged

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAccidentsByDateRange_1
*   **Description:** Get accidents within a date range with pagination.
*   **Parameters:**
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageAccidentResponse`.

### /api/v1/accidents/anomalies

#### GET
*   **Tags:** accident-controller
*   **Operation ID:** getAllAccidentsWithAnomalies
*   **Description:** Get all accidents filtered by anomaly types.
*   **Parameters:**
    *   `isGvsFirstChannelFlowAnomaly` (query, optional): Filter by GVS first channel flow anomaly.
    *   `isGvsSecondChannelFlowAnomaly` (query, optional): Filter by GVS second channel flow anomaly.
    *   `isHvsConsumptionFlowAnomaly` (query, optional): Filter by HVS consumption flow anomaly.
    *   `isHvsGvsConsumptionFlowsAnomaly` (query, optional): Filter by HVS/GVS consumption flows anomaly.
    *   `isGvsChannelsFlowsRatioAnomaly` (query, optional): Filter by GVS channels flows ratio anomaly.
    *   `isGvsChannelsFlowsNegativeRatioAnomaly` (query, optional): Filter by GVS channels flows negative ratio anomaly.
*   **Responses:**
    *   `200`: OK - Returns an array of `AccidentResponse`.

### /api/v1/district

#### GET
*   **Tags:** district-controller
*   **Operation ID:** getAllDistricts
*   **Description:** Get all districts.
*   **Responses:**
    *   `200`: OK - Returns an array of `DistrictResponse`.

### /api/v1/itp/{id}

#### GET
*   **Tags:** itp-controller
*   **Operation ID:** getITPById
*   **Description:** Get an ITP by its ID with detailed information.
*   **Parameters:**
    *   `id` (path, required): The UUID of the ITP.
*   **Responses:**
    *   `200`: OK - Returns `ITPDetailResponse`.

#### PUT
*   **Tags:** itp-controller
*   **Operation ID:** updateITP
*   **Description:** Update an existing ITP.
*   **Parameters:**
    *   `id` (path, required): The UUID of the ITP.
*   **Request Body:** `application/json`, required, schema `ITPUpdateRequest`.
*   **Responses:**
    *   `200`: OK - Returns `ITPResponse`.

#### DELETE
*   **Tags:** itp-controller
*   **Operation ID:** deleteITP
*   **Description:** Delete an ITP by its ID.
*   **Parameters:**
    *   `id` (path, required): The UUID of the ITP.
*   **Responses:**
    *   `200`: OK

### /api/v1/itp

#### GET
*   **Tags:** itp-controller
*   **Operation ID:** getAllITPs
*   **Description:** Get all ITPs with pagination.
*   **Parameters:**
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageITPResponse`.

#### POST
*   **Tags:** itp-controller
*   **Operation ID:** createITP
*   **Description:** Create a new ITP.
*   **Request Body:** `application/json`, required, schema `ITPCreateRequest`.
*   **Responses:**
    *   `200`: OK - Returns `ITPResponse`.

### /api/v1/itp/{id}/simple

#### GET
*   **Tags:** itp-controller
*   **Operation ID:** getITPByIdSimple
*   **Description:** Get an ITP by its ID with basic information.
*   **Parameters:**
    *   `id` (path, required): The UUID of the ITP.
*   **Responses:**
    *   `200`: OK - Returns `ITPResponse`.

### /api/v1/itp/search

#### GET
*   **Tags:** itp-controller
*   **Operation ID:** searchITPs
*   **Description:** Search ITPs by number with pagination.
*   **Parameters:**
    *   `number` (query, required): The ITP number to search for.
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageITPResponse`.

### /api/v1/itp/by-district

#### GET
*   **Tags:** itp-controller
*   **Operation ID:** getAllITPsByDistrictName
*   **Description:** Get all ITPs for a specific district with pagination.
*   **Parameters:**
    *   `district` (query, required): The district name.
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageITPResponse`.

### /api/v1/mkd/{id}

#### GET
*   **Tags:** mkd-controller
*   **Operation ID:** getMKDById
*   **Description:** Get an MKD by its ID.
*   **Parameters:**
    *   `id` (path, required): The UUID of the MKD.
*   **Responses:**
    *   `200`: OK - Returns `MKDResponse`.

#### PUT
*   **Tags:** mkd-controller
*   **Operation ID:** updateMKD
*   **Description:** Update an existing MKD.
*   **Parameters:**
    *   `id` (path, required): The UUID of the MKD.
*   **Request Body:** `application/json`, required, schema `MKDUpdateRequest`.
*   **Responses:**
    *   `200`: OK - Returns `MKDResponse`.

#### DELETE
*   **Tags:** mkd-controller
*   **Operation ID:** deleteMKD
*   **Description:** Delete an MKD by its ID.
*   **Parameters:**
    *   `id` (path, required): The UUID of the MKD.
*   **Responses:**
    *   `200`: OK

### /api/v1/mkd

#### GET
*   **Tags:** mkd-controller
*   **Operation ID:** getAllMKDs
*   **Description:** Get all MKDs with pagination.
*   **Parameters:**
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns an object (Note: Schema is listed as "type": "object", not a specific schema).

### /api/v1/mkd/itp/{itpId}

#### GET
*   **Tags:** mkd-controller
*   **Operation ID:** getMKDByItpId
*   **Description:** Get the MKD associated with a specific ITP.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
*   **Responses:**
    *   `200`: OK - Returns `MKDResponse`.

#### POST
*   **Tags:** mkd-controller
*   **Operation ID:** createMKD
*   **Description:** Create a new MKD associated with an ITP.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
*   **Request Body:** `application/json`, required, schema `MKDCreateRequest`.
*   **Responses:**
    *   `200`: OK - Returns `MKDResponse`.

### /api/v1/mkd/search

#### GET
*   **Tags:** mkd-controller
*   **Operation ID:** searchMKDs
*   **Description:** Search MKDs by address with pagination.
*   **Parameters:**
    *   `address` (query, required): The address to search for.
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageMKDResponse`.

### /api/v1/mkd/nearby

#### GET
*   **Tags:** mkd-controller
*   **Operation ID:** getNearbyMKDs
*   **Description:** Get MKDs near a given location.
*   **Parameters:**
    *   `latitude` (query, required): The latitude.
    *   `longitude` (query, required): The longitude.
    *   `radius` (query, optional): The search radius (default 0.01).
*   **Responses:**
    *   `200`: OK - Returns an array of `MKDResponse`.

### /api/v1/statistics/by-itp

#### GET
*   **Tags:** statistic-controller
*   **Operation ID:** getStatisticsByITP
*   **Description:** Get statistics for a specific ITP.
*   **Parameters:**
    *   `itpId` (query, required): The UUID of the ITP.
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
    *   `timeStep` (query, required): Time step (HOUR, DAY, WEEK, MONTH).
*   **Responses:**
    *   `200`: OK - Returns `StatisticResponse`.

### /api/v1/statistics/by-district

#### GET
*   **Tags:** statistic-controller
*   **Operation ID:** getStatisticsByDistrict
*   **Description:** Get statistics for a specific district.
*   **Parameters:**
    *   `districtName` (query, required): The district name.
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
    *   `timeStep` (query, required): Time step (HOUR, DAY, WEEK, MONTH).
*   **Responses:**
    *   `200`: OK - Returns `StatisticResponse`.

### /api/v1/statistics/all

#### GET
*   **Tags:** statistic-controller
*   **Operation ID:** getOverallStatistics
*   **Description:** Get overall statistics.
*   **Parameters:**
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
    *   `timeStep` (query, required): Time step (HOUR, DAY, WEEK, MONTH).
*   **Responses:**
    *   `200`: OK - Returns `StatisticResponse`.

### /api/v1/water-meter-data/{id}

#### GET
*   **Tags:** water-meter-data-controller
*   **Operation ID:** getWaterMeterDataById
*   **Description:** Get water meter data by its ID.
*   **Parameters:**
    *   `id` (path, required): The UUID of the water meter data.
*   **Responses:**
    *   `200`: OK - Returns `WaterMeterDataResponse`.

#### PUT
*   **Tags:** water-meter-data-controller
*   **Operation ID:** updateWaterMeterData
*   **Description:** Update existing water meter data.
*   **Parameters:**
    *   `id` (path, required): The UUID of the water meter data.
*   **Request Body:** `application/json`, required, schema `WaterMeterDataUpdateRequest`.
*   **Responses:**
    *   `200`: OK - Returns `WaterMeterDataResponse`.

#### DELETE
*   **Tags:** water-meter-data-controller
*   **Operation ID:** deleteWaterMeterData
*   **Description:** Delete water meter data by its ID.
*   **Parameters:**
    *   `id` (path, required): The UUID of the water meter data.
*   **Responses:**
    *   `200`: OK

### /api/v1/water-meter-data

#### GET
*   **Tags:** water-meter-data-controller
*   **Operation ID:** getAllWaterMeterData
*   **Description:** Get all water meter data with pagination.
*   **Parameters:**
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageWaterMeterDataResponse`.

#### POST
*   **Tags:** water-meter-data-controller
*   **Operation ID:** createWaterMeterData
*   **Description:** Create new water meter data.
*   **Request Body:** `application/json`, required, schema `WaterMeterDataCreateRequest`.
*   **Responses:**
    *   `200`: OK - Returns `WaterMeterDataResponse`.

### /api/v1/water-meter-data/upload/itp/{itpId}

#### POST
*   **Tags:** water-meter-data-controller
*   **Operation ID:** uploadWaterMeterData
*   **Description:** Upload water meter data (GVS and HVS) for an ITP.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
*   **Request Body:** `application/json`, schema with `gvsData` and `hvsData` properties (format: binary).
*   **Responses:**
    *   `200`: OK - Returns an array of `WaterMeterDataResponse`.

### /api/v1/water-meter-data/itp/{itpId}

#### GET
*   **Tags:** water-meter-data-controller
*   **Operation ID:** getWaterMeterDataByITP
*   **Description:** Get all water meter data for a specific ITP.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
*   **Responses:**
    *   `200`: OK - Returns an array of `WaterMeterDataResponse`.

### /api/v1/water-meter-data/itp/{itpId}/period

#### GET
*   **Tags:** water-meter-data-controller
*   **Operation ID:** getWaterMeterDataByITPAndPeriod
*   **Description:** Get water meter data for a specific ITP within a date range.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
*   **Responses:**
    *   `200`: OK - Returns an array of `WaterMeterDataResponse`.

### /api/v1/water-meter-data/itp/{itpId}/period-for-hour

#### GET
*   **Tags:** water-meter-data-controller
*   **Operation ID:** getAllByItpIdAndHourForPeriod
*   **Description:** Get water meter data for a specific ITP on a specific hour of the day over a number of days.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
    *   `days` (query, required): Number of days to look back.
    *   `hour` (query, required): The hour of the day (0-23).
*   **Responses:**
    *   `200`: OK - Returns an array of `WaterMeterDataResponse`.

### /api/v1/water-meter-data/itp/{itpId}/paged

#### GET
*   **Tags:** water-meter-data-controller
*   **Operation ID:** getWaterMeterDataByITPPaged
*   **Description:** Get water meter data for a specific ITP with pagination.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
    *   `pageable` (query, required): Pagination parameters.
*   **Responses:**
    *   `200`: OK - Returns `PageWaterMeterDataResponse`.

### /api/v1/water-meter-data/itp/{itpId}/latest

#### GET
*   **Tags:** water-meter-data-controller
*   **Operation ID:** getLatestWaterMeterDataByITP
*   **Description:** Get the latest water meter data for a specific ITP.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
*   **Responses:**
    *   `200`: OK - Returns an array of `WaterMeterDataResponse`.

### /api/v1/water-meter-data/itp/{itpId}/average/hvs

#### GET
*   **Tags:** water-meter-data-controller
*   **Operation ID:** getAverageHvsFlowByITPAndPeriod
*   **Description:** Get the average HVS flow for a specific ITP within a date range.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
*   **Responses:**
    *   `200`: OK - Returns a number (double).

### /api/v1/water-meter-data/itp/{itpId}/average/gvs

#### GET
*   **Tags:** water-meter-data-controller
*   **Operation ID:** getAverageGvsFlowByITPAndPeriod
*   **Description:** Get the average GVS flow for a specific ITP within a date range.
*   **Parameters:**
    *   `itpId` (path, required): The UUID of the ITP.
    *   `startDate` (query, required): Start date-time.
    *   `endDate` (query, required): End date-time.
*   **Responses:**
    *   `200`: OK - Returns a number (double).

## Components

### Schemas

#### AccidentCreateRequest
*   **Type:** object
*   **Required:** `itpId`, `measurementTimestamp`
*   **Properties:**
    *   `itpId` (string, uuid): The UUID of the associated ITP.
    *   `measurementTimestamp` (string, date-time): The timestamp of the measurement.
    *   `probabilityType` (string, enum: LOW, MEDIUM, HIGH, CRITICAL): The probability type of the accident.
    *   `isGvsFirstChannelFlowAnomaly` (boolean): Flag for GVS first channel flow anomaly.
    *   `gvsStandardFirstChannelFlowValue` (number, float): Standard GVS first channel flow value.
    *   `gvsActualFirstChannelFlowValue` (number, float): Actual GVS first channel flow value.
    *   `isGvsSecondChannelFlowAnomaly` (boolean): Flag for GVS second channel flow anomaly.
    *   `gvsStandardSecondChannelFlowValue` (number, float): Standard GVS second channel flow value.
    *   `gvsActualSecondChannelFlowValue` (number, float): Actual GVS second channel flow value.
    *   `isHvsConsumptionFlowAnomaly` (boolean): Flag for HVS consumption flow anomaly.
    *   `hvsStandardConsumptionFlowValue` (number, float): Standard HVS consumption flow value.
    *   `hvsActualConsumptionFlowValue` (number, float): Actual HVS consumption flow value.
    *   `isHvsGvsConsumptionFlowsAnomaly` (boolean): Flag for HVS/GVS consumption flows anomaly.
    *   `hvsGvsConsumptionFlowsDelta` (number, float): Delta between HVS and GVS consumption flows.
    *   `isGvsChannelsFlowsRatioAnomaly` (boolean): Flag for GVS channels flows ratio anomaly.
    *   `gvsChannelsFlowsRatio` (number, float): Ratio of GVS channels flows.
    *   `isGvsChannelsFlowsNegativeRatioAnomaly` (boolean): Flag for GVS channels flows negative ratio anomaly.
    *   `gvsChannelsFlowsNegativeRatio` (number, float): Negative ratio of GVS channels flows.

#### AccidentResponse
*   **Type:** object
*   **Properties:**
    *   `id` (string, uuid): The UUID of the accident.
    *   `itpId` (string, uuid): The UUID of the associated ITP.
    *   `itpNumber` (string): The number of the associated ITP.
    *   `measurementTimestamp` (string, date-time): The timestamp of the measurement.
    *   `probabilityType` (string, enum: LOW, MEDIUM, HIGH, CRITICAL): The probability type of the accident.
    *   `isGvsFirstChannelFlowAnomaly` (boolean): Flag for GVS first channel flow anomaly.
    *   `gvsStandardFirstChannelFlowValue` (number, float): Standard GVS first channel flow value.
    *   `gvsActualFirstChannelFlowValue` (number, float): Actual GVS first channel flow value.
    *   `isGvsSecondChannelFlowAnomaly` (boolean): Flag for GVS second channel flow anomaly.
    *   `gvsStandardSecondChannelFlowValue` (number, float): Standard GVS second channel flow value.
    *   `gvsActualSecondChannelFlowValue` (number, float): Actual GVS second channel flow value.
    *   `isHvsConsumptionFlowAnomaly` (boolean): Flag for HVS consumption flow anomaly.
    *   `hvsStandardConsumptionFlowValue` (number, float): Standard HVS consumption flow value.
    *   `hvsActualConsumptionFlowValue` (number, float): Actual HVS consumption flow value.
    *   `isHvsGvsConsumptionFlowsAnomaly` (boolean): Flag for HVS/GVS consumption flows anomaly.
    *   `hvsGvsConsumptionFlowsDelta` (number, float): Delta between HVS and GVS consumption flows.
    *   `isGvsChannelsFlowsRatioAnomaly` (boolean): Flag for GVS channels flows ratio anomaly.
    *   `gvsChannelsFlowsRatio` (number, float): Ratio of GVS channels flows.
    *   `isGvsChannelsFlowsNegativeRatioAnomaly` (boolean): Flag for GVS channels flows negative ratio anomaly.
    *   `gvsChannelsFlowsNegativeRatio` (number, float): Negative ratio of GVS channels flows.
    *   `createdAt` (string, date-time): Creation timestamp.
    *   `updatedAt` (string, date-time): Update timestamp.

#### AccidentStatisticsResponse
*   **Type:** object
*   **Properties:**
    *   `itpId` (string, uuid): The UUID of the ITP.
    *   `itpNumber` (string): The number of the ITP.
    *   `totalAccidents` (integer, int64): Total number of accidents.
    *   `accidentsByProbability` (object): Map of accident counts by probability type.

#### AccidentUpdateRequest
*   **Type:** object
*   **Properties:**
    *   `measurementTimestamp` (string, date-time): The timestamp of the measurement.
    *   `probabilityType` (string, enum: LOW, MEDIUM, HIGH, CRITICAL): The probability type of the accident.
    *   `isGvsFirstChannelFlowAnomaly` (boolean): Flag for GVS first channel flow anomaly.
    *   `gvsStandardFirstChannelFlowValue` (number, float): Standard GVS first channel flow value.
    *   `gvsActualFirstChannelFlowValue` (number, float): Actual GVS first channel flow value.
    *   `isGvsSecondChannelFlowAnomaly` (boolean): Flag for GVS second channel flow anomaly.
    *   `gvsStandardSecondChannelFlowValue` (number, float): Standard GVS second channel flow value.
    *   `gvsActualSecondChannelFlowValue` (number, float): Actual GVS second channel flow value.
    *   `isHvsConsumptionFlowAnomaly` (boolean): Flag for HVS consumption flow anomaly.
    *   `hvsStandardConsumptionFlowValue` (number, float): Standard HVS consumption flow value.
    *   `hvsActualConsumptionFlowValue` (number, float): Actual HVS consumption flow value.
    *   `isHvsGvsConsumptionFlowsAnomaly` (boolean): Flag for HVS/GVS consumption flows anomaly.
    *   `hvsGvsConsumptionFlowsDelta` (number, float): Delta between HVS and GVS consumption flows.
    *   `isGvsChannelsFlowsRatioAnomaly` (boolean): Flag for GVS channels flows ratio anomaly.
    *   `gvsChannelsFlowsRatio` (number, float): Ratio of GVS channels flows.
    *   `isGvsChannelsFlowsNegativeRatioAnomaly` (boolean): Flag for GVS channels flows negative ratio anomaly.
    *   `gvsChannelsFlowsNegativeRatio` (number, float): Negative ratio of GVS channels flows.

#### DistrictResponse
*   **Type:** object
*   **Properties:**
    *   `id` (string, uuid): The UUID of the district.
    *   `name` (string): The name of the district.

#### FlowDifferenceResponse
*   **Type:** object
*   **Properties:**
    *   `timestamp` (string, date-time): The timestamp.
    *   `gvsConsumption` (number, float): GVS consumption value.
    *   `hvsFlow` (number, float): HVS flow value.
    *   `difference` (number, float): The difference between GVS consumption and HVS flow.

#### GvsFlowGraphDataResponse
*   **Type:** object
*   **Properties:**
    *   `timestamp` (string, date-time): The timestamp.
    *   `supply` (number, float): Supply flow value.
    *   `returnFlow` (number, float): Return flow value.
    *   `consumption` (number, float): Consumption flow value.

#### HvsFlowGraphDataResponse
*   **Type:** object
*   **Properties:**
    *   `timestamp` (string, date-time): The timestamp.
    *   `consumption` (number, float): Consumption flow value.

#### ITPCreateRequest
*   **Type:** object
*   **Required:** `id`, `number`
*   **Properties:**
    *   `id` (string, uuid): The UUID of the ITP.
    *   `number` (string): The number of the ITP.
    *   `mkd` (MKDCreateRequest): The associated MKD details.

#### ITPDetailResponse
*   **Type:** object
*   **Properties:**
    *   `id` (string, uuid): The UUID of the ITP.
    *   `number` (string): The number of the ITP.
    *   `mkd` (MKDResponse): The associated MKD details.
    *   `waterMeterData` (array of WaterMeterDataResponse): Associated water meter data.
    *   `createdAt` (string, date-time): Creation timestamp.
    *   `updatedAt` (string, date-time): Update timestamp.

#### ITPResponse
*   **Type:** object
*   **Properties:**
    *   `id` (string, uuid): The UUID of the ITP.
    *   `number` (string): The number of the ITP.
    *   `mkd` (MKDResponse): The associated MKD details.
    *   `createdAt` (string, date-time): Creation timestamp.
    *   `updatedAt` (string, date-time): Update timestamp.

#### ITPUpdateRequest
*   **Type:** object
*   **Required:** `number`
*   **Properties:**
    *   `number` (string): The number of the ITP.

#### MKDCreateRequest
*   **Type:** object
*   **Required:** `address`, `fias`, `unom`
*   **Properties:**
    *   `address` (string): The address of the MKD.
    *   `fias` (string): The FIAS code.
    *   `unom` (string): The UNOM code.

#### MKDResponse
*   **Type:** object
*   **Properties:**
    *   `id` (string, uuid): The UUID of the MKD.
    *   `address` (string): The address of the MKD.
    *   `fias` (string): The FIAS code.
    *   `unom` (string): The UNOM code.
    *   `latitude` (number): The latitude of the MKD.
    *   `longitude` (number): The longitude of the MKD.
    *   `itpId` (string, uuid): The UUID of the associated ITP.
    *   `createdAt` (string, date-time): Creation timestamp.
    *   `updatedAt` (string, date-time): Update timestamp.
    *   `district` (string): The district name.

#### MKDUpdateRequest
*   **Type:** object
*   **Required:** `address`, `fias`, `unom`
*   **Properties:**
    *   `address` (string): The address of the MKD.
    *   `fias` (string): The FIAS code.
    *   `unom` (string): The UNOM code.

#### PageAccidentResponse
*   **Type:** object
*   **Properties:**
    *   `totalElements` (integer, int64): Total number of elements.
    *   `totalPages` (integer, int32): Total number of pages.
    *   `first` (boolean): Whether this is the first page.
    *   `last` (boolean): Whether this is the last page.
    *   `sort` (SortObject): Sorting information.
    *   `size` (integer, int32): Size of the page.
    *   `content` (array of AccidentResponse): The content of the page.
    *   `number` (integer, int32): Current page number.
    *   `numberOfElements` (integer, int32): Number of elements on the current page.
    *   `pageable` (PageableObject): Pageable information.
    *   `empty` (boolean): Whether the page is empty.

#### PageITPResponse
*   **Type:** object
*   **Properties:**
    *   `totalElements` (integer, int64): Total number of elements.
    *   `totalPages` (integer, int32): Total number of pages.
    *   `first` (boolean): Whether this is the first page.
    *   `last` (boolean): Whether this is the last page.
    *   `sort` (SortObject): Sorting information.
    *   `size` (integer, int32): Size of the page.
    *   `content` (array of ITPResponse): The content of the page.
    *   `number` (integer, int32): Current page number.
    *   `numberOfElements` (integer, int32): Number of elements on the current page.
    *   `pageable` (PageableObject): Pageable information.
    *   `empty` (boolean): Whether the page is empty.

#### PageMKDResponse
*   **Type:** object
*   **Properties:**
    *   `totalElements` (integer, int64): Total number of elements.
    *   `totalPages` (integer, int32): Total number of pages.
    *   `first` (boolean): Whether this is the first page.
    *   `last` (boolean): Whether this is the last page.
    *   `sort` (SortObject): Sorting information.
    *   `size` (integer, int32): Size of the page.
    *   `content` (array of MKDResponse): The content of the page.
    *   `number` (integer, int32): Current page number.
    *   `numberOfElements` (integer, int32): Number of elements on the current page.
    *   `pageable` (PageableObject): Pageable information.
    *   `empty` (boolean): Whether the page is empty.

#### PageWaterMeterDataResponse
*   **Type:** object
*   **Properties:**
    *   `totalElements` (integer, int64): Total number of elements.
    *   `totalPages` (integer, int32): Total number of pages.
    *   `first` (boolean): Whether this is the first page.
    *   `last` (boolean): Whether this is the last page.
    *   `sort` (SortObject): Sorting information.
    *   `size` (integer, int32): Size of the page.
    *   `content` (array of WaterMeterDataResponse): The content of the page.
    *   `number` (integer, int32): Current page number.
    *   `numberOfElements` (integer, int32): Number of elements on the current page.
    *   `pageable` (PageableObject): Pageable information.
    *   `empty` (boolean): Whether the page is empty.

#### PageableObject
*   **Type:** object
*   **Properties:**
    *   `sort` (SortObject): Sorting information.
    *   `offset` (integer, int64): The offset.
    *   `unpaged` (boolean): Whether the result is unpaged.
    *   `paged` (boolean): Whether the result is paged.
    *   `pageNumber` (integer, int32): The page number.
    *   `pageSize` (integer, int32): The page size.

#### Pageable
*   **Type:** object
*   **Properties:**
    *   `page` (integer, int32, minimum: 0): The page number.
    *   `size` (integer, int32, minimum: 1): The page size.
    *   `sort` (array of string): Sorting criteria.

#### SortObject
*   **Type:** object
*   **Properties:**
    *   `unsorted` (boolean): Whether the result is unsorted.
    *   `sorted` (boolean): Whether the result is sorted.
    *   `empty` (boolean): Whether the sort is empty.

#### StatisticResponse
*   **Type:** object
*   **Properties:**
    *   `gvsFlowGraph` (array of GvsFlowGraphDataResponse): GVS flow graph data.
    *   `hvsFlowGraph` (array of HvsFlowGraphDataResponse): HVS flow graph data.
    *   `flowDifferences` (array of FlowDifferenceResponse): Flow difference data.
    *   `totalGvsConsumption` (number, float): Total GVS consumption.
    *   `totalGvsSupply` (number, float): Total GVS supply.
    *   `totalGvsReturn` (number, float): Total GVS return.
    *   `totalHvsFlow` (number, float): Total HVS flow.
    *   `averageGvsSupply` (number, float): Average GVS supply.
    *   `averageGvsReturn` (number, float): Average GVS return.
    *   `averageGvsConsumption` (number, float): Average GVS consumption.
    *   `averageHvsFlow` (number, float): Average HVS flow.
    *   `startDate` (string, date-time): Start date of the statistics period.
    *   `endDate` (string, date-time): End date of the statistics period.
    *   `timeStep` (string, enum: HOUR, DAY, WEEK, MONTH): Time step used for aggregation.

#### WaterMeterDataCreateRequest
*   **Type:** object
*   **Required:** `itpId`, `measurementTimestamp`
*   **Properties:**
    *   `itpId` (string, uuid): The UUID of the associated ITP.
    *   `heatMeterIdentifier` (string, uuid): Identifier for the heat meter.
    *   `firstChannelFlowmeterIdentifier` (string, uuid): Identifier for the first channel flowmeter.
    *   `secondChannelFlowmeterIdentifier` (string, uuid): Identifier for the second channel flowmeter.
    *   `gvsFirstChannelFlowValue` (number, float): GVS first channel flow value.
    *   `gvsSecondChannelFlowValue` (number, float): GVS second channel flow value.
    *   `gvsConsumptionFlowValue` (number, float): GVS consumption flow value.
    *   `waterMeterIdentifier` (string, uuid): Identifier for the water meter.
    *   `hvsFlowValue` (number, float): HVS flow value.
    *   `measurementTimestamp` (string, date-time): The timestamp of the measurement.

#### WaterMeterDataResponse
*   **Type:** object
*   **Properties:**
    *   `id` (string, uuid): The UUID of the water meter data entry.
    *   `itpId` (string, uuid): The UUID of the associated ITP.
    *   `heatMeterIdentifier` (string, uuid): Identifier for the heat meter.
    *   `firstChannelFlowmeterIdentifier` (string, uuid): Identifier for the first channel flowmeter.
    *   `secondChannelFlowmeterIdentifier` (string, uuid): Identifier for the second channel flowmeter.
    *   `gvsFirstChannelFlowValue` (number, float): GVS first channel flow value.
    *   `gvsSecondChannelFlowValue` (number, float): GVS second channel flow value.
    *   `gvsConsumptionFlowValue` (number, float): GVS consumption flow value.
    *   `waterMeterIdentifier` (string, uuid): Identifier for the water meter.
    *   `hvsFlowValue` (number, float): HVS flow value.
    *   `measurementTimestamp` (string, date-time): The timestamp of the measurement.
    *   `createdAt` (string, date-time): Creation timestamp.
    *   `updatedAt` (string, date-time): Update timestamp.

#### WaterMeterDataUpdateRequest
*   **Type:** object
*   **Required:** `measurementTimestamp`
*   **Properties:**
    *   `heatMeterIdentifier` (string, uuid): Identifier for the heat meter.
    *   `firstChannelFlowmeterIdentifier` (string, uuid): Identifier for the first channel flowmeter.
    *   `secondChannelFlowmeterIdentifier` (string, uuid): Identifier for the second channel flowmeter.
    *   `gvsFirstChannelFlowValue` (number, float): GVS first channel flow value.
    *   `gvsSecondChannelFlowValue` (number, float): GVS second channel flow value.
    *   `gvsConsumptionFlowValue` (number, float): GVS consumption flow value.
    *   `waterMeterIdentifier` (string, uuid): Identifier for the water meter.
    *   `hvsFlowValue` (number, float): HVS flow value.
    *   `measurementTimestamp` (string, date-time): The timestamp of the measurement.

```