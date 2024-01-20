import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SamltestComponent } from './samltest/samltest.component';

const routes: Routes = [
  { path: 'header', component: SamltestComponent },
  { path: '', redirectTo: '/header', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
