import { Injectable } from '@nestjs/common';

@Injectable()
export class MqttAdapter {
  // Stub để batch sau nối MQTT broker thật.
  async publishCommand(lockId: string, command: string, payload: Record<string, unknown>) {
    return { queued: true, transport: 'mqtt-stub', lockId, command, payload };
  }
}
