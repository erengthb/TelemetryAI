# Release Checklist (20.16)

## Paketleme
- [ ] backend build alin (jar)
- [ ] frontend build alin (dist)
- [ ] plugin klasoru (TelemetryAI) temiz ve derlenebilir

## Ornekler
- [ ] schema ornekleri: docs/schema_examples/
- [ ] payload ornekleri: docs/payload_examples/
- [ ] hizli baslangic: docs/quickstart.md

## Basit smoke test
- [ ] docker compose up
- [ ] backend calisiyor (/actuator/health)
- [ ] UI aciliyor
- [ ] events/batch test cagrisi 200 donuyor

## Dagitim notlari
- Backend: Railway
- Frontend: Vercel
- DB: managed Postgres (Railway/Neon/Supabase)
