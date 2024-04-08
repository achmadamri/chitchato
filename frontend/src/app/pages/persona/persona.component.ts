import { Component, OnInit } from '@angular/core';
import { GetPersonaListResponse } from 'src/app/services/getpersonalistresponse';
import { GetPersonaResponse } from 'src/app/services/getpersonaresponse';
import { Persona } from 'src/app/services/persona';
import { PersonaService } from 'src/app/services/personaservice';
import { UploadService } from 'src/app/services/uploadservice';
import { Util } from 'src/app/util';

@Component({
  selector: 'app-persona',
  templateUrl: './persona.component.html',
  styleUrls: ['./persona.component.scss']
})
export class PersonaComponent implements OnInit {

  util: Util = new Util();
  clicked = false;
  public getPersonaListResponse: GetPersonaListResponse;
  public getPersonaResponse: GetPersonaResponse;

  constructor(
    private personaService: PersonaService,
    private uploadService: UploadService
  ) { }

  ngOnInit() {
    this.getPersonaList();        
  }

  postDeleteDocument(connectorUuid: string) {
    this.clicked = true;

    this.uploadService.postDeleteDocument(connectorUuid).subscribe(data => {
      console.log(data);

      this.clicked = false;
      this.util.showNotification(2, 'bottom', 'center', 'Persona deleted');
      this.getPersonaList();
    }, error => {
      console.log(error);
      
      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', 'Error deleting persona');
      }
    });
  }

  getPersonaList() {
    this.personaService.getPersonaList().subscribe(data => {
      this.getPersonaListResponse = data;
    }, error => {
      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', 'Error getting persona list');
      }
    });
  }

  openPersona(uuid: string) {
    this.clicked = true;

    this.personaService.getPersona(uuid).subscribe(data => {
      this.getPersonaResponse = data;
      this.clicked = false;
    }, error => {
      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', 'Error getting persona');
      }    
    });
  }

  back(uuid: string) {
    this.getPersonaListResponse = null;
    this.getPersonaResponse = null;
    this.getPersonaList();
  }

  update(uuid: string) {
    this.clicked = true;
    
    this.personaService.getPersona('b7c00e22-30f2-4409-88de-ac779112f726').subscribe(data => {
      this.getPersonaResponse = data;
      this.clicked = false;
    }, error => {
      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', 'Error updating persona');
      }
    });
  }
}
