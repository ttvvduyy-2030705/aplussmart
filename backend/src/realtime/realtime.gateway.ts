import { WebSocketGateway, WebSocketServer } from '@nestjs/websockets';
import { Server } from 'socket.io';

@WebSocketGateway({ namespace: 'realtime', cors: true })
export class RealtimeGateway {
  @WebSocketServer() server?: Server;

  emitLockEvent(event: string, payload: unknown) {
    this.server?.emit(event, payload);
  }

  emitCustom(event: string, payload: unknown) {
    this.server?.emit(event, payload);
  }
}
