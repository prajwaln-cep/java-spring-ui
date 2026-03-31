import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserService } from '../../../core/services/user.service';
import { User, UserRole } from '../../../core/models/user.model';

export interface UserFormDialogData {
  user?: User;
}

@Component({
  selector: 'app-user-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatIconModule,
    MatSlideToggleModule, MatProgressSpinnerModule
  ],
  templateUrl: './user-form-dialog.component.html',
  styleUrl: './user-form-dialog.component.scss'
})
export class UserFormDialogComponent implements OnInit {
  form!: FormGroup;
  isLoading = false;
  isEditMode = false;
  roles = Object.values(UserRole);
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private dialogRef: MatDialogRef<UserFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserFormDialogData
  ) {}

  ngOnInit(): void {
    this.isEditMode = !!this.data?.user;
    this.buildForm();
  }

  private buildForm(): void {
    const user = this.data?.user;
    this.form = this.fb.group({
      name: [user?.name ?? '', [Validators.required, Validators.minLength(2)]],
      email: [user?.email ?? '', [Validators.required, Validators.email]],
      role: [user?.role ?? UserRole.USER, Validators.required],
      enabled: [user?.enabled ?? true],
      ...(this.isEditMode ? {} : {
        password: ['', [Validators.required, Validators.minLength(8),
          Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])/)]],
      })
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.isLoading = true;
    const value = this.form.value;

    const request$ = this.isEditMode
      ? this.userService.updateUser(this.data.user!.id, { name: value.name, email: value.email, role: value.role, enabled: value.enabled })
      : this.userService.createUser({ name: value.name, email: value.email, password: value.password, role: value.role });

    request$.subscribe({
      next: (user) => this.dialogRef.close(user),
      error: () => { this.isLoading = false; }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}

