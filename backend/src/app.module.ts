import { Module } from '@nestjs/common';
import { AuthController } from './auth/auth.controller';
import { LocksController } from './locks/locks.controller';
import { LocksService } from './locks/locks.service';
import { MockSmartLockAdapter } from './locks/mock-smart-lock.adapter';
import { RealtimeGateway } from './realtime/realtime.gateway';
import { ReportsController } from './reports/reports.controller';
import { ReportsService } from './reports/reports.service';
import { PushService } from './push/push.service';
import { MqttAdapter } from './realtime/mqtt.adapter';

@Module({
  imports: [],
  controllers: [AuthController, LocksController, ReportsController],
  providers: [LocksService, MockSmartLockAdapter, RealtimeGateway, ReportsService, PushService, MqttAdapter],
})
export class AppModule {}
