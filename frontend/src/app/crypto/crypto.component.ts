import { Component, OnInit } from '@angular/core';
import { WebSocketService } from '../services/web-socket.service';
import { Crypto } from '../entity/crypto.entity';
import { HttpClient } from '@angular/common/http';
import { env } from '../env';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-crypto',
  templateUrl: './crypto.component.html',
  styleUrls: ['./crypto.component.css'],
  imports: [TableModule, CommonModule]
})
export class CryptoComponent implements OnInit {
  messages: string[] = [];
  protected crypto = new Map<string, Crypto>();
  priceChangeStatus = new Map<string, 'up' | 'down' | null>();
  
  constructor(
    private webSocketService: WebSocketService,
    private http: HttpClient
  ) {}
  
  ngOnInit(): void {
    // Fetch initial crypto data
    this.http.get<Crypto[]>(env.apiUrl + '/crypto').subscribe({
      next: (data) => {
        console.log('Fetched crypto data:', data);
        data.forEach(crypto => {
          this.crypto.set(crypto.symbol, crypto);
          this.priceChangeStatus.set(crypto.symbol, null);
        });
      },
      error: (error) => {
        console.error('Error fetching crypto data:', error);
      }
    });

    console.log("Map: ", this.crypto)

    console.log('Subscribing to messages');
    this.webSocketService.getMessages().subscribe((msg : string | null) => {
      if (!msg) return;
      const newData: Crypto = JSON.parse(msg);
      
      // Compare with previous price if it exists
      const currentCrypto = this.crypto.get(newData.symbol);
      if (currentCrypto) {
        if (newData.last > currentCrypto.last) {
          this.priceChangeStatus.set(newData.symbol, 'up');
        } else if (newData.last < currentCrypto.last) {
          this.priceChangeStatus.set(newData.symbol, 'down');
        }
        
        // Reset the status after a delay
        setTimeout(() => {
          this.priceChangeStatus.set(newData.symbol, null);
        }, 1000);
      }
      
      // Update the data
      this.crypto.set(newData.symbol, newData);
    });
  }

  get cryptoArray(): Crypto[] {
    return Array.from(this.crypto.values());
  }
  
  getPriceChangeStatus(symbol: string): string {
    const status = this.priceChangeStatus.get(symbol);
    return status ? status : '';
  }
}
