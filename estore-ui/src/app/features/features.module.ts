import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../material.module';

import { LoginRegistrationComponent } from './login-registration/login-registration.component';
import { AdminModule } from './admin/admin.module';
import { BrowseCatalogModule } from './browse-catalog/browse-catalog.module';
import { NavigationModule } from './navigation/navigation.module';
import { OrderProcessingModule } from './order-processing/order-processing.module';
import { ReviewsModule } from './reviews/reviews.module';



@NgModule({
  declarations: [LoginRegistrationComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    AdminModule,
    BrowseCatalogModule,
    NavigationModule,
    OrderProcessingModule,
    ReviewsModule,
  ],
  exports: [
    LoginRegistrationComponent,
    AdminModule,
    BrowseCatalogModule,
    LoginRegistrationComponent,
    NavigationModule,
    OrderProcessingModule,
    ReviewsModule,
  ]
})
export class FeaturesModule { }
