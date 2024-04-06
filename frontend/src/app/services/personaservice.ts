import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { KeycloakService } from "keycloak-angular";
import { Observable } from "rxjs";
import { Persona } from "./persona";
import { GetPersonaResponse } from "./getpersonaresponse";
import { GetPersonaListResponse } from "./getpersonalistresponse";


@Injectable({
  providedIn: 'root'
})
export class PersonaService {
  apiUrl = '/api/persona';

  constructor(private keycloakService: KeycloakService, private httpClient: HttpClient) { }

  getPersonaList(): Observable<GetPersonaListResponse> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      ;

    return this.httpClient.get<GetPersonaListResponse>(`${this.apiUrl}/personalist`, { headers, params });
  }

  getPersona(uuid: string): Observable<GetPersonaResponse> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      .set('uuid', uuid)
      ;

    return this.httpClient.get<GetPersonaResponse>(`${this.apiUrl}/persona`, { headers, params });
  }
}