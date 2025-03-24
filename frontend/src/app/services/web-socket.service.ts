import { Injectable } from '@angular/core';
import { consumerPollProducersForChange } from '@angular/core/primitives/signals';
import { Client } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private stompClient: Client | null = null;
  private messageSubject = new BehaviorSubject<string | null>(null);
  private sessionId: string | null = null;
  // Add connection status subject
  private connectionStatusSubject = new BehaviorSubject<boolean>(false);

  constructor() {
    this.connect();
  }

  private connect() {
    this.stompClient = new Client({
        brokerURL: 'ws://localhost:5000/ws',
        connectHeaders: {},
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
    });

    this.stompClient.onConnect = async (_frame) => {
        console.log('Connected to WebSocket');
        // Set status to connected
        this.connectionStatusSubject.next(true);

        const uuid = crypto.randomUUID();
        this.sessionId = uuid;

        // Connect to backend by sending the session ID
        const res = await this.stompClient?.publish({
            destination: '/app/connect',
            body: JSON.stringify({ sessionId: uuid }),
            headers: { 'content-type': 'application/json' }
        });

        console.log(res)

        // Extract a proper session ID from the connection
        console.log("Extracted session ID:", this.sessionId);

        // Subscribe to the user-specific crypto topic
        if (this.sessionId) {
            this.stompClient?.subscribe(`/update/crypto`, (message) => {
            console.log("Received:", message.body);
            this.messageSubject.next(message.body);
            });
        }
    }

    this.stompClient.onStompError = (frame) => {
        console.error('STOMP error', frame);
        this.connectionStatusSubject.next(false);
    };
            
    this.stompClient.onWebSocketClose = () => {
        console.warn('WebSocket disconnected');
        // Set status to disconnected
        this.connectionStatusSubject.next(false);
    };

    this.stompClient.activate();
  }

  getMessages(): Observable<string | null> {
    return this.messageSubject.asObservable();
  }

  // Add method to get connection status
  getConnectionStatus(): Observable<boolean> {
    return this.connectionStatusSubject.asObservable();
  }
}
