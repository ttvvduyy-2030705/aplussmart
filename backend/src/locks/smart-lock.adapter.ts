export type CredentialType = 'Password' | 'Fingerprint' | 'Face' | 'Card' | 'Remote' | 'Phone' | 'Nfc' | 'Admin';

export interface LockDeviceDto {
  id: string;
  name: string;
  room: string;
  mode: 'Home' | 'Hotel' | 'Apartment' | 'Office' | 'Campus';
  isLocked: boolean;
  isOnline: boolean;
  battery: number;
  signal: number;
  doorOpen: boolean;
  firmware: string;
}

export interface SmartLockAdapter {
  listLocks(): Promise<LockDeviceDto[]>;
  getLock(id: string): Promise<LockDeviceDto>;
  lock(id: string): Promise<LockDeviceDto>;
  unlock(id: string, remote?: boolean): Promise<LockDeviceDto>;
  addCredential(lockId: string, type: CredentialType, ownerName: string, label: string, schedule: string): Promise<unknown>;
}
