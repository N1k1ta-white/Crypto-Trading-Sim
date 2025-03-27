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
import { AuthService } from '../services/auth.service';
import { CryptoService } from '../services/crypto.service';

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
  approxPrice: number | null = null;
  
  cryptoOptions: { label: string, value: string }[] = [];
  
  tradeTypeOptions = [
    { label: 'Buy', value: 'BUY' },
    { label: 'Sell', value: 'SELL' }
  ];
  
  private countdownSubscription!: Subscription;
  private themeSubscription!: Subscription;
  private cryptoSubscription!: Subscription;
  selectedCryptoData: Crypto | null = null;

  maxAmount: number | null = null;
  userBalance: number = 0;
  userHoldings: {[key: string]: number} = {};

  constructor(
    private fb: FormBuilder, 
    private http: HttpClient,
    private themeService: ThemeService,
    private messageService: MessageService,
    private authService: AuthService,
    private cryptoService: CryptoService
    ) {
    this.tradeForm = this.fb.group({
      crypto: ['', Validators.required],
      tradeType: ['BUY', Validators.required],
      amount: [0, [Validators.required, Validators.min(0.01)]] // Changed min value to 0.01
    });

    this.tradeForm.get('crypto')?.valueChanges.subscribe(crypto => {
      if (crypto) {
        this.fetchCryptoData(crypto);
      } else {
        this.selectedCryptoData = null;
      }
    });

    this.cryptoSubscription = this.cryptoService.getCryptoUpdates().subscribe(() => {
      this.approxPrice = this.selectedCryptoData?.last ?? null;
    });
  }

  ngOnInit(): void {
    // Subscribe to theme service
    this.themeSubscription = this.themeService.darkMode$.subscribe(isDark => {
      this.darkMode = isDark;
    });
    
    // Fetch available cryptocurrencies from the backend
    this.fetchCryptocurrencies();

    // Get user balance
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.userBalance = user.balance ?? 0;
      }
    });
    
    // Fetch user's holdings
    this.fetchUserHoldings();
    
    // Subscribe to form value changes to update max amounts
    this.tradeForm.get('crypto')?.valueChanges.subscribe(crypto => {
      this.updateMaxAmount();
    });
    
    this.tradeForm.get('tradeType')?.valueChanges.subscribe(tradeType => {
      this.updateMaxAmount();
    });
  }
  
  fetchCryptocurrencies(): void {
    this.http.get<Crypto[]>(`${env.apiUrl}/crypto`).subscribe({
      next: (cryptos) => {
          this.cryptoOptions = cryptos.map(crypto => {

          const cleanSymbol = crypto.symbol.replace('/USD', '');
          return {
            label: cleanSymbol,
            value: cleanSymbol
          };
        });
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

  fetchCryptoData(symbol: string): void {
    this.http.get<Crypto>(`${env.apiUrl}/crypto/${symbol}`).subscribe({
      next: (data) => {
        this.selectedCryptoData = data;

        this.approxPrice = data.last;

        this.updateMaxAmount();
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
        this.updateMaxAmount(); // Update max amount when price is fetched
        this.startCountdown();
      },
      error: (error) => {
        this.price = null;
        this.showErrorToast(
          'Price Fetch Failed',
          `We couldn't retrieve the current market price for ${crypto}. Please try again in a moment.
          + Error: ${error.message}`,
          4000
        );
      }
    });
  }

  startCountdown(): void {
    this.countdown = 10;
    
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
    
    const tradeRequest: TradeRequest = {
      symbol: cryptoSymbol,
      tradeType: tradeData.tradeType,
      fixedPrice: this.price,
      amount: amount
    };

    this.countdownSubscription.unsubscribe();
    
    this.http.post(`${env.apiUrl}/trade`, tradeRequest).subscribe({
      next: () => {
        this.authService.refreshUser();
        this.fetchUserHoldings();
        
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
        let errorMsg = `Your ${tradeAction.toLowerCase()} of ${amount} ${cryptoSymbol} could not be processed.`;
        
        this.showErrorToast(`${tradeAction} Failed`, errorMsg + error.message, 5000);
      }
    });
  }
  
  showErrorToast(summary: string, detail: string, duration: number = 4000): void {
    this.messageService.add({
      severity: 'error',
      summary: summary,
      detail: detail,
      life: duration,
      styleClass: 'error-toast'
    });
  }

  fetchUserHoldings() {
    this.http.get<any>(`${env.apiUrl}/holdings`).subscribe({
      next: (holdings) => {
        // Create a map of crypto code to amount
        this.userHoldings = {};
        holdings.forEach((holding: any) => {
          this.userHoldings[holding.crypto] = +holding.amount;
        });
        this.updateMaxAmount();
      },
      error: (error) => {
        console.error('Error fetching holdings', error);
      }
    });
  }
  
  updateMaxAmount() {
    const crypto = this.tradeForm.get('crypto')?.value;
    const tradeType = this.tradeForm.get('tradeType')?.value;
    
    if (!crypto) {
      this.maxAmount = null;
      return;
    }
    
    if (tradeType === 'BUY') {
      if (this.approxPrice && this.approxPrice > 0 && this.userBalance > 0) {
        // Calculate max amount user can buy with their balance
        // Reduce slightly to account for price fluctuations
        this.maxAmount = this.userBalance / this.approxPrice * 0.99;
      } else {
        this.maxAmount = null;
      }
    } else if (tradeType === 'SELL') {
      // Get the amount of this crypto that the user owns
      const cryptoCode = crypto.replace(/\/.*$/, ''); // Remove the trading pair suffix if any
      this.maxAmount = this.userHoldings[cryptoCode] || 0;
    }
  }
  
  setMaxAmount() {
    if (this.maxAmount && this.maxAmount > 0) {
      this.tradeForm.get('amount')?.setValue(this.maxAmount);
    }
  }
}
