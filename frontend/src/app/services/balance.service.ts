import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../models/user.interface';
import { HttpClient } from '@angular/common/http';
import { env } from '../env';

@Injectable({
    providedIn: 'root'
})
export class BalanceService {
    private balanceSubject = new BehaviorSubject<number>(0);
    public balance$: Observable<number> = this.balanceSubject.asObservable();

    constructor(
        private authService: AuthService,
        private http: HttpClient
    ) {
        this.authService.currentUser$.subscribe((user: User | null) => {
            if (user) {
                this.updateBalanceFromAuth(user.balance);
            } else {
                this.balanceSubject.next(0);
            }
        });
    }

    private updateBalanceFromAuth(balance: number | undefined): void {
        if (balance !== undefined) {
            this.balanceSubject.next(balance);
        } else {
            this.balanceSubject.next(0);
        }
    }

    getBalance(): number {
        return this.balanceSubject.value;
    }
    
    updateBalance(newBalance: number): void {
        this.balanceSubject.next(newBalance);
    }
}