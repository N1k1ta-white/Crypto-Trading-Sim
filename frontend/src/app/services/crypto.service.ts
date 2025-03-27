import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { WebSocketService } from './web-socket.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { Crypto } from '../entity/crypto.entity';
import { env } from '../env';

@Injectable({
  providedIn: 'root'
})
export class CryptoService {
  private crypto = new Map<string, Crypto>();
  private priceChangeStatus = new Map<string, 'up' | 'down' | null>();
  private cryptoUpdated = new BehaviorSubject<boolean>(false);
  
  constructor(
    private http: HttpClient,
    private webSocketService: WebSocketService
  ) {
    this.initializeWebSocketListeners();
  }
  
  private initializeWebSocketListeners(): void {
    this.webSocketService.getMessages().subscribe((msg: string | null) => {
      if (!msg) return;
      this.handleCryptoUpdate(JSON.parse(msg));
    });
    
    let previousConnectionState = false;
    this.webSocketService.getConnectionStatus().subscribe(status => {
      if (!previousConnectionState && status) {
        console.log('WebSocket connection restored - fetching latest data');
        this.fetchCryptoData();
      }
      previousConnectionState = status;
    });
  }
  
  private handleCryptoUpdate(newData: Crypto): void {
    const currentCrypto = this.crypto.get(newData.symbol);
    if (currentCrypto) {
      if (newData.last > currentCrypto.last) {
        this.priceChangeStatus.set(newData.symbol, 'up');
      } else if (newData.last < currentCrypto.last) {
        this.priceChangeStatus.set(newData.symbol, 'down');
      }
      
      setTimeout(() => {
        this.priceChangeStatus.set(newData.symbol, null);
        this.cryptoUpdated.next(true);
      }, 1000);
    }
    
    this.crypto.set(newData.symbol, newData);
    this.cryptoUpdated.next(true);
  }
  
  public fetchCryptoData(): void {
    this.http.get<Crypto[]>(env.apiUrl + '/crypto').subscribe({
      next: (data) => {
        data.forEach(crypto => {
          this.crypto.set(crypto.symbol, crypto);
          this.priceChangeStatus.set(crypto.symbol, null);
        });
        this.cryptoUpdated.next(true);
      },
      error: (error) => {
        console.error('Error fetching crypto data:', error);
      }
    });
  }
  
  public getCryptoList(): Crypto[] {
    return Array.from(this.crypto.values());
  }
  
  public getCryptoBySymbol(symbol: string): Crypto | undefined {
    return this.crypto.get(symbol);
  }
  
  public getPriceChangeStatus(symbol: string): string {
    const status = this.priceChangeStatus.get(symbol);
    return status ? status : '';
  }
  
  public getConnectionStatus(): Observable<boolean> {
    return this.webSocketService.getConnectionStatus();
  }
  
  public getCryptoUpdates(): Observable<boolean> {
    return this.cryptoUpdated.asObservable();
  }
  
  public generateColorFromSymbol(symbol: string): string {
    let hash = 0;
    for (let i = 0; i < symbol.length; i++) {
      hash = symbol.charCodeAt(i) + ((hash << 5) - hash);
    }
    
    const saturation = 75 + (Math.abs(hash) % 20); // 75-95%
    const lightness = 45 + (Math.abs(hash) % 10);  // 45-55%
    const hue = Math.abs(hash) % 360;              // 0-359 degrees
    
    return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
  }
  
  public abs(num: number): number {
    return Math.abs(num);
  }
}