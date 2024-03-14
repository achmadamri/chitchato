import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakProfile } from 'keycloak-js';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  userProfile: KeycloakProfile | null = null;

  constructor(private keycloakService: KeycloakService) { }

  async ngOnInit() {
    if (this.keycloakService.isLoggedIn()) {
      this.userProfile = await this.keycloakService.loadUserProfile();      
    }
  }

}
