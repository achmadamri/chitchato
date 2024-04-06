import { Component, OnInit } from '@angular/core';
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
  public lstPersona: Persona[];
  public getPersonaResponse: GetPersonaResponse;

  constructor(
    private personaService: PersonaService
  ) { }

  ngOnInit() {
    this.personaService.getPersonaList().subscribe(data => {
      this.lstPersona = data;
    });
  }

  openPersona(uuid: string) {
    this.clicked = true;

    this.personaService.getPersona(uuid).subscribe(data => {
      this.getPersonaResponse = data;

      this.clicked = false;
    });
  }

  back(uuid: string) {
    this.getPersonaResponse = null;
  }

  update(uuid: string) {
    this.clicked = true;

    // disable button while loading and re-enable it after loading
    this.personaService.getPersona('b7c00e22-30f2-4409-88de-ac779112f726').subscribe(data => {
      this.getPersonaResponse = data;

      this.clicked = false;
    });
  }

}
