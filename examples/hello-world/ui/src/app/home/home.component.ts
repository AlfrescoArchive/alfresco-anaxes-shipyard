/*
 * Copyright 2017 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Component, OnInit } from '@angular/core';
import { Http, Response } from '@angular/http';
import { AppConfigService } from 'ng2-alfresco-core';
import 'rxjs/add/operator/map';
import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  private id;
  private sub: any;
  private apiUrl;
  data: any ={};
  msg;

  constructor(private route: ActivatedRoute, private http:Http,
        appConfig: AppConfigService) {
    this.apiUrl = appConfig.get('backEndHost') + '/hello/';
  }

  private ngOnInit() {
     this.sub = this.route.params.subscribe(params => {
       this.id = params['id'];
      });
     this.getResponse(this.id);
  }

  getResponse(id) {
     this.apiUrl += id;
     return this.http.get(this.apiUrl).
       map((res: Response) => res.json()).subscribe(data => {
         this.msg=data.value;
         this.data = data;
       },
       err => {
         this.msg = 'ERROR: Something went wrong!';
       })
   }
}
