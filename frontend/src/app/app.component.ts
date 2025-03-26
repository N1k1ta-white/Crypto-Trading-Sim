import { Component, OnInit, Renderer2 } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ThemeService } from './services/theme.service';
import { NavComponent } from './layout/nav/nav.component';

@Component({
  selector: 'app-root',
  template: `
    <app-nav></app-nav>
    <div class="container">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .container {
      padding: 1rem;
    }
  `],
  standalone: true,
  imports: [RouterOutlet, FormsModule, CommonModule, NavComponent]
})

export class AppComponent implements OnInit {
  title = 'crypto-trading-sim';
  userBalance = 10000; // This would come from a user service
  darkMode = false;
  
  // Quick trade properties
  quickTradeSymbol: string = '';
  quickTradeType: string = 'BUY';
  quickTradeAmount: number | null = null;
  
  // Example data - in a real app, this would come from a service
  availableCryptos = [
    { symbol: 'BTC' },
    { symbol: 'ETH' },
    { symbol: 'XRP' },
    { symbol: 'SOL' },
    { symbol: 'ADA' }
  ];
  
  constructor(private themeService: ThemeService, private renderer: Renderer2) {}
  
  ngOnInit() {
    // Subscribe to theme changes from the service
    this.themeService.darkMode$.subscribe(isDark => {
      this.darkMode = isDark;
    });
    
    // Check user preference from localStorage
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.darkMode = true;
      this.renderer.addClass(document.body, 'dark-theme');
    }
  }
  
  toggleTheme() {
    this.themeService.toggleTheme();
  }
  
  executeQuickTrade() {
    if (!this.quickTradeSymbol || !this.quickTradeAmount || this.quickTradeAmount <= 0) {
      // Show error message or notification
      console.log('Please fill all required fields');
      return;
    }
    
    // This is where you would call your trading service
    console.log('Executing trade:', {
      symbol: this.quickTradeSymbol,
      type: this.quickTradeType,
      amount: this.quickTradeAmount
    });
    
    // Reset form after submission
    this.quickTradeAmount = null;
  }
}
