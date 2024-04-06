import { Component, OnInit } from '@angular/core';
import { GetPersonaListResponse } from 'src/app/services/getpersonalistresponse';
import { GetPersonaResponse } from 'src/app/services/getpersonaresponse';
import { Persona } from 'src/app/services/persona';
import { PersonaService } from 'src/app/services/personaservice';

@Component({
  selector: 'app-persona',
  templateUrl: './persona.component.html',
  styleUrls: ['./persona.component.scss']
})
export class PersonaComponent implements OnInit {

  clicked = false;
  public getPersonaListResponse: GetPersonaListResponse;
  public getPersonaResponse: GetPersonaResponse;

  constructor(
    private personaService: PersonaService
  ) { }

  ngOnInit() {
    this.getPersonaList();
  }

  getPersonaList() {
    this.personaService.getPersonaList().subscribe(data => {
      this.getPersonaListResponse = data;
    }, error => {
      window.location.href = '/';
    });
  }

  openPersona(uuid: string) {
    this.clicked = true;

    this.personaService.getPersona(uuid).subscribe(data => {
      this.getPersonaResponse = data;
      this.clicked = false;
    }, error => {
      window.location.href = '/';
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
      window.location.href = '/';
    });
  }

}
