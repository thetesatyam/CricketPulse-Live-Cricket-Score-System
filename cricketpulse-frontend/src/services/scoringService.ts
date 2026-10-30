import api from './api';
import { ApiResponse, DeliveryRequest, DeliveryResponse } from '../types';

export const scoringService = {
  recordDelivery: async (inningsId: number, payload: DeliveryRequest): Promise<DeliveryResponse> => {
    const res = await api.post<ApiResponse<DeliveryResponse>>(`/scoring/innings/${inningsId}/deliveries`, payload);
    return res.data.data;
  },

  applyCorrection: async (deliveryId: number, reason: string): Promise<DeliveryResponse> => {
    const res = await api.post<ApiResponse<DeliveryResponse>>('/scoring/deliveries/corrections', { deliveryId, reason });
    return res.data.data;
  }
};
