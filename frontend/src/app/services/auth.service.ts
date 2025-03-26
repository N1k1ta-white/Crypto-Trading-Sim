import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { env } from '../env';

export interface User {
  id?: number;
  username: string;
  email: string;
  token?: string;
}

export interface AuthCredentials {
  username: string;
  password: string;
  email?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  private apiUrl = 'http://localhost:5000/api/user';
  
  constructor(private http: HttpClient) {
    // Check if there's a stored token on initialization
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
              email: '', // The backend might return this
              token: response.token
            };
            localStorage.setItem('currentUser', JSON.stringify(user));
            this.currentUserSubject.next(user);
          }
        })
      );
  }
  
  register(userData: { username: string; password: string; email: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, userData);
  }
  
  logout(): void {
    // Remove user from local storage and reset current user
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
  
  isLoggedIn(): boolean {
    return !!this.currentUserValue?.token;
  }
}