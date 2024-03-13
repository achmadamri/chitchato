import { Routes } from '@angular/router';

import { DashboardComponent } from '../../pages/dashboard/dashboard.component';
import { UserProfileComponent } from '../../pages/user-profile/user-profile.component';
import { DocumentComponent } from 'src/app/pages/document/document.component';
import { PersonaComponent } from 'src/app/pages/persona/persona.component';

export const AdminLayoutRoutes: Routes = [
    { path: 'dashboard',      component: DashboardComponent },
    { path: 'user-profile',   component: UserProfileComponent },
    { path: 'document',       component: DocumentComponent },
    { path: 'persona',        component: PersonaComponent }
];
