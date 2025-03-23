import { Component, OnInit } from '@angular/core';
import { WebSocketService } from '../services/web-socket.service';
import { Crypto } from '../entity/crypto.entity';
import { HttpClient } from '@angular/common/http';
import { env } from '../env';
import { KeyValuePipe } from '@angular/common';

@Component({
  selector: 'app-crypto',
  templateUrl: './crypto.component.html',
  imports: [KeyValuePipe]
})
export class CryptoComponent implements OnInit {
  messages: string[] = [];

  protected crypto = new Map<string, Crypto>();

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
      const crypto: Crypto = JSON.parse(msg);
      this.crypto.set(crypto.symbol, crypto);
    });
  }
}
