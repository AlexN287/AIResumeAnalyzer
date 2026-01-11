import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { AuthService } from '../services/auth.service';
import { ResumeAnalysisDTO } from '../model/resume-analysis.model';
import { ResumeAnalysisService } from '../services/resume-analysis';
import { ResumeService } from '../services/resume';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './main.html',
  styleUrls: ['./main.css']
})
export class MainComponent implements OnInit {

  resumeAnalyses = signal<ResumeAnalysisDTO[]>([]);
  selectedAnalysis = signal<ResumeAnalysisDTO | null>(null);

  pdfUrl = signal<SafeResourceUrl | null>(null);

  private objectUrl: string | null = null;

userId!: number;

  constructor(
    private authService: AuthService,
    private resumeAnalysisService: ResumeAnalysisService,
    private resumeService: ResumeService,
    private sanitizer: DomSanitizer,
    private router: Router
  ) {}

  ngOnInit() {
    const id = this.authService.getUserId();
  if (!id) {
    this.router.navigate(['/login']);
    return;
  }

  this.userId = id;
  this.loadResumeAnalyses();
  }

  loadResumeAnalyses() {
    this.resumeAnalysisService.getAnalysesByUser(this.userId)
      .subscribe({
        next: analyses => this.resumeAnalyses.set(analyses),
        error: err => console.error('Failed to load analyses', err)
      });
  }

selectAnalysis(analysis: ResumeAnalysisDTO) {
  this.selectedAnalysis.set(analysis);
  this.loadPdf(analysis.resumeId);
}
  loadPdf(resumeId: number) {
    this.pdfUrl.set(null);

    if (this.objectUrl) {
      URL.revokeObjectURL(this.objectUrl);
      this.objectUrl = null;
    }

    this.resumeService.getResumePdf(resumeId).subscribe({
      next: (blob) => {
        const pdfBlob = new Blob([blob], { type: 'application/pdf' });

        this.objectUrl = URL.createObjectURL(pdfBlob);
        this.pdfUrl.set(
          this.sanitizer.bypassSecurityTrustResourceUrl(this.objectUrl)
        );
      },
      error: (err) => console.error('Failed to load PDF', err)
    });
  }

  isUploading = signal(false);

onFileSelected(event: Event) {
  const input = event.target as HTMLInputElement;
  if (!input.files || input.files.length === 0) return;

  const file = input.files[0];
  input.value = ''; 

  this.uploadResume(file);
}

uploadResume(file: File) {
  this.isUploading.set(true);

  this.resumeService.uploadResume(this.userId, file).subscribe({
    next: () => {
      this.isUploading.set(false);

      this.loadResumeAnalyses();

      this.selectedAnalysis.set(null);
      this.pdfUrl.set(null);
    },
    error: (err) => {
      this.isUploading.set(false);
      console.error('Upload failed', err);
      alert('Failed to upload resume');
    }
  });
}

  deleteAnalysis(id: number, event: MouseEvent) {
  event.stopPropagation();

  this.resumeAnalysisService.deleteAnalysis(id).subscribe({
    next: () => {
      this.resumeAnalyses.update(list => list.filter(a => a.id !== id));

      if (this.selectedAnalysis()?.id === id) {
        this.selectedAnalysis.set(null);
        this.pdfUrl.set(null);

        if (this.objectUrl) {
          URL.revokeObjectURL(this.objectUrl);
          this.objectUrl = null;
        }
      }

      this.loadResumeAnalyses();
    },
    error: (err) => {
      console.error('Delete failed', err);
      alert('Failed to delete analysis');
    }
  });
}

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
