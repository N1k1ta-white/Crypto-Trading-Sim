import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/crypto',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'crypto',
    loadComponent: () => import('./crypto/crypto.component').then(m => m.CryptoComponent),
    canActivate: [authGuard]
  },
  {
    path: 'trade',
    loadComponent: () => import('./trade/trade.component').then(m => m.TradeComponent),
    canActivate: [authGuard]
  },
  {
    path: 'holdings',
    loadComponent: () => import('./holdings/holdings.component').then(m => m.HoldingsComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/crypto'
  }
];
