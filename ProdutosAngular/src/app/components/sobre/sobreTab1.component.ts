import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-sobre',
  imports: [
    RouterLink,
    MatCardModule
  ],
  templateUrl: './sobreTab1.component.html',
  styleUrl: './sobreTab1.component.css'
})
export class SobreTab1Component {



}
