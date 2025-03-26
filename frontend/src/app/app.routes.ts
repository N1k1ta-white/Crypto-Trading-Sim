import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { TradeComponent } from './trade/trade.component';

export const routes: Routes = [
  { 
    path: 'login', 
    loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent),
  },
  { 
    path: 'register', 
    loadComponent: () => import('./auth/register/register.component').then(m => m.RegisterComponent),
  },
  { 
    path: 'crypto', 
    loadComponent: () => import('./crypto/crypto.component').then(m => m.CryptoComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'trade', 
    component: TradeComponent 
  },
  { 
    path: '', 
    redirectTo: '/crypto', 
    pathMatch: 'full' 
  },
  { 
    path: '**', 
    redirectTo: '/login' 
  }
];
