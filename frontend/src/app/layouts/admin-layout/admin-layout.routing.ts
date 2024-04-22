import { Routes } from '@angular/router';

import { DashboardComponent } from '../../pages/dashboard/dashboard.component';
import { UserProfileComponent } from '../../pages/user-profile/user-profile.component';
import { PersonaComponent } from 'src/app/pages/persona/persona.component';
import { MessageComponent } from 'src/app/pages/message/message.component';

export const AdminLayoutRoutes: Routes = [
    { path: 'dashboard',      component: DashboardComponent },
    { path: 'user-profile',   component: UserProfileComponent },
    { path: 'persona',        component: PersonaComponent },
    { path: 'message',        component: MessageComponent }
];
