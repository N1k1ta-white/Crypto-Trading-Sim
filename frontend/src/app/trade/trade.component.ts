import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Subscription, timer } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ThemeService } from '../services/theme.service';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { env } from '../env';
import { Crypto } from '../entity/crypto.entity';
import { TradeRequest } from '../models/trade-request.interface';

@Component({
  selector: 'app-trade',
  templateUrl: './trade.component.html',
  styleUrls: ['./trade.component.css'],
  imports: [
    ReactiveFormsModule, 
    CommonModule, 
    CardModule, 
    ButtonModule, 
    DropdownModule, 
    InputNumberModule,
    ToastModule
  ],
  providers: [MessageService]
})
export class TradeComponent implements OnInit, OnDestroy {
  tradeForm: FormGroup;
  price: number | null = null;
  countdown: number = 10;
  successMessageVisible = false;
  darkMode = false;
  
  cryptoOptions: { label: string, value: string }[] = [];
  
  tradeTypeOptions = [
    { label: 'Buy', value: 'BUY' },
    { label: 'Sell', value: 'SELL' }
  ];
  
  private countdownSubscription!: Subscription;
  private themeSubscription!: Subscription;
  selectedCryptoData: Crypto | null = null;

  constructor(
    private fb: FormBuilder, 
    private http: HttpClient,
    private themeService: ThemeService,
    private messageService: MessageService
  ) {
    this.tradeForm = this.fb.group({
      crypto: ['', Validators.required],
      tradeType: ['BUY', Validators.required],
      amount: [0, [Validators.required, Validators.min(0.01)]] // Changed min value to 0.01
    });

    // Subscribe to crypto selection changes
    this.tradeForm.get('crypto')?.valueChanges.subscribe(crypto => {
      if (crypto) {
        this.fetchCryptoData(crypto);
      } else {
        this.selectedCryptoData = null;
      }
    });
  }

  ngOnInit(): void {
    // Subscribe to theme service
    this.themeSubscription = this.themeService.darkMode$.subscribe(isDark => {
      this.darkMode = isDark;
    });
    
    // Fetch available cryptocurrencies from the backend
    this.fetchCryptocurrencies();
  }
  
  fetchCryptocurrencies(): void {
    this.http.get<Crypto[]>(`${env.apiUrl}/crypto`).subscribe({
      next: (cryptos) => {
        this.cryptoOptions = cryptos.map(crypto => {
          // Remove "/USD" from symbol if present
          const cleanSymbol = crypto.symbol.replace('/USD', '');
          return {
            label: cleanSymbol,
            value: cleanSymbol
          };
        });
        console.log('Fetched crypto options:', this.cryptoOptions);
      },
      error: (error) => {
        console.error('Error fetching cryptocurrencies:', error);
        this.showErrorToast(
          'Failed to Load Cryptocurrencies',
          'The system couldn\'t retrieve the list of available cryptocurrencies. Please check your connection and try again.',
          5000
        );
      }
    });
  }

  ngOnDestroy(): void {
    if (this.countdownSubscription) {
      this.countdownSubscription.unsubscribe();
    }
    if (this.themeSubscription) {
      this.themeSubscription.unsubscribe();
    }
  }

  /**
   * Fetch data for the selected cryptocurrency
   */
  fetchCryptoData(symbol: string): void {
    this.http.get<Crypto>(`${env.apiUrl}/crypto/${symbol}`).subscribe({
      next: (data) => {
        this.selectedCryptoData = data;
        console.log('Fetched crypto data:', data);
      },
      error: (error) => {
        console.error(`Error fetching data for ${symbol}:`, error);
        this.showErrorToast(
          `Failed to Load ${symbol} Data`,
          `We couldn't retrieve the latest information for ${symbol}. Please try selecting it again.`,
          4000
        );
        this.selectedCryptoData = null;
      }
    });
  }

  fetchPrice(): void {
    const crypto = this.tradeForm.get('crypto')?.value;
    if (!crypto) return;

    // Instead of custom API call, use the already fetched data if available
    if (this.selectedCryptoData && this.selectedCryptoData.symbol === crypto) {
      this.price = this.selectedCryptoData.last;
      this.startCountdown();
      return;
    }

    // Otherwise, fetch the price
    this.http.get<Crypto>(`${env.apiUrl}/crypto/${crypto}`).subscribe({
      next: (data) => {
        this.price = data.last;
        this.selectedCryptoData = data; // Update the selected crypto data
        this.startCountdown();
      },
      error: (error) => {
        this.price = null;
        this.showErrorToast(
          'Price Fetch Failed',
          `We couldn't retrieve the current market price for ${crypto}. Please try again in a moment.`,
          4000
        );
      }
    });
  }

  startCountdown(): void {
    this.countdown = 10;
    
    // Clear any existing subscription to avoid multiple timers
    if (this.countdownSubscription) {
      this.countdownSubscription.unsubscribe();
    }
    
    this.countdownSubscription = timer(0, 1000).subscribe((seconds) => {
      this.countdown = 10 - seconds;
      if (this.countdown <= 0) {
        this.countdownSubscription.unsubscribe();
        // Reset price when the timer reaches zero
        this.price = null;
        // Show a toast notification to inform the user
        this.messageService.add({
          severity: 'info',
          summary: 'Price Expired',
          detail: 'The quoted price has expired. Please fetch a new price to continue.',
          life: 3000
        });
      }
    });
  }

  confirmPurchase(): void {
    if (!this.price) return;

    const tradeData = this.tradeForm.value;
    const tradeAction = tradeData.tradeType === 'BUY' ? 'Purchase' : 'Sale';
    const cryptoSymbol = tradeData.crypto;
    const amount = tradeData.amount;
    
    // Create proper trade request object using the interface
    const tradeRequest: TradeRequest = {
      symbol: cryptoSymbol,
      tradeType: tradeData.tradeType,
      fixedPrice: this.price,
      amount: amount
    };
    
    // The auth interceptor will automatically add the token
    this.http.post(`${env.apiUrl}/trade`, tradeRequest).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: `${tradeAction} Successful!`,
          detail: `You have successfully ${tradeData.tradeType === 'BUY' ? 'purchased' : 'sold'} 
            ${amount} ${cryptoSymbol} at $${this.price}.`,
          life: 5000
        });
        this.price = null;
        this.tradeForm.get('amount')?.setValue(0);
      },
      error: (error) => {
        console.error('Trade error:', error);
        let errorMsg = `Your ${tradeAction.toLowerCase()} of ${amount} ${cryptoSymbol} could not be processed.`;
        
        this.showErrorToast(`${tradeAction} Failed`, errorMsg + error.message, 5000);
      }
    });
  }
  
  /**
   * Helper method to show error toast with consistent styling
   */
  showErrorToast(summary: string, detail: string, duration: number = 4000): void {
    this.messageService.add({
      severity: 'error',
      summary: summary,
      detail: detail,
      life: duration,
      styleClass: 'error-toast'
    });
  }
}
