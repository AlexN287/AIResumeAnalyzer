import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {

  loginForm: FormGroup;
  submitted = false;

constructor(
  private fb: FormBuilder,
  private authService: AuthService,
  private router: Router
) {
  if (this.authService.getToken()) {
    this.router.navigate(['/main']);
  }

  this.loginForm = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });
}

  // getters for strict typing
  get username() { return this.loginForm.get('username'); }
  get password() { return this.loginForm.get('password'); }

  onSubmit(): void {
    this.submitted = true;
    if (this.loginForm.invalid) return;

    // Call backend signin
    this.authService.signin({
      username: this.username?.value!,
      password: this.password?.value!
    }).subscribe({
      next: (res) => {
        // Save JWT token
        this.authService.saveToken(res.token);

        // Navigate to home/dashboard
        this.router.navigate(['/main']);
      },
      error: (err) => {
        console.error('Login error:', err);
        alert('Invalid username or password');
      }
    });
  }
}