import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable, isDevMode } from "@angular/core";
import { KeycloakService } from "keycloak-angular";
import { Observable } from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class UploadService {
  apiUrl = isDevMode() ? '/api/upload' : 'https://api.chitchato.com/upload';

  constructor(private keycloakService: KeycloakService, private httpClient: HttpClient) { }

  postDeleteDocument(connectorUuid: string): Observable<any> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      .set('connectorUuid', connectorUuid)
      ;

    return this.httpClient.post(`${this.apiUrl}/delete-document`, {}, { headers, params });
  }

  postGetQr(personaUuid: string): Observable<any> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');

    const params = new HttpParams()
      .set('personaUuid', personaUuid)
      ;

    return this.httpClient.post(`${this.apiUrl}/get-qr`, {}, { headers, params });
  }

  postUploadDocument(personaUuid: string, selectedFile: File): Observable<any> {
    console.log('postDeleteDocument selectedFile:', selectedFile);
    const headers = new HttpHeaders()
      ;

    const params = new HttpParams()
      .set('personaUuid', personaUuid)
      ;

    const formData = new FormData();
    formData.append('file', selectedFile, selectedFile.name);

    return this.httpClient.post(`${this.apiUrl}/upload-document`, formData, { headers, params });
  }

  postAddPersona(name: string, description: string, number: number, selectedFile: File): Observable<any> {
    console.log('postDeleteDocument selectedFile:', selectedFile);
    const headers = new HttpHeaders()
      ;

    const params = new HttpParams()
      .set('name', name)
      .set('description', description)
      .set('number', number)
      ;

    const formData = new FormData();
    formData.append('file', selectedFile, selectedFile.name);

    return this.httpClient.post(`${this.apiUrl}/add-persona`, formData, { headers, params });
  }
}