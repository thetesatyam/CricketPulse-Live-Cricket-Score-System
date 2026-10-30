import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { DeliveryResponse } from '../types';

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws';

class WebSocketService {
  private client: Client | null = null;
  private connected: boolean = false;
  private subscribers: Map<string, number> = new Map();

  public connect(onConnected?: () => void) {
    if (this.client && this.connected) {
      if (onConnected) onConnected();
      return;
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        this.connected = true;
        if (onConnected) onConnected();
      },
      onDisconnect: () => {
        this.connected = false;
      },
      onStompError: (frame) => {
        console.error('STOMP protocol error:', frame.headers['message']);
      }
    });

    this.client.activate();
  }

  public subscribeToMatch(
    matchId: number,
    onScoreUpdate: (update: DeliveryResponse) => void,
    onCommentaryUpdate?: (update: DeliveryResponse) => void
  ): () => void {
    if (!this.client) {
      this.connect();
    }

    let scoreSub: any = null;
    let commSub: any = null;

    const setupSubscriptions = () => {
      if (!this.client || !this.client.connected) return;

      scoreSub = this.client.subscribe(`/topic/matches/${matchId}/score`, (message) => {
        try {
          const data: DeliveryResponse = JSON.parse(message.body);
          onScoreUpdate(data);
        } catch (e) {
          console.error('Error parsing score update message', e);
        }
      });

      if (onCommentaryUpdate) {
        commSub = this.client.subscribe(`/topic/matches/${matchId}/commentary`, (message) => {
          try {
            const data: DeliveryResponse = JSON.parse(message.body);
            onCommentaryUpdate(data);
          } catch (e) {
            console.error('Error parsing commentary message', e);
          }
        });
      }
    };

    if (this.connected && this.client?.connected) {
      setupSubscriptions();
    } else {
      // Connect and subscribe once ready
      this.connect(setupSubscriptions);
    }

    return () => {
      if (scoreSub) scoreSub.unsubscribe();
      if (commSub) commSub.unsubscribe();
    };
  }

  public isConnected(): boolean {
    return this.connected;
  }
}

export const websocketService = new WebSocketService();
export default websocketService;
