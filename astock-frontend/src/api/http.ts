import axios, { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios'

export interface ApiResult<T> {
  code?: number | string
  success?: boolean
  message?: string
  msg?: string
  data?: T
  traceId?: string
}

export class ApiError extends Error {
  status?: number
  traceId?: string

  constructor(message: string, status?: number, traceId?: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.traceId = traceId
  }
}

const baseURL = import.meta.env.VITE_API_BASE_URL || ''

export const http: AxiosInstance = axios.create({
  baseURL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export async function request<T>(config: AxiosRequestConfig): Promise<T> {
  try {
    const response = await http.request<ApiResult<T> | T>(config)
    const payload = response.data as ApiResult<T>

    if (payload && typeof payload === 'object' && 'data' in payload) {
      const success = payload.success === undefined
        ? payload.code === undefined || payload.code === 0 || payload.code === '0' || payload.code === 200
        : payload.success

      if (!success) {
        throw new ApiError(payload.message || payload.msg || '接口返回失败', response.status, payload.traceId)
      }
      return payload.data as T
    }

    return response.data as T
  } catch (err) {
    const error = err as AxiosError<ApiResult<unknown>>
    if (error.response) {
      const body = error.response.data
      throw new ApiError(
        body?.message || body?.msg || error.message || '接口请求失败',
        error.response.status,
        body?.traceId
      )
    }
    if (err instanceof ApiError) {
      throw err
    }
    throw new ApiError(error.message || '网络请求失败')
  }
}
