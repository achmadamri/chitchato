import { Component, OnInit } from '@angular/core';
import { GetMessageResponse } from 'src/app/services/getmessageresponse';
import { UserService } from 'src/app/services/userservice';
import { Util } from 'src/app/util';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

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
  qrCode = null;
  public getMessageResponse: GetMessageResponse;

  constructor(
    private userService: UserService  
  ) { }

  ngOnInit() {
    this.getMessages();
  }

  getMessages() {
    this.userService.getMessages().subscribe(data => {
      this.getMessageResponse = data;
    }, error => {
      if (error.status === 403) {
        window.location.href = '/';        
      } else {        
        this.clicked = false;
        this.util.showNotification(4, 'bottom', 'center', 'Error getting message list');
      }
    });
  }
}
