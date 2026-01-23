TelemetryAI
===========

TelemetryAI; Unreal Engine (ilk etap) ve opsiyonel Unity icin telemetry toplama, Java backend uzerinden dogrulama + karantina, React panel uzerinden analiz ve AI raporlama saglayan bir urundur.

Moduller
--------
- backend/: Spring Boot API + DB migrations
- frontend/: React panel
- plugins/unreal/: Unreal plugin
- infra/docker/: Postgres docker compose
- docs/: ornekler ve hizli baslangic

Hizli baslangic
--------------
Detayli kurulum icin `docs/quickstart.md` dosyasina bak.

Ornekler
--------
- Schema: `docs/schema_examples/schema_v1.json`
- Payload: `docs/payload_examples/events_batch_example.json`

Dizin yapisi (ozet)
-------------------
backend/
frontend/
plugins/
  unreal/
  unity/
infra/
docs/

Notlar
------
- API key DB'de hash olarak tutulur.
- Schema uyumsuz event'ler karantinaya gider.
- /v1/events/batch endpoint'i gzip kabul eder.
