import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthRequest, AuthResponse } from '../model/auth.models';
import { UserDTO } from '../model/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = 'http://localhost:8080/api/auth'; 
  private userKey = 'user';

  constructor(private http: HttpClient) {}

  signup(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/signup`, request);
  }

  signin(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/signin`, request);
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  saveUser(user: UserDTO) {
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }

  getUser(): UserDTO | null {
    const raw = localStorage.getItem(this.userKey);
    return raw ? (JSON.parse(raw) as UserDTO) : null;
  }

  getUserId(): number | null {
    return this.getUser()?.id ?? null;
  }

  logout(): void {
    localStorage.removeItem('token');
  }
}
