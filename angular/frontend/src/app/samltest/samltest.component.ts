import { Component } from '@angular/core';
import { SamltestserviceService } from './samltestservice.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-samltest',
  templateUrl: './samltest.component.html',
  styleUrls: ['./samltest.component.css']
})
export class SamltestComponent {
  constructor(public service:SamltestserviceService){

  }

  messagecontent!:string;

  visible: boolean = false;

  

  showDialog() {
      this.visible = true;
      this.service.getPopUpMessage().subscribe(
        (data:any) => {
        // Access the "message" property directly
      this.messagecontent =  data.message;

      // Log the message content
      console.log(this.messagecontent);

        },
        (error) => {
          // Handle error
          console.error('Error:', error);
        }
      );
  }

  

}
