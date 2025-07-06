// src/app/components/file-upload/file-upload.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { FileUploadService, FileUploadResponse } from '../../services/file-upload.service';
import { WebSocketService } from '../../services/websocket.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';

interface ProgressMessage {
  fileId: string;
  phase: 'conversion' | 'saving' | 'done' | 'error';
  percent?: number;
  message?: string;
}

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressBarModule,
    MatIconModule
  ],
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss']
})
export class FileUploadComponent implements OnInit, OnDestroy {
  selectedFile: File | null = null;
  message = '';
  progress = -1;
  downloadUrl: string | null = null;

  // NEW: store both the returned id and the original filename
  private latestUploadedFileId: string | null = null;
  uploadedFilename: string | null = null;

  private wsSubscription?: Subscription;

  constructor(
      private fileUploadService: FileUploadService,
      private webSocketService: WebSocketService
  ) {}

  ngOnInit() {
    this.wsSubscription = this.webSocketService.connect().subscribe({
      next: msg => this.handleWsMessage(msg),
      error: err => console.error('WebSocket error:', err),
      complete: () => console.log('WebSocket connection closed')
    });
  }

  ngOnDestroy() {
    this.wsSubscription?.unsubscribe();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] || null;

    // reset state
    this.message = '';
    this.progress = -1;
    this.downloadUrl = null;
    this.latestUploadedFileId = null;
    this.uploadedFilename = null;
  }

  upload() {
    if (!this.selectedFile) return;

    this.message = '';
    this.progress = 0;
    this.downloadUrl = null;

    this.fileUploadService.uploadFile(this.selectedFile).subscribe({
      next: (res: FileUploadResponse) => {
        // show the ID and remember it for WS updates
        this.latestUploadedFileId = res.id;
        // remember the original filename (or from the response if your backend sets it)
        this.uploadedFilename = res.filename || this.selectedFile!.name;
        this.message = `Uploaded: ${this.latestUploadedFileId}`;
      },
      error: () => {
        this.message = 'Upload failed!';
        this.progress = -1;
      }
    });
  }

  private handleWsMessage(msg: string) {
    let data: ProgressMessage;
    try {
      data = JSON.parse(msg);
    } catch {
      console.warn('Unexpected WS message:', msg);
      return;
    }
    if (data.fileId !== this.latestUploadedFileId) return;

    switch (data.phase) {
      case 'conversion':
      case 'saving':
        if (typeof data.percent === 'number') {
          this.progress = data.percent;
        }
        break;
      case 'done':
        this.progress = 100;
        this.fileUploadService.getPresignedUrl(data.fileId).subscribe({
          next: url => (this.downloadUrl = url),
          error: () => (this.message = 'Failed to fetch download link.')
        });
        break;
      case 'error':
        this.message = data.message || 'Processing failed';
        this.progress = -1;
        break;
    }
  }
}
