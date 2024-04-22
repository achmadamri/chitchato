import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable, isDevMode } from "@angular/core";
import { KeycloakService } from "keycloak-angular";
import { Observable } from "rxjs";
import { GetUserResponse } from "./getuserresponse";
import { GetMessageResponse } from "./getmessageresponse";


@Injectable({
  providedIn: 'root'
})
export class UserService {
  apiUrl = isDevMode() ? '/api/user' : 'https://api.chitchato.com/api/user';

  constructor(private keycloakService: KeycloakService, private httpClient: HttpClient) { }

  getUser(): Observable<GetUserResponse> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      ;

    return this.httpClient.get<GetUserResponse>(`${this.apiUrl}/get-user`, { headers, params });
  }

  getMessages(): Observable<GetMessageResponse> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      ;

    return this.httpClient.get<GetMessageResponse>(`${this.apiUrl}/get-messages`, { headers, params });
  }
}