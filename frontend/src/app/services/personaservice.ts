import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { KeycloakService } from "keycloak-angular";
import { Observable } from "rxjs";
import { GetPersonaListResponse } from "./getpersonalistresponse";
import { GetPersonaResponse } from "./getpersonaresponse";
import { PostUpdatePersonaRequest } from "./postupdatepersonarequest";


@Injectable({
  providedIn: 'root'
})
export class PersonaService {
  // apiUrl = '/api/persona';
  apiUrl = 'https://api.chitchato.com/persona';

  constructor(private keycloakService: KeycloakService, private httpClient: HttpClient) { }

  getPersonaList(): Observable<GetPersonaListResponse> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      ;

    return this.httpClient.get<GetPersonaListResponse>(`${this.apiUrl}/persona-list`, { headers, params });
  }

  getPersona(uuid: string): Observable<GetPersonaResponse> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      .set('uuid', uuid)
      ;

    return this.httpClient.get<GetPersonaResponse>(`${this.apiUrl}/persona`, { headers, params });
  }

  postUpdatePersona(postUpdatePersonaRequest: PostUpdatePersonaRequest): Observable<any> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    return this.httpClient.post(`${this.apiUrl}/update-persona`, postUpdatePersonaRequest, { headers });
  }
}