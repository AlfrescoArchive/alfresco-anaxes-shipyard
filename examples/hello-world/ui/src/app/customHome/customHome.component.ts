import { Component, OnInit } from '@angular/core';
import { Http, Response } from '@angular/http';
import { AppConfigService } from 'ng2-alfresco-core';
import 'rxjs/add/operator/map';
import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-home',
  templateUrl: './customHome.component.html',
  styleUrls: ['./customHome.component.css']
})
export class CustomHomeComponent {

  private id;
  private sub: any;
  private apiUrl;
  data: any ={};
  msg;
  private status;

  constructor(private route: ActivatedRoute, private http:Http,
              appConfig: AppConfigService) {
                this.apiUrl = appConfig.get('backEndHost') + "hello/";
  }
  
  private ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
     });
    this.getResponse(this.id);
  }

  getResponse(id) {
    this.apiUrl += id;
    this.http.get(this.apiUrl).
      map((res: Response) => res.json()).subscribe(data => {
        this.msg=data.value;
        this.data = data;
        console.log(data.status)
      },
      err => {
        this.msg = 'ERROR: Something went wrong!';
      })
  }
}
