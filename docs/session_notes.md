Session Notes - TelemetryAI
===========================

Genel
-----
- Calisma dizini: C:\Eren\TelemetryAI
- Diger proje dizinine dokunma: C:\Eren\wsSpringApp\ws
- Dokuman kaynak: TelemetryAI.pdf ve TelemetryAI.docx (root)

Repo yapisi
-----------
- backend/ Spring Boot
- frontend/ React + Vite
- plugins/unreal/ Unreal plugin
- infra/docker/ postgres + pgadmin compose
- docs/ ornekler + quickstart + checklist
- README.md guncellendi

Backend (genel)
--------------
- Spring Boot 3.x, Java 21
- JWT config: backend/src/main/resources/application.yml
- Varsayilan DB:
  - DB_URL: jdbc:postgresql://localhost:5432/telemetryai
  - DB_USER: telemetryai
  - DB_PASSWORD: telemetryai
- Flyway migration aktif

Frontend
--------
- React + TS + Vite
- UI metinleri Turkce (ASCII)
- API entegrasyon katmani var: frontend/src/api/*
- Proje/env secimi localStorage ile kalici
- API key revoke butonu baglandi
- YZ rapor JSON'lari kartlara ayrildi

Frontend endpoints (kullanim)
-----------------------------
- /v1/auth/login, /v1/auth/me
- /v1/orgs, /v1/projects, /v1/projects/{id}/environments
- /v1/projects/{id}/keys + /keys/rotate + /keys/revoke
- /v1/projects/{id}/schema/current|import|export
- /v1/projects/{id}/dashboard/overview|funnel
- /v1/projects/{id}/quarantine
- /v1/projects/{id}/reports/daily|weekly

Unreal plugin (20.15)
---------------------
- Plugin iskeleti: plugins/unreal/TelemetryAI
- Module, settings, client, blueprint library eklendi
- Game thread disina cikma eklendi (ThreadPool)
- Gzip gonderim opsiyonu eklendi
- Spool daha saglam: pending/processing, retry, silme kontrolu
- Ana dosyalar:
  - TelemetryAI.uplugin
  - TelemetryAI.Build.cs
  - TelemetryAISettings.h/.cpp
  - TelemetryAIClient.h/.cpp
  - TelemetryAIBlueprintLibrary.h/.cpp

Docs / Release (20.16)
----------------------
- docs/schema_examples/schema_v1.json
- docs/schema_examples/schema_min.json
- docs/payload_examples/events_batch_example.json
- docs/payload_examples/events_batch_invalid_example.json
- docs/payload_examples/events_batch_response_example.json
- docs/quickstart.md
- docs/release_checklist.md
- docs/unreal_plugin_package.md
- README.md guncellendi

Docker Notu
-----------
- Docker komutu bulunamadi -> Docker Desktop kurulu degil.
- Cozum: Docker Desktop kur, yeniden baslat, docker --version kontrol et.

Calistirma notlari
------------------
- DB: cd infra/docker + docker compose up -d
- Backend: mvn -f backend/pom.xml spring-boot:run
- Frontend: cd frontend + npm install + npm run dev
- UI icin VITE_API_BASE_URL varsayilan http://localhost:8080

Commit mesajlari (ornek)
------------------------
- Frontend entegrasyonlar: feat: wire frontend api, persist project/env, add key revoke action
- Unreal iyilestirme: feat: move batch prep off game thread, add gzip, harden spool flow
- Docs/release: docs: add quickstart, examples, and release checklist
