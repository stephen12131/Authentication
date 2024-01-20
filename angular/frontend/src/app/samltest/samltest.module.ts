import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SamltestComponent } from './samltest.component';
import { SamltestserviceService } from './samltestservice.service';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { HttpClientModule } from '@angular/common/http';





@NgModule({
  declarations: [SamltestComponent],
  imports: [    
    CommonModule,DialogModule,ButtonModule,HttpClientModule
    
  ],
  providers: [SamltestserviceService],
})
export class SamltestModule { }
