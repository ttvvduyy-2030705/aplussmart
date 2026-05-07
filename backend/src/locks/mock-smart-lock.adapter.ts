import { Injectable, NotFoundException } from '@nestjs/common';
import { CredentialType, LockDeviceDto, SmartLockAdapter } from './smart-lock.adapter';
import { RealtimeGateway } from '../realtime/realtime.gateway';

@Injectable()
export class MockSmartLockAdapter implements SmartLockAdapter {
  private locks: LockDeviceDto[] = [
    { id: 'front-door', name: 'Cửa chính', room: 'Sảnh chính', mode: 'Apartment', isLocked: true, isOnline: true, battery: 86, signal: 92, doorOpen: false, firmware: '1.0.8' },
    { id: 'office-door', name: 'Phòng làm việc', room: 'Tầng 2', mode: 'Office', isLocked: false, isOnline: true, battery: 64, signal: 78, doorOpen: false, firmware: '1.0.7' },
    { id: 'hotel-203', name: 'Phòng 203', room: 'Khách sạn', mode: 'Hotel', isLocked: true, isOnline: false, battery: 24, signal: 35, doorOpen: false, firmware: '1.0.4' },
  ];

  private credentials: any[] = [];
  private records: any[] = [];
  private alerts: any[] = [
    { id: 'alert-1', lockId: 'hotel-203', title: 'Khóa offline', message: 'Gateway mất kết nối', severity: 'Warning', unread: true },
    { id: 'alert-2', lockId: 'hotel-203', title: 'Pin yếu', message: 'Pin còn 24%', severity: 'Warning', unread: true },
  ];

  constructor(private readonly realtime: RealtimeGateway) {}

  async listLocks() { return this.locks; }
  async getLock(id: string) { return this.find(id); }
  async listCredentials(lockId: string) { return this.credentials.filter(c => c.lockId === lockId); }
  async listRecords(lockId?: string) { return this.records.filter(r => !lockId || r.lockId === lockId); }
  async listAlerts() { return this.alerts; }

  async lock(id: string) {
    const lock = this.update(id, { isLocked: true });
    this.record(id, 'Đã khóa cửa', 'Phone');
    this.realtime.emitLockEvent('lock.status', lock);
    return lock;
  }

  async unlock(id: string, remote = false) {
    const lock = this.update(id, { isLocked: false });
    this.record(id, remote ? 'Mở khóa từ xa' : 'Mở khóa bằng app', 'Phone');
    this.realtime.emitLockEvent('lock.status', lock);
    return lock;
  }

  async addCredential(lockId: string, type: CredentialType, ownerName: string, label: string, schedule: string) {
    this.find(lockId);
    const credential = { id: `${type.toLowerCase()}-${Date.now()}`, lockId, type, ownerName, label, schedule, active: true };
    this.credentials.unshift(credential);
    this.record(lockId, `Thêm ${type} cho ${ownerName}`, type);
    this.realtime.emitCustom('credential.added', credential);
    return credential;
  }

  async revokeCredential(credentialId: string) {
    this.credentials = this.credentials.map(c => c.id === credentialId ? { ...c, active: false } : c);
    this.realtime.emitCustom('credential.revoked', { credentialId });
    return { ok: true };
  }

  private find(id: string) {
    const lock = this.locks.find(l => l.id === id);
    if (!lock) throw new NotFoundException(`Lock ${id} not found`);
    return lock;
  }

  private update(id: string, patch: Partial<LockDeviceDto>) {
    const lock = this.find(id);
    Object.assign(lock, patch);
    return lock;
  }

  private record(lockId: string, title: string, method: string) {
    this.records.unshift({ id: `rec-${Date.now()}`, lockId, title, method, success: true, timeText: 'Vừa xong' });
  }
}
