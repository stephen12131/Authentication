import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SamltestserviceService {

  constructor(public http: HttpClient) { }

  port:string="http://localhost:8090"
  getPopUpMessage(): Observable<any[]> {
    return this.http.get<any[]>(this.port+'/getsamplemessage');
}
}
