import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebSocketService } from '../services/web-socket.service';
import { Crypto } from '../entity/crypto.entity';
import { HttpClient } from '@angular/common/http';
import { env } from '../env';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'app-crypto',
  templateUrl: './crypto.component.html',
  styleUrls: ['./crypto.component.css'],
  imports: [TableModule, CommonModule, DialogModule]
})
export class CryptoComponent implements OnInit, OnDestroy {
  messages: string[] = [];
  protected crypto = new Map<string, Crypto>();
  priceChangeStatus = new Map<string, 'up' | 'down' | null>();
  
  // Add these new properties
  selectedCrypto: any = null;
  showDetailDialog: boolean = false;
  isConnected: boolean = false;

  constructor(
    private webSocketService: WebSocketService,
    private http: HttpClient
  ) {}
  
  ngOnInit(): void {
    // Fetch initial crypto data
    this.fetchCryptoData();

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

    // Subscribe to connection status with previous state tracking
    let previousConnectionState = false;
    this.webSocketService.getConnectionStatus().subscribe(status => {
      // Check if this is a reconnection (was disconnected, now connected)
      if (!previousConnectionState && status) {
        console.log('WebSocket connection restored - fetching latest data');
        this.fetchCryptoData();
      }
      
      // Update state tracking
      previousConnectionState = status;
      this.isConnected = status;
    });

    const themeToggle = document.getElementById('theme-toggle') as HTMLInputElement;

    themeToggle.addEventListener('change', () => {
      document.body.classList.toggle('dark-theme', themeToggle.checked);
    });

    // Optional: Persist theme preference in localStorage
    const isDarkMode = localStorage.getItem('dark-theme') === 'true';
    themeToggle.checked = isDarkMode;
    document.body.classList.toggle('dark-theme', isDarkMode);

    themeToggle.addEventListener('change', () => {
      const isDark = themeToggle.checked;
      document.body.classList.toggle('dark-theme', isDark);
      localStorage.setItem('dark-theme', String(isDark));
    });
  }

  get cryptoArray(): Crypto[] {
    return Array.from(this.crypto.values());
  }
  
  getPriceChangeStatus(symbol: string): string {
    const status = this.priceChangeStatus.get(symbol);
    return status ? status : '';
  }

  // Add this method to handle row click
  showDetails(crypto: any): void {
    this.selectedCrypto = crypto;
    this.showDetailDialog = true;
  }
  
  // Add a method to format the timestamp for display
  formatTimestamp(timestamp: number): string {
    if (!timestamp) return 'N/A';
    return new Date(timestamp).toLocaleString();
  }

  // Extract fetch logic to a reusable method
  private fetchCryptoData(): void {
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
  }

  ngOnDestroy(): void {
    // Cleanup logic if needed
  }
}
