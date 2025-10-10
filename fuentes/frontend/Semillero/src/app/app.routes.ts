
import { Routes, RouterModule } from "@angular/router";
import { AdminEstudiantesComponent } from "./components/admin-estudiantes/admin-estudiantes.component";
import { NuevoEstudianteComponent } from "./components/nuevo-estudiante/nuevo-estudiante.component";
import { AdminMateriasComponent } from "./components/admin-materias/admin-materias.component";
import { MateriasComponent } from "./components/materias/materias.component";
import { PromediosComponent } from "./components/promedios/promedios.component";


const APP_ROUTES: Routes = [
    { path: 'adminEstudiantes', component: AdminEstudiantesComponent },
    { path: 'nuevoEstudiante', component: NuevoEstudianteComponent },
    { path: 'adminMaterias', component: AdminMateriasComponent },
    { path: 'materias', component: MateriasComponent },
    { path: 'promedios', component: PromediosComponent }
];

export const APP_ROUTING = RouterModule.forRoot(APP_ROUTES, { useHash:true});