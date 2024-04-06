import { Component, OnInit } from '@angular/core';
import { Persona } from 'src/app/services/persona';
import { PersonaService } from 'src/app/services/personaservice';

@Component({
  selector: 'app-persona',
  templateUrl: './persona.component.html',
  styleUrls: ['./persona.component.scss']
})
export class PersonaComponent implements OnInit {

  public lstPersona: Persona[];

  constructor(
    private personaService: PersonaService
  ) { }

  ngOnInit() {
    this.personaService.getProductList().subscribe(data => {
      this.lstPersona = data;
      
      // iterate over the list of personas
      this.lstPersona.forEach(persona => {
        console.log(persona);
      });
    });
  }

}
