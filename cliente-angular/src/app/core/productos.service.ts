import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from './api.config';
import { Producto } from './models';

@Injectable({ providedIn: 'root' })
export class ProductosService {
  private readonly http = inject(HttpClient);

  listar(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${API_BASE}/productos`);
  }

  obtener(codigo: string): Observable<Producto> {
    return this.http.get<Producto>(`${API_BASE}/productos/${encodeURIComponent(codigo)}`);
  }
}
