import { Body, Controller, Delete, Get, Param, Post, Query } from '@nestjs/common';
import { LocksService } from './locks.service';
import { CredentialType } from './smart-lock.adapter';

@Controller()
export class LocksController {
  constructor(private readonly locks: LocksService) {}

  @Get('health') health() { return { ok: true, service: 'aplus-lock-backend', version: '1.0.0-ui31' }; }
  @Get('locks') listLocks() { return this.locks.listLocks(); }
  @Get('locks/:id') getLock(@Param('id') id: string) { return this.locks.getLock(id); }
  @Post('locks/:id/lock') lock(@Param('id') id: string) { return this.locks.lock(id); }
  @Post('locks/:id/unlock') unlock(@Param('id') id: string) { return this.locks.unlock(id); }
  @Post('locks/:id/remote-unlock') remoteUnlock(@Param('id') id: string) { return this.locks.unlock(id, true); }

  @Get('locks/:id/credentials') credentials(@Param('id') id: string) { return this.locks.credentials(id); }
  @Post('locks/:id/passwords') password(@Param('id') id: string, @Body() b: any) { return this.locks.addCredential(id, 'Password', b.ownerName ?? 'Guest', b.label ?? 'Password', b.schedule ?? 'Always'); }
  @Post('locks/:id/fingerprints') fingerprint(@Param('id') id: string, @Body() b: any) { return this.locks.addCredential(id, 'Fingerprint', b.ownerName ?? 'User', b.label ?? 'Fingerprint', b.schedule ?? 'Always'); }
  @Post('locks/:id/faces') face(@Param('id') id: string, @Body() b: any) { return this.locks.addCredential(id, 'Face', b.ownerName ?? 'User', b.label ?? 'Face', b.schedule ?? 'Always'); }
  @Post('locks/:id/cards') card(@Param('id') id: string, @Body() b: any) { return this.locks.addCredential(id, 'Card', b.ownerName ?? 'Guest', b.label ?? 'Card', b.schedule ?? 'Check-in'); }
  @Post('locks/:id/remotes') remote(@Param('id') id: string, @Body() b: any) { return this.locks.addCredential(id, 'Remote', b.ownerName ?? 'Remote', b.label ?? 'Remote', b.schedule ?? 'Always'); }
  @Delete('locks/:id/credentials/:credentialId') revoke(@Param('credentialId') credentialId: string) { return this.locks.revokeCredential(credentialId); }

  @Get('records') records(@Query('lockId') lockId?: string) { return this.locks.records(lockId); }
  @Get('alerts') alerts() { return this.locks.alerts(); }
}
