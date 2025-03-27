import { Component, OnInit, OnDestroy } from '@angular/core';
import { Crypto } from '../models/crypto.interface';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ThemeService } from '../services/theme.service';
import { CryptoService } from '../services/crypto.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-crypto',
  templateUrl: './crypto.component.html',
  styleUrls: ['./crypto.component.css'],
  imports: [TableModule, CommonModule, DialogModule]
})
export class CryptoComponent implements OnInit, OnDestroy {
  selectedCrypto: Crypto | null = null;
  showDetailDialog = false;
  isConnected = false;
  darkMode = false;
  
  private themeSubscription!: Subscription;
  private connectionSubscription!: Subscription;
  private cryptoUpdateSubscription!: Subscription;

  constructor(
    private cryptoService: CryptoService,
    private themeService: ThemeService
  ) {}
  
  ngOnInit(): void {
    this.themeSubscription = this.themeService.darkMode$.subscribe(isDark => {
      this.darkMode = isDark;
    });
    
    this.connectionSubscription = this.cryptoService.getConnectionStatus().subscribe(status => {
      this.isConnected = status;
    });
  }

  ngOnDestroy(): void {
    if (this.themeSubscription) {
      this.themeSubscription.unsubscribe();
    }
    if (this.connectionSubscription) {
      this.connectionSubscription.unsubscribe();
    }
    if (this.cryptoUpdateSubscription) {
      this.cryptoUpdateSubscription.unsubscribe();
    }
  }

  get cryptoArray(): Crypto[] {
    return this.cryptoService.getCryptoList();
  }
  
  getPriceChangeStatus(symbol: string): string {
    return this.cryptoService.getPriceChangeStatus(symbol);
  }

  showDetails(crypto: Crypto): void {
    this.selectedCrypto = crypto;
    this.showDetailDialog = true;
  }
  
  formatTimestamp(timestamp: number): string {
    if (!timestamp) return 'N/A';
    return new Date(timestamp).toLocaleString();
  }

  generateColorFromSymbol(symbol: string): string {
    return this.cryptoService.generateColorFromSymbol(symbol);
  }

  abs(num: number): number {
    return this.cryptoService.abs(num);
  }
}
