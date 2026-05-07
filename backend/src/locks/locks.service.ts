import { Injectable } from '@nestjs/common';
import { CredentialType } from './smart-lock.adapter';
import { MockSmartLockAdapter } from './mock-smart-lock.adapter';

@Injectable()
export class LocksService {
  constructor(private readonly adapter: MockSmartLockAdapter) {}

  listLocks() { return this.adapter.listLocks(); }
  getLock(id: string) { return this.adapter.getLock(id); }
  lock(id: string) { return this.adapter.lock(id); }
  unlock(id: string, remote = false) { return this.adapter.unlock(id, remote); }
  credentials(lockId: string) { return this.adapter.listCredentials(lockId); }
  records(lockId?: string) { return this.adapter.listRecords(lockId); }
  alerts() { return this.adapter.listAlerts(); }
  addCredential(lockId: string, type: CredentialType, ownerName: string, label: string, schedule: string) {
    return this.adapter.addCredential(lockId, type, ownerName, label, schedule);
  }
  revokeCredential(id: string) { return this.adapter.revokeCredential(id); }
}
