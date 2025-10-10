import { Component, OnInit } from '@angular/core';
import { Promedios } from 'src/app/_models/promedios';
import { PromediosService } from 'src/app/_services/promedios.service';

@Component({
  selector: 'app-promedios',
  templateUrl: './promedios.component.html',
  styleUrls: ['./promedios.component.css']
})
export class PromediosComponent implements OnInit {

  promedios: Promedios[] = [];
  nuevoPromedio: Promedios = { idEstudiante: 0, idMateria: 0, nota1: 0, nota2: 0, nota3: 0 };

  editando: boolean = false;

  constructor(private promediosService: PromediosService) {}

  ngOnInit(): void {
    this.listarPromedios();
  }

  listarPromedios() {
    this.promediosService.getPromedios().subscribe(data => {
      this.promedios = data;
    });
  }

  guardarPromedios() {
    if (this.editando) {
      this.promediosService.updatePromedios(this.nuevoPromedio).subscribe(() => {
        this.cancelarEdicion();
        this.listarPromedios();
      });
    } else {
      this.promediosService.insertPromedios(this.nuevoPromedio).subscribe(() => {
        this.nuevoPromedio = { idEstudiante: 0, idMateria: 0, nota1: 0, nota2: 0, nota3: 0 };
        this.listarPromedios();
      });
    }
  }

  editarPromedios(p: Promedios) {
    this.editando = true;
    this.nuevoPromedio = { ...p };
  }

  eliminarPromedios(id: number | undefined) {
    if (!id) return;
    if (confirm('¿Seguro que deseas eliminar este promedio?')) {
      this.promediosService.deletePromedios(id).subscribe(() => {
        this.listarPromedios();
      });
    }
  }

  cancelarEdicion() {
    this.editando = false;
    this.nuevoPromedio = { idEstudiante: 0, idMateria: 0, nota1: 0, nota2: 0, nota3: 0 };
  }
}
