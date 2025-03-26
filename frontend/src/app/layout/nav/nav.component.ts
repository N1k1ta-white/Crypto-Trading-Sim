import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';

// Import PrimeNG components
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
export class NavComponent {
  menuItems: MenuItem[] = [];
  darkMode = false;
  
  constructor(
    public authService: AuthService,
    private themeService: ThemeService,
    private router: Router
  ) {
    this.themeService.darkMode$.subscribe(isDark => {
      this.darkMode = isDark;
      this.updateMenuItems();
    });
    this.updateMenuItems();
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
          routerLink: '/trading'
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
}