import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private darkModeKey = 'darkMode';
  private darkModeSubject = new BehaviorSubject<boolean>(this.initializeDarkMode());
  
  darkMode$ = this.darkModeSubject.asObservable();
  
  constructor() {
    // Apply theme on service instantiation
    this.applyTheme(this.darkModeSubject.value);
  }
  
  toggleTheme(): void {
    const newValue = !this.darkModeSubject.value;
    this.setDarkMode(newValue);
  }
  
  setDarkMode(isDark: boolean): void {
    this.darkModeSubject.next(isDark);
    localStorage.setItem(this.darkModeKey, String(isDark));
    this.applyTheme(isDark);
  }
  
  private initializeDarkMode(): boolean {
    const savedPreference = localStorage.getItem(this.darkModeKey);
    
    if (savedPreference !== null) {
      return savedPreference === 'true';
    }
    
    // If no saved preference, check system preference
    return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
  }
  
  private applyTheme(isDark: boolean): void {
    // Apply theme to document body
    document.body.classList.toggle('dark-theme', isDark);
  }
}