import { Component, OnInit } from '@angular/core';
import { Http, Response } from '@angular/http';
import 'rxjs/add/operator/map';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  private apiUrl = '/hello/welcome';
  data: any ={};
  msg;
  constructor(private http:Http) {
    this.getResponse();
  }

  getResponse() {
    return this.http.get(this.apiUrl).
      map((res: Response) => res.json()).subscribe(data => {
        this.msg=data.value;
        this.data = data
      })
  }
}
