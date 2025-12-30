import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen flex flex-col items-center justify-center bg-gray-100">
      <h1 class="text-3xl font-bold mb-4">Welcome 🎉</h1>

      <p class="mb-6 text-gray-700">
        You are successfully logged in.
      </p>

      <button
        (click)="logout()"
        class="bg-red-500 text-white px-6 py-2 rounded hover:bg-red-600 transition">
        Logout
      </button>
    </div>
  `
})
export class MainComponent {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
