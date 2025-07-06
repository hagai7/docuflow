import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private socket?: WebSocket;
  private subject?: Subject<string>;

  connect(): Observable<string> {
    if (!this.subject || this.socket?.readyState === WebSocket.CLOSED) {
      this.subject = new Subject<string>();

      this.socket = new WebSocket('ws://localhost:8080/ws/progress');

      this.socket.onopen = () => {
        console.log('WS connection OPENED');
      };

      this.socket.onmessage = (event) => {
        this.subject?.next(event.data);
      };

      this.socket.onerror = (event) => {
        console.error('WS error', event);
        this.subject?.error(event);
      };

      this.socket.onclose = () => {
        console.log('WS connection CLOSED');
        this.subject?.complete();
        this.subject = undefined;
      };
    }

    return this.subject.asObservable();
  }
}
