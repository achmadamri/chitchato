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
  userNumber = '';
  public getMessageResponse: GetMessageResponse;

  constructor(
    private userService: UserService  
  ) { }

  ngOnInit() {
    this.userNumber = '';
    this.getMessages(this.userNumber);
  }

  getMessages(userNumber: string) {
    this.clicked = true;

    this.userNumber = userNumber;
    this.getMessageResponse = null;

    this.userService.getMessages(userNumber).subscribe(data => {
      this.getMessageResponse = data;

      this.clicked = false;
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
