import axios from "axios";
import type { AxiosInstance } from "axios";
const API_BASE_URL = "http://158.160.147.123:8083";

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// apiClient.interceptors.response.use(
//   (response) => response,
//   (error) => {
//
//     console.error("API Error:", error);
//     return Promise.reject(error);
//   },
// );

export { apiClient };
