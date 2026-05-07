# Backend API contract v1

Base URL: `/api`

## Auth

- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/refresh`

## Locks

- `GET /api/locks`
- `GET /api/locks/:id`
- `POST /api/locks/:id/lock`
- `POST /api/locks/:id/unlock`
- `POST /api/locks/:id/remote-unlock`

## Access credentials

- `GET /api/locks/:id/credentials`
- `POST /api/locks/:id/passwords`
- `POST /api/locks/:id/fingerprints`
- `POST /api/locks/:id/faces`
- `POST /api/locks/:id/cards`
- `POST /api/locks/:id/remotes`
- `DELETE /api/locks/:id/credentials/:credentialId`

## Management

- `GET /api/rooms`
- `POST /api/rooms`
- `GET /api/members`
- `POST /api/members`
- `GET /api/records`
- `GET /api/alerts`
- `POST /api/alerts/:id/ack`
- `GET /api/reports/summary`

## Realtime

- WebSocket namespace: `/realtime`
- Events: `lock.status`, `lock.alarm`, `lock.record`, `lock.battery`, `firmware.progress`

## Adapter rule

Mobile UI không gọi trực tiếp phần cứng. UI → `AplusLockSdk` → Backend/Adapter → Mock/BLE/MQTT/Cloud.
