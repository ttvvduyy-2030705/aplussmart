import { Body, Controller, Post } from '@nestjs/common';

@Controller('auth')
export class AuthController {
  @Post('login')
  login(@Body() body: { email?: string; phone?: string; password?: string }) {
    return {
      user: { id: 'owner-1', name: 'Aplus Owner', email: body.email ?? 'admin@aplus.vn', role: 'Owner' },
      accessToken: 'mock-access-token',
      refreshToken: 'mock-refresh-token',
      expiresIn: 3600,
    };
  }

  @Post('register')
  register(@Body() body: { name?: string; email?: string; phone?: string }) {
    return { ok: true, user: { id: 'owner-new', name: body.name ?? 'New Owner', email: body.email, phone: body.phone } };
  }

  @Post('refresh')
  refresh() {
    return { accessToken: 'mock-access-token-refreshed', expiresIn: 3600 };
  }
}
