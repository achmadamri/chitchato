import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { KeycloakService } from "keycloak-angular";
import { Observable } from "rxjs";
import { Persona } from "./persona";


@Injectable({
  providedIn: 'root'
})
export class PersonaService {
  apiUrl = '/api/persona'; // URL to web api

  constructor(private keycloakService: KeycloakService, private httpClient: HttpClient) { }  

  getProductList(): Observable<Persona> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      // .set('requestId', this.util.randomString(10))
      // .set('requestDate', ((new Date(Date.now() - ((new Date()).getTimezoneOffset() * 60000))).toISOString().slice(0, -1)) + '000')
      // .set('email', localStorage.getItem('email'))
      // .set('token', localStorage.getItem('token'))
      // .set('length', length.toString())
      // .set('pageSize', pageSize.toString())
      // .set('pageIndex', pageIndex.toString())
      // .set('brand', brand)
      // .set('sku', sku)
      // .set('item', item)
      // .set('code', code)
      // .set('type', type)
      ;

    return this.httpClient.get<Persona>(`${this.apiUrl}/list`, { headers, params });
  }
}