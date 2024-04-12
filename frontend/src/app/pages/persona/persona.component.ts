import { Component, OnInit } from '@angular/core';
import { GetPersonaListResponse } from 'src/app/services/getpersonalistresponse';
import { GetPersonaResponse } from 'src/app/services/getpersonaresponse';
import { GetUserResponse } from 'src/app/services/getuserresponse';
import { Persona } from 'src/app/services/persona';
import { PersonaService } from 'src/app/services/personaservice';
import { PostAddPersonaRequest } from 'src/app/services/postaddpersonarequest';
import { PostUpdatePersonaRequest } from 'src/app/services/postupdatepersonarequest';
import { UploadService } from 'src/app/services/uploadservice';
import { UserService } from 'src/app/services/userservice';
import { Util } from 'src/app/util';

@Component({
  selector: 'app-persona',
  templateUrl: './persona.component.html',
  styleUrls: ['./persona.component.scss']
})
export class PersonaComponent implements OnInit {

  util: Util = new Util();
  clicked = false;
  showList = true;
  showDetails = false;
  personaUuid = '';
  name = '';
  description = '';
  systemPrompt = '';
  taskPrompt = '';  
  showAdd = false;
  public getUserResponse: GetUserResponse;
  public getPersonaListResponse: GetPersonaListResponse;
  public getPersonaResponse: GetPersonaResponse;
  public postAddPersonaRequest: PostAddPersonaRequest = new PostAddPersonaRequest();
  public postUpdatePersonaRequest: PostUpdatePersonaRequest = new PostUpdatePersonaRequest();
  selectedFileAdd: File;
  selectedFileUpdate: File;

  constructor(
    private userService: UserService,
    private personaService: PersonaService,
    private uploadService: UploadService    
  ) { }

  ngOnInit() {
    this.getPersonaList();
  }

  doShowList(uuid: string) {
    this.showList = true;
    this.showDetails = false;
    this.showAdd = false;

    this.getPersonaListResponse = null;
    this.getPersonaResponse = null;
    this.getPersonaList();
  }

  doShowAdd(uuid: string) {
    this.clicked = true;

    this.showList = false;
    this.showDetails = false;
    this.showAdd = true;

    this.clicked = false;
  }

  doShowDetails(uuid: string) {
    this.clicked = true;

    this.showList = false;
    this.showDetails = true;
    this.showAdd = false;

    this.personaUuid = uuid;

    this.userService.getUser().subscribe(data => {
      this.getUserResponse = data;
      
      this.personaService.getPersona(uuid).subscribe(data => {
        this.getPersonaResponse = data;

        this.postUpdatePersonaRequest.name = this.getPersonaResponse.persona.name;
        this.postUpdatePersonaRequest.description = this.getPersonaResponse.persona.description;
        this.postUpdatePersonaRequest.systemPrompt = this.getPersonaResponse.prompt.systemPrompt;
        this.postUpdatePersonaRequest.taskPrompt = this.getPersonaResponse.prompt.taskPrompt;

        console.log(data);
        this.clicked = false;
      }, error => {
        if (error.status === 403) {
          window.location.href = '/';        
        } else {        
          this.clicked = false;
          this.util.showNotification(4, 'bottom', 'center', 'Error getting persona');
        }    
      });
    }, error => {
      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', 'Error getting user');
      }
    });    
  }
  
  onFileChangedAdd(event) {
    this.selectedFileAdd = event.target.files[0];
  }
  
  onFileChangedUpdate(event) {
    this.selectedFileUpdate = event.target.files[0];
  }

  postUpdatePersona() {
    this.clicked = true;

    this.postUpdatePersonaRequest.personaUuid = this.personaUuid;
    
    this.personaService.postUpdatePersona(this.postUpdatePersonaRequest).subscribe(data => {
      console.log(data);

      this.clicked = false;
      this.util.showNotification(2, 'bottom', 'center', 'Persona updated');
      
      this.doShowDetails(this.personaUuid);
    }, error => {
      console.log(error);

      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', error.error.message);
      }
    });
  }

  postDeleteDocument(connectorUuid: string) {
    this.clicked = true;

    this.uploadService.postDeleteDocument(connectorUuid).subscribe(data => {
      console.log(data);

      this.clicked = false;
      this.util.showNotification(2, 'bottom', 'center', 'Document deleted');
      
      this.doShowDetails(this.personaUuid);
    }, error => {
      console.log(error);

      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', error.error.message);
      }
    });
  }

  postUploadDocument() {
    if (!this.selectedFileUpdate) {
      this.util.showNotification(4, 'bottom', 'center', 'No file selected');
      return;
    }

    this.clicked = true;

    this.uploadService.postUploadDocument(this.personaUuid, this.selectedFileUpdate).subscribe(data => {
      console.log(data);

      this.clicked = false;
      this.util.showNotification(2, 'bottom', 'center', 'Document uploaded');
      
      this.doShowDetails(this.personaUuid);

      const input = document.getElementById('input-file-update') as HTMLInputElement;
      input.value = null;

      this.selectedFileUpdate = null;
    }, error => {
      console.log(error);

      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', error.error.message);
      }
    });
  }

  postAddPersona() {
    if (!this.selectedFileAdd) {
      this.util.showNotification(4, 'bottom', 'center', 'No file selected');
      return;
    }

    this.clicked = true;

    this.uploadService.postAddPersona(this.postAddPersonaRequest.name, this.postAddPersonaRequest.description, this.selectedFileAdd).subscribe(data => {
      console.log(data);

      this.clicked = false;
      this.util.showNotification(2, 'bottom', 'center', 'Persona added');
      
      this.doShowList(null);

      const input = document.getElementById('input-file-add') as HTMLInputElement;
      input.value = null;

      this.selectedFileAdd = null;
    }, error => {
      console.log(error);

      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', error.error.message);
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
