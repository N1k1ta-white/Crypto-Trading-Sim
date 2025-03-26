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

export class AppComponent {}
