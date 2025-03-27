import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { Subscription } from 'rxjs';

import { ButtonModule } from 'primeng/button';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule, RouterLink, ButtonModule, MenubarModule],
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent implements OnInit, OnDestroy {
  menuItems: MenuItem[] = [];
  darkMode = false;
  userBalance = 0;
  private authSubscription!: Subscription;
  private themeSubscription!: Subscription;
  private userSubscription!: Subscription;
  
  constructor(
    public authService: AuthService,
    private themeService: ThemeService,
    private router: Router,
  ) {}
  
  ngOnInit(): void {
    this.themeSubscription = this.themeService.darkMode$.subscribe(isDark => {
      this.darkMode = isDark;
      this.updateMenuItems();
    });

    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.userBalance = user?.balance || 0;
    });
    
    this.authSubscription = this.authService.currentUser$.subscribe(() => {
      this.updateMenuItems();
    });
    
    this.updateMenuItems();
  }
  
  ngOnDestroy(): void {
    if (this.themeSubscription) {
      this.themeSubscription.unsubscribe();
    }
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  updateMenuItems() {
    if (this.authService.isLoggedIn()) {
      this.menuItems = [
        {
          label: 'Dashboard',
          icon: 'pi pi-home',
          routerLink: '/crypto'
        },
        {
          label: 'Portfolio',
          icon: 'pi pi-chart-pie',
          routerLink: '/portfolio'
        },
        {
          label: 'Trading',
          icon: 'pi pi-chart-line',
          routerLink: '/trade'
        }
      ];
    } else {
      this.menuItems = [];
    }
  }
  
  toggleTheme() {
    this.themeService.toggleTheme();
  }
  
  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
    this.updateMenuItems();
  }

  getUserInitials(): string {
    const username = this.getUsername();
    return username ? username.charAt(0).toUpperCase() : 'U';
  }

  getUsername(): string {
    return this.authService.currentUserValue?.username || 'User';
  }

  getUserBalance(): number {
    return this.userBalance;
  }
}