import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { env } from '../env';
import { User } from '../models/user.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  
  private apiUrl = env.apiUrl + '/user';

  constructor(private http: HttpClient, private router: Router) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }
  
  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }
  
  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          if (response && response.token) {
            const user = {
              username: credentials.username,
              email: response.email,
              token: response.token,
              balance: response.balance
            };

            localStorage.setItem('currentUser', JSON.stringify(user));
            this.currentUserSubject.next(user);
          }
        })
      );
  }

  refreshUser(): void {
    this.http.get<User>(`${this.apiUrl}/me`).subscribe({
      next: (user) => {
        user.token = this.currentUserValue?.token;
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
      },
      error: (error) => {
        console.error('Error refreshing user:', error);
        if (error.status === 401) {
          this.logout();
          this.router.navigate(['/login']);
        }
      }
    });
  }
  
  register(userData: { username: string; password: string; email: string }): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/register`, userData);
  }
  
  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
  
  isLoggedIn(): boolean {
    return !!this.currentUserValue?.token;
  }

  get currentUser$(): Observable<User | null> {
    return this.currentUserSubject.asObservable();
  }
}