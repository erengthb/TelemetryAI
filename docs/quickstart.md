# TelemetryAI Hizli Baslangic (5 dk)

Bu sayfa yerel ortamda calisan minimum kurulumu anlatir. Tum icerik ASCII uyumludur.

## On kosullar
- Java 21
- Node.js 18+
- Docker Desktop
- Git

## 1) DB'yi ayaga kaldir
```bash
cd infra/docker
docker compose up -d
```

## 2) Backend'i calistir
```bash
mvn -f backend/pom.xml spring-boot:run
```

## 3) Frontend'i calistir
```bash
cd frontend
npm install
npm run dev
```

## 4) UI'da ilk adimlar
1. Project olustur
2. API key uret
3. Schema import yap (ornek: docs/schema_examples/schema_v1.json)

## 5) Ingestion test (cURL)
```bash
curl -X POST http://localhost:8080/v1/events/batch \
  -H "X-Api-Key: <YOUR_API_KEY>" \
  -H "Content-Type: application/json" \
  -d @docs/payload_examples/events_batch_example.json
```

## 6) Dashboard ve raporlar
Dashboard ve AI rapor ekranlarinda veri gorunur. Veri yoksa:
- Backend loglarini kontrol et
- Schema uyumu ve API key dogrulugunu kontrol et

## Notlar
- /v1/events/batch endpoint'i gzip kabul eder.
- PII tespit edilirse event karantinaya gider.
