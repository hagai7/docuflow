import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface FileUploadResponse {
  id: string;
  filename: string;
  totalSize: number;
  chunkCount: number;
  hashesOrdered: string[];
}

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  private baseUrl = 'http://localhost:8080/api/files';
  private filesUrl = 'http://localhost:8080/files';

  constructor(private http: HttpClient) {}

  uploadFile(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    console.log(`Uploading file: ${file.name}, size: ${file.size} bytes`);
    return this.http.post<FileUploadResponse>(`${this.baseUrl}/upload`, formData);
  }

  /**
   * Fetches the presigned download URL from backend JSON { url: string }
   * and extracts the 'url' property for direct href binding.
   */
  getPresignedUrl(fileId: string): Observable<string> {
    return this.http
      .get<{ url: string }>(`${this.filesUrl}/${fileId}`)
      .pipe(map(response => response.url));
  }

  listFiles(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }
}
