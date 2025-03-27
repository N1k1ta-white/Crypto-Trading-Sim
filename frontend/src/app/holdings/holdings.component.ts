import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { ThemeService } from '../services/theme.service';
import { HttpClient } from '@angular/common/http';
import { find, Subscription } from 'rxjs';
import { env } from '../env';
import { Holding } from '../models/holding.interface';
import { CryptoService } from '../services/crypto.service';
import { Transaction } from '../models/transactions.interface';
import { BalanceService } from '../services/balance.service';
import { AuthService } from '../services/auth.service';


@Component({
  selector: 'app-holdings',
  standalone: true,
  imports: [CommonModule, TableModule, CardModule, DialogModule, ButtonModule],
  templateUrl: './holdings.component.html',
  styleUrls: ['./holdings.component.css']
})
export class HoldingsComponent implements OnInit, OnDestroy {
  holdings: Holding[] = [];
  transactions: Transaction[] = [];
  
  darkMode = false;
  showTransactionDialog = false;
  selectedTransaction: Transaction | null = null;
  
  userBalance: number = 0;
  portfolioValue: number = 0;
  totalProfitLoss: number = 0;
  
  private themeSubscription!: Subscription;
  private cryptoPriceSubscription!: Subscription;
  
  constructor(
    private http: HttpClient,
    private themeService: ThemeService,
    private cryptoService: CryptoService,
    private balanceService: BalanceService,
    private authService: AuthService,
  ) {}
  
  ngOnInit(): void {
    this.themeSubscription = this.themeService.darkMode$.subscribe(isDark => {
      this.darkMode = isDark;
    });
    
    this.cryptoPriceSubscription = this.cryptoService.cryptoPricesUpdated$.subscribe(() => {
      this.calculatePortfolioValue();
    });
    
    this.fetchHoldings();
    this.fetchTransactions();

    this.userBalance = this.balanceService.getBalance();
  }
  
  ngOnDestroy(): void {
    if (this.themeSubscription) {
      this.themeSubscription.unsubscribe();
    }
    
    if (this.cryptoPriceSubscription) {
      this.cryptoPriceSubscription.unsubscribe();
    }
  }
  
  fetchHoldings(): void {
    this.http.get<Holding[]>(`${env.apiUrl}/holding`).subscribe({
      next: (data) => {
        this.holdings = data;
        this.calculatePortfolioValue();
      },
      error: (error) => {
        console.error('Error fetching holdings:', error);
      }
    });
  }
  
  fetchTransactions(): void {
    this.http.get<Transaction[]>(`${env.apiUrl}/trade`).subscribe({
      next: (data) => {
        this.transactions = data.sort((a, b) => 
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        this.calculateProfitLoss();
      },
      error: (error) => {
        console.error('Error fetching transactions:', error);
      }
    });
  }
  
  calculatePortfolioValue(): void {
    let total = 0;
    
    this.holdings.forEach(holding => {
      const currentPrice = this.cryptoService.getCryptoBySymbol(holding.cryptoCode + "/USD")?.last;
      
      if (currentPrice) {
        const value = holding.amount * currentPrice;
        holding.currentValue = value;
        holding.profitLoss = value - (holding.amount * holding.averagePricing);
        total += value;
      } else {
        total += holding.currentValue || (holding.amount * holding.averagePricing || 0);
      }
    });
    
    this.portfolioValue = total;
  }
  
  calculateProfitLoss(): void {
    let profit = 0;
    
    this.transactions.forEach(transaction => {
      profit += transaction.profit ?? 0;
    });

    this.totalProfitLoss = profit;
  }
  
  showTransactionDetails(transaction: Transaction): void {
    this.selectedTransaction = transaction;
    this.showTransactionDialog = true;
  }
  
  generateColorFromSymbol(symbol: string): string {
    return this.cryptoService.generateColorFromSymbol(symbol);
  }
  
  formatTimestamp(timestamp: any): string {
    if (!timestamp) return 'N/A';
    return new Date(timestamp).toLocaleString();
  }
  
  getProfitLossClass(value: number): string {
    return value > 0 ? 'positive' : value < 0 ? 'negative' : 'neutral';
  }

  refreshData(): void {
    this.fetchHoldings();
    this.fetchTransactions();
  }

  resetAccount(): void {
    if (confirm('Are you sure you want to reset your account? This will remove all holdings and set your balance to the default amount.')) {
      this.http.post<any>(`${env.apiUrl}/user/reset`, {}).subscribe({
        next: (response) => {
          this.userBalance = response.balance;
          this.refreshData();
          this.authService.refreshUser();
          
          this.portfolioValue = 0;
          this.totalProfitLoss = 0;
        },
        error: (error) => {
          console.error('Error resetting account:', error);
        }
      });
    }
  }
}