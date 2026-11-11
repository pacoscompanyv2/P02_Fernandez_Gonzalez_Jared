import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from './api.config';
import { VentaRequest, VentaResponse } from './models';

@Injectable({ providedIn: 'root' })
export class VentasService {
  private readonly http = inject(HttpClient);

  registrar(venta: VentaRequest): Observable<VentaResponse> {
    return this.http.post<VentaResponse>(`${API_BASE}/ventas`, venta);
  }

  consultar(folio: string): Observable<VentaResponse> {
    return this.http.get<VentaResponse>(`${API_BASE}/ventas/${encodeURIComponent(folio)}`);
  }
}
