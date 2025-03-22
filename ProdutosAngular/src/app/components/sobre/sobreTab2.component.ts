import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";

@Component({
  selector: 'app-sobre1',
    imports: [
        RouterLink,
        MatCard,
        MatCardContent,
        MatCardHeader,
        MatCardTitle
    ],
  templateUrl: './sobreTab2.component.html',
  styleUrl: './sobreTab2.component.css'
})
export class SobreTab2Component {

}
