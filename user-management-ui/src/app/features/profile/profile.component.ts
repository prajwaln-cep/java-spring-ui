import { Component, OnInit } from '@angular/core';

import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { StorageService } from '../../core/services/storage.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatDividerModule
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  currentUser: User | null = null;
  profileForm!: FormGroup;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private storage: StorageService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.profileForm = this.fb.group({
      name: [this.currentUser?.name, [Validators.required, Validators.minLength(2)]],
      email: [{ value: this.currentUser?.email, disabled: true }]
    });
  }

  getInitials(): string {
    return (this.currentUser?.name || 'U')
      .split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  onSave(): void {
    if (this.profileForm.invalid || !this.currentUser) return;
    this.isLoading = true;
    this.userService.updateUser(this.currentUser.id, { name: this.profileForm.value.name }).subscribe({
      next: (updated) => {
        this.storage.setUser(updated);
        this.currentUser = updated;
        this.isLoading = false;
        this.snackBar.open('Profile updated successfully!', 'Close', { duration: 3000 });
      },
      error: () => { this.isLoading = false; }
    });
  }
}

