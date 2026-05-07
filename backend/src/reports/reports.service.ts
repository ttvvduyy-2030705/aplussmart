import { Injectable } from '@nestjs/common';

@Injectable()
export class ReportsService {
  summary() {
    return {
      unlockToday: 18,
      activeCredentials: 9,
      warningLocks: 2,
      topMethods: [
        { method: 'Password', count: 8 },
        { method: 'App', count: 6 },
        { method: 'Card', count: 4 },
      ],
    };
  }
}
