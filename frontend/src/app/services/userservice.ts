import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { KeycloakService } from "keycloak-angular";
import { Observable } from "rxjs";
import { GetUserResponse } from "./getuserresponse";


@Injectable({
  providedIn: 'root'
})
export class UserService {
  // apiUrl = '/api/user';
  apiUrl = 'https://api.chitchato.com/user';

  constructor(private keycloakService: KeycloakService, private httpClient: HttpClient) { }

  getUser(): Observable<GetUserResponse> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      ;

    return this.httpClient.get<GetUserResponse>(`${this.apiUrl}/get-user`, { headers, params });
  }
}