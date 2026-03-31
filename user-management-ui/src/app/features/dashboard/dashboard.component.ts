import { Component, OnInit } from '@angular/core';

import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { User, UserRole } from '../../core/models/user.model';

interface StatCard {
  label: string;
  value: number | string;
  icon: string;
  color: string;
  bg: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  isLoading = true;
  isAdmin = false;
  currentUser: User | null = null;
  stats: StatCard[] = [];
  recentUsers: User[] = [];

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.currentUser = this.authService.getCurrentUser();

    if (this.isAdmin) {
      this.userService.getAllUsers().subscribe({
        next: (users) => {
          const admins = users.filter(u => u.role === UserRole.ADMIN).length;
          const active = users.filter(u => u.enabled).length;
          this.stats = [
            { label: 'Total Users', value: users.length, icon: 'people', color: '#6366f1', bg: '#eef2ff' },
            { label: 'Admins', value: admins, icon: 'admin_panel_settings', color: '#f59e0b', bg: '#fffbeb' },
            { label: 'Active Users', value: active, icon: 'check_circle', color: '#10b981', bg: '#ecfdf5' },
            { label: 'Inactive', value: users.length - active, icon: 'cancel', color: '#ef4444', bg: '#fef2f2' }
          ];
          this.recentUsers = users.slice(0, 5);
          this.isLoading = false;
        },
        error: () => { this.isLoading = false; }
      });
    } else {
      this.stats = [
        { label: 'Role', value: this.currentUser?.role || '-', icon: 'badge', color: '#6366f1', bg: '#eef2ff' },
        { label: 'Status', value: this.currentUser?.enabled ? 'Active' : 'Inactive', icon: 'circle', color: '#10b981', bg: '#ecfdf5' }
      ];
      this.isLoading = false;
    }
  }
}

