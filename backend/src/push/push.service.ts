import { Injectable } from '@nestjs/common';

@Injectable()
export class PushService {
  async sendFcm(token: string, title: string, body: string) {
    return { queued: true, provider: 'fcm-stub', token, title, body };
  }

  async sendApns(token: string, title: string, body: string) {
    return { queued: true, provider: 'apns-stub', token, title, body };
  }
}
