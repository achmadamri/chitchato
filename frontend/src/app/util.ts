import { Injectable } from "@angular/core";

declare let $: any;

@Injectable({ providedIn: 'root' })
export class Util {
    constructor() { }

    showNotification(color, from, align, message){
        const type = ['','info','success','warning','danger'];
    
        // const color = Math.floor((Math.random() * 4) + 1);
    
        $.notify({
            icon: "notifications",
            message: message
    
        },{
            type: type[color],
            timer: 60000,
            placement: {
                from: from,
                align: align
            },
            template: '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}" role="alert">' +
                '<button type="button" aria-hidden="true" class="close" data-notify="dismiss">×</button>' +
                '<span data-notify="icon"></span> ' +
                '<span data-notify="title">{1}</span> ' +
                '<span data-notify="message">{2}</span>' +
                '<div class="progress" data-notify="progressbar">' +
                    '<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>' +
                '</div>' +
                '<a href="{3}" target="{4}" data-notify="url"></a>' +
            '</div>'
        });
    }
}
