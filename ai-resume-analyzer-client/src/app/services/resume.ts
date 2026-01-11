import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Resume } from '../model/resume.model';

@Injectable({
  providedIn: 'root'
})
export class ResumeService {

  private baseUrl = 'http://localhost:8080/api/resumes';

  constructor(private http: HttpClient) { }

  uploadResume(userId: number, file: File): Observable<Resume> {
    const formData = new FormData();
    formData.append('userId', userId.toString());
    formData.append('file', file);
    return this.http.post<Resume>(`${this.baseUrl}/upload`, formData);
  }

  getResumePdf(resumeId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${resumeId}/pdf`, {
      responseType: 'blob' as const
    });
  }

}
