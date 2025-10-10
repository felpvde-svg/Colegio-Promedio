import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Promedios } from '../_models/promedios';

@Injectable({
  providedIn: 'root'
})
export class PromediosService {

  private apiUrl = 'http://localhost:8081/servicesRest/WsColegio'; // ajusta con tu puerto/app

  constructor(private http: HttpClient) {}

  getPromedios(): Observable<Promedios[]> {
    return this.http.get<Promedios[]>(`${this.apiUrl}/getPromedios`);
  }

  insertPromedios(promedio: Promedios): Observable<any> {
    return this.http.post(`${this.apiUrl}/insertPromedios`, promedio);
  }

  updatePromedios(promedio: Promedios): Observable<any> {
    return this.http.put(`${this.apiUrl}/updatePromedios`, promedio);
  }

  deletePromedios(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/deletePromedios/${id}`);
  }
}
