import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResumeAnalysisDTO } from '../model/resume-analysis.model';

@Injectable({
  providedIn: 'root'
})
export class ResumeAnalysisService {

  private baseUrl = 'http://localhost:8080/api/resume-analysis';

  constructor(private http: HttpClient) {}

  getAnalysesByUser(userId: number): Observable<ResumeAnalysisDTO[]> {
    return this.http.get<ResumeAnalysisDTO[]>(`${this.baseUrl}/user/${userId}`);
  }

  deleteAnalysis(id: number): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/${id}`);
  }
}