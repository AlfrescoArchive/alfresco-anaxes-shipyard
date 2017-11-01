import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdfModule } from './adf.module';
import { HomeComponent } from './home/home.component';
import { AppComponent } from './app.component';

import {HttpModule} from '@angular/http';

const appRoutes: Routes = [
  { path: '', redirectTo: 'hello/welcome', pathMatch: 'full' },
  { path: 'hello/:id', component: HomeComponent }
];

@NgModule({
  imports: [
    BrowserModule,
    RouterModule.forRoot(
      appRoutes // ,
      // { enableTracing: true } // <-- debugging purposes only
    ),
    AdfModule,
    HttpModule
  ],
  declarations: [
    AppComponent,
    HomeComponent
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
