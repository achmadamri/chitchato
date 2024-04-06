import { Component, OnInit } from '@angular/core';
import { PersonaService } from 'src/app/services/personaservice';

@Component({
  selector: 'app-persona',
  templateUrl: './persona.component.html',
  styleUrls: ['./persona.component.scss']
})
export class PersonaComponent implements OnInit {

  constructor(
    private personaService: PersonaService
  ) { }

  ngOnInit() {
    this.personaService.getProductList().subscribe(data => {
      console.log(data);
    });
  }

}
