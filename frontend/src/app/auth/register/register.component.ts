import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { PasswordModule } from 'primeng/password';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ThemeService } from '../../services/theme.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    InputTextModule, 
    ButtonModule, 
    CardModule, 
    PasswordModule,
    ToastModule,
    RouterLink
  ],
  providers: [MessageService],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  isLoading = false;
  
  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService,
    private themeService: ThemeService
  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }
  
  
  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }
    
    this.isLoading = true;
    
    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        this.isLoading = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Registration Successful',
          detail: 'Your account has been created! You will be redirected to login page.',
          life: 5000
        });
        // Navigate after a delay to show the success message
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: (error) => {
        this.isLoading = false;
        this.showErrorToast(
          'Registration Failed',
          error.message || 'Could not create account. Please try again later.',
          5000
        );
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