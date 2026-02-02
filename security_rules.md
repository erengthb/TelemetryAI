# Yazılım Hataları / Bug Kataloğu (Aşırı Detaylı + Çok Çeşitli)

> Bu doküman “tek tek tüm bug’lar”ı sayamaz (sonsuz kombinasyon), ama pratikte yazılım dünyasında bug’ların **neredeyse tamamını kapsayan hata sınıflarını** ve her sınıfın altında görülen **çok sayıda alt-hata türünü** verir.  
> Her madde: **Nedir → Belirti → Tipik neden → Tespit → Fix → Önleme**.

---

## 0) En sık görülen “meta-hatalar” (her şeyi bozan kök nedenler)
- **Yanlış varsayım**: “Bu asla null gelmez / asla 0 olmaz / asla eşzamanlı olmaz”
- **Sınırların net olmaması**: “Kim sorumlu? UI mı backend mi? DB mi servis mi?”
- **Sözleşme yokluğu**: API contract / schema / event format / versioning belirsiz
- **Gözlemlenebilirlik yokluğu**: Prod’da bug var ama kanıt yok (log/trace/metric)
- **Test stratejisi yokluğu**: Unit var ama entegrasyon, contract, e2e yok
- **Zaman ve dağıtıklık hafife alınması**: Timeout, retry, ordering, clock skew

---

## 1) Gereksinim / Ürün / UX kaynaklı bug’lar

### 1.1 Belirsiz gereksinim
- **Belirti:** “Ama ben böyle istemiştim” tartışması
- **Neden:** Ölçü yok (hızlı, güvenli, kolay)
- **Tespit:** Kabul kriterleri yazılamıyor
- **Fix:** Metriğe bağla (`p95 < 300ms`), örnek senaryo ekle
- **Önleme:** PRD + Acceptance Criteria + örnek input/output tabloları

### 1.2 Çelişen gereksinimler
- **Belirti:** UI başka, backend başka davranır
- **Fix:** Tek “source of truth”; ADR (Architecture Decision Record) yaz
- **Önleme:** Versiyonlu spec + değişiklik günlüğü

### 1.3 Edge-case unutma (empty/max/invalid)
- **Belirti:** “Nadiren” crash
- **Fix:** Edge-case matrix (empty, 1 item, max, invalid, slow network, offline)
- **Önleme:** Test case şablonu + property-based test

### 1.4 UX “hata”ları (teknik değil ama üretimde bug gibi)
- **Örnekler:**
  - Loading yok → kullanıcı tekrar tıklar → double submit
  - Yanlış hata mesajı → kullanıcı yanlış aksiyon alır
  - Geri tuşu (Android) / swipe-back (iOS) state’i bozar
- **Önleme:** UX state machine + retry/backoff UI + disable/lock

---

## 2) Kod mantığı / iş kuralı bug’ları (klasik)

### 2.1 Off-by-one (sınır hatası)
- **Belirti:** Son eleman eksik/ fazla
- **Neden:** inclusive/exclusive karışır
- **Tespit:** Sınır testleri (0,1,2,n-1,n)
- **Fix:** Standard helper (`rangeExclusive`) + test
- **Önleme:** Sınır tablosu + lint

### 2.2 Yanlış karşılaştırma / eşitlik
- `==` vs `===`, case sensitivity, locale compare, unicode compare
- “0”, 0, false farkı
- **Önleme:** Tip güvenliği + strict compare

### 2.3 Default değer bug’ı
- **Belirti:** Yeni kullanıcıda sistem bozuk, eskilerde değil
- **Neden:** Default config, feature flag default’u yanlış
- **Önleme:** “Safe default” politikası + config testleri

### 2.4 Durum makinesi eksikliği
- **Belirti:** “İşlem yarıda kaldı” durumları yönetilemez
- **Önleme:** State machine (pending→processing→done/failed) + idempotency

---

## 3) Tip sistemi / JSON / Validasyon bug’ları

### 3.1 Şema drift (client-server mismatch)
- **Belirti:** Prod’da parse hatası
- **Neden:** Alan adı değişti ama client güncellenmedi
- **Tespit:** Contract tests / schema registry
- **Fix:** Backward compatible değişiklik + versioning
- **Önleme:** OpenAPI/Protobuf/JSON schema + CI doğrulama

### 3.2 Partial update bug’ları
- **Belirti:** PATCH ile alanlar “silinir”
- **Neden:** `null` vs “unset” farkı korunmaz
- **Fix:** Patch semantics belirle (RFC6902 / merge patch)
- **Önleme:** DTO kuralları + test

### 3.3 Validation eksikliği / fazla katı validation
- **Eksik:** Kötü veri DB’ye girer
- **Fazla katı:** Gerçek kullanıcı datası reddedilir (isimde apostrof)
- **Önleme:** Allowlist + normalize + iyi error messages

---

## 4) Tarih/Saat/Zaman bug’ları (en sinsi)

### 4.1 UTC / local karışması
- **Belirti:** 1 gün kayma
- **Fix:** Storage UTC, UI local; API her zaman ISO8601 + timezone
- **Önleme:** Time policy dokümanı + test

### 4.2 DST (yaz saati) bug’ı
- **Belirti:** 23/25 saatlik gün, cron şaşırır
- **Fix:** Wall-clock yerine UTC; cron timezone explicit
- **Önleme:** DST simülasyon testleri

### 4.3 “Now” bağımlılığı
- **Belirti:** Testler flaky, prod’da farklı davranır
- **Fix:** Fake clock / injectable time provider
- **Önleme:** Zaman soyutlaması

### 4.4 TTL/expiry hataları
- Token, cache TTL, signed URL süreleri yanlış
- **Önleme:** TTL budget + alarm + audit

---

## 5) Sayısal / finans / yuvarlama bug’ları

### 5.1 Float ile para hesaplama
- **Belirti:** Kuruş sapması
- **Fix:** Integer “kuruş” / Decimal type
- **Önleme:** Finans işlemlerinde float yasak

### 5.2 Rounding mode bug’ları
- Bankers rounding vs normal rounding
- **Önleme:** Rounding standardı + test dataset

### 5.3 Overflow/underflow
- 32-bit int taşması, timestamp çarpma
- **Önleme:** BigInt/64-bit + sınır testleri

---

## 6) Veritabanı / ORM / SQL bug’ları (çok geniş)

### 6.1 N+1 Query
- **Belirti:** Veri büyüyünce p95 fırlar
- **Tespit:** Query count metric + ORM debug log
- **Fix:** Eager loading / join / batch fetch
- **Önleme:** “Query budget” (ör: endpoint başı max 10 query)

### 6.2 Yanlış indeks stratejisi
- Missing index, wrong composite order, low selectivity index
- **Tespit:** `EXPLAIN ANALYZE`, slow query log
- **Fix:** Composite index (where + order by)
- **Önleme:** Query review + load test

### 6.3 Transaction eksikliği
- **Belirti:** Yarım kayıt, tutarsız stok
- **Fix:** DB transaction / outbox pattern
- **Önleme:** Kritik akışlar için atomicity spec

### 6.4 Deadlock / lock contention
- **Belirti:** Random fail, retry ile düzelir
- **Fix:** Kilit sırası standardı, kısa transaction
- **Önleme:** Deadlock retry + backoff + metrik

### 6.5 Isolation yanlışlığı
- Dirty read, phantom read, non-repeatable read
- **Önleme:** Isolation seviyesi kararı + test

### 6.6 Soft delete tuzakları
- Filtre unutulur, unique constraint bozulur, join’lerde sızıntı
- **Fix:** Default scope/view, unique constraint için `WHERE deleted_at IS NULL` benzeri strateji
- **Önleme:** Query builder policy

### 6.7 Migration tuzakları
- Long-running migration lock’lar
- Backfill job yok
- Deploy sırası kırılır
- **Fix:** Expand→migrate→contract, online migration
- **Önleme:** Migration review checklist

### 6.8 Connection pool bug’ları
- Leak, pool size yanlış, idle timeout
- **Önleme:** Pool metrics + alarm

---

## 7) Cache / CDN / Tutarlılık bug’ları

### 7.1 Cache invalidation hataları
- Wrong key, wrong TTL, wrong scope
- **Fix:** Versioned keys, event invalidation, write-through
- **Önleme:** “Cache contract” dokümanı

### 7.2 Stampede (thundering herd)
- **Fix:** single-flight, request coalescing, jitter TTL
- **Önleme:** Hot endpoint koruması

### 7.3 Stale-while-revalidate yanlışlığı
- Kullanıcı uzun süre eski veri görür
- **Fix:** max stale limit + background refresh
- **Önleme:** SWR policy

### 7.4 CDN cache poisoning / wrong headers
- `Cache-Control` yanlış → private data cache’lenir
- **Önleme:** Header testleri + security review

---

## 8) Concurrency / paralellik bug’ları

### 8.1 Race condition
- **Örnek:** Stok azaltma: read→check→write
- **Fix:** Atomic update (`UPDATE ... SET stock = stock-1 WHERE stock>0`)
- **Önleme:** Optimistic locking, unique constraints

### 8.2 Lost update
- **Fix:** version column (OCC), ETag
- **Önleme:** “Update = check version” standardı

### 8.3 Double submit / replay
- **Fix:** Idempotency key + dedupe store
- **Önleme:** Her kritik POST idempotent

### 8.4 Thread-safety / shared mutable state
- Singleton cache map, global variables
- **Önleme:** Immutable + safe concurrency primitives

---

## 9) Dağıtık sistem bug’ları (network + retry + ordering)

### 9.1 Timeout yok / yanlış timeout
- **Belirti:** Kuyruk gibi birikir
- **Fix:** end-to-end timeout budget
- **Önleme:** Standard timeout matrix (client/server/LB)

### 9.2 Retry storm
- **Fix:** exponential backoff + jitter + circuit breaker
- **Önleme:** Retry policy tek yerde

### 9.3 Circuit breaker yok
- **Belirti:** Bir servis ölür, herkes ona abanır
- **Fix:** CB + fallback + bulkhead
- **Önleme:** Resilience patterns

### 9.4 Out-of-order events
- **Fix:** sequence number, versioning, reorder buffer
- **Önleme:** Event contract + consumer idempotency

### 9.5 At-least-once delivery
- **Fix:** exactly-once değil, dedupe/idempotency
- **Önleme:** Consumer tasarım standardı

### 9.6 Clock skew
- **Fix:** NTP + tolerant validation
- **Önleme:** Clock drift alarmı

---

## 10) API tasarımı / HTTP bug’ları

### 10.1 Yanlış status code
- 200 ile hata döndürmek, 500 ile client hatası döndürmek
- **Önleme:** API style guide + contract tests

### 10.2 Hata formatı tutarsız
- **Fix:** Standard error schema (`code`, `message`, `details`, `traceId`)
- **Önleme:** Shared error middleware

### 10.3 Pagination bug’ları
- Offset ile kayma, cursor yanlış
- **Fix:** keyset + tie-breaker (unique id)
- **Önleme:** Pagination contract test

### 10.4 Rate limit yok / yanlış
- **Fix:** token bucket + key selection (IP/user/token)
- **Önleme:** Abuse test + limit policy

### 10.5 Webhook bug’ları
- Signature doğrulama yok, replay koruma yok, event ordering yok
- **Önleme:** Webhook standardı + idempotency

---

## 11) Güvenlik bug’ları (detaylı geniş liste)

### 11.1 Broken Access Control (IDOR)
- **Belirti:** Başka kullanıcının verisi görünür
- **Fix:** Policy check (resource owner)
- **Önleme:** Yetkilendirme her endpoint’te zorunlu

### 11.2 Injection türleri
- SQL injection
- NoSQL injection
- LDAP injection
- Command injection
- Template injection (SSTI)
- GraphQL injection (query depth abuse)
- **Önleme:** Param binding + allowlist + WAF (gerektiğinde)

### 11.3 XSS türleri
- Reflected
- Stored
- DOM-based
- **Fix:** output escaping + CSP + sanitize
- **Önleme:** Auto-escape template + security tests

### 11.4 CSRF
- **Fix:** SameSite + CSRF token + double submit cookie
- **Önleme:** State-changing request’lerde zorunlu

### 11.5 SSRF
- URL fetch endpoint’leri, image proxy’ler
- DNS rebinding, internal metadata access (cloud)
- **Fix:** allowlist + egress filter + block private IP ranges
- **Önleme:** SSRF test suite

### 11.6 Auth bug’ları
- JWT alg=none, wrong issuer/audience, refresh token rotasyonu yok
- Session fixation
- Password reset token reuse
- OTP brute force (rate limit yok)
- **Önleme:** Auth checklist + pentest

### 11.7 Crypto bug’ları
- Weak RNG
- Insecure hashing (md5/sha1) password için
- Wrong salt/iterations
- TLS verification disabled
- **Önleme:** Kütüphane standardı + code review “crypto gate”

### 11.8 Secrets sızıntısı
- Log’larda token
- Client bundle içinde API key
- Repo’da .env
- **Önleme:** secret scanning + redaction + vault

### 11.9 Supply-chain
- Dependency hijack, typosquatting, lockfile drift
- **Önleme:** SBOM, pin versions, audit, sigstore

---

## 12) Frontend / Mobile bug’ları (çok çeşit)

### 12.1 UI state bug’ları
- Stale UI (cache)
- Optimistic update rollback yok
- Concurrent navigation (2 kez push)
- **Önleme:** Single source of truth + state machine

### 12.2 Performance UI bug’ları
- Re-render storm
- Heavy list without virtualization
- Main thread blocking (image decode)
- **Önleme:** profiling + lazy loading + memoization

### 12.3 Memory leak
- Unsubscribed listeners
- Timers not disposed
- Large image cache
- **Önleme:** lifecycle discipline + leak detector

### 12.4 Offline-first bug’ları
- Conflict resolution yok
- Queue replay idempotent değil
- Partial sync
- **Önleme:** sync protocol + conflict strategy

### 12.5 Push notification bug’ları
- Token refresh handling yok
- iOS permission flow yanlış
- Notification click deep-link state bozar
- **Önleme:** push lifecycle test matrix

### 12.6 i18n/RTL/typography bug’ları
- Türkçe İ/i case-folding
- Metin taşması
- RTL layout bozulması
- **Önleme:** pseudo-localization + locale test

### 12.7 Accessibility bug’ları
- Focus order, contrast, screen reader labels yok
- **Önleme:** a11y checklist + automated scans

---

## 13) Networking / DNS / TLS bug’ları

- **DNS caching**: yanlış TTL, stale DNS
- **Connection pooling**: socket exhaustion
- **HTTP keep-alive**: yanlış ayar
- **Proxy headers**: `X-Forwarded-Proto` yanlış → redirect loop
- **TLS issues**: chain incomplete, SNI, cert rotation bug
- **H2/H3 quirks**: multiplexing, middlebox issues
- **Önleme:** network observability + synthetic tests

---

## 14) Performans / ölçeklenebilirlik bug’ları (derin)

### 14.1 N+1 sadece DB değil
- N+1 HTTP calls
- N+1 filesystem reads
- N+1 cache misses
- **Önleme:** batch/aggregation + metrics

### 14.2 Hot partitions
- DB shard / Kafka partition / Redis cluster hot spot
- **Fix:** partition key redesign, salting, load distribution
- **Önleme:** access pattern review

### 14.3 Backpressure yok
- Queue consumer yetişmez → memory blow-up
- **Fix:** bounded queues, rate limit, shed load
- **Önleme:** saturation metrics

### 14.4 OOM ve memory fragmentation
- Büyük objeler, leak, native buffers
- **Önleme:** heap profiles + limits

---

## 15) Test bug’ları (neden testler yetmiyor?)

- **Flaky tests**: time/random/network
- **Mock hell**: her şey mock → gerçeği yakalamaz
- **Test isolation yok**: DB state paylaşımı
- **E2E yok**: kritik akışlar test edilmemiş
- **Önleme:** test pyramid + contract tests + deterministic time

---

## 16) CI/CD / Build / Release bug’ları

- **Env drift**: dev/stage/prod farklı
- **Wrong build artifact**: eski commit deploy
- **Feature flag misconfig**: prod’da yanlış default
- **Migration order**: schema/app uyumsuz
- **Rollback yok**: incident uzar
- **Önleme:** immutable artifacts + deploy runbook + canary

---

## 17) Container / Kubernetes / Infra bug’ları

- **Readiness/Liveness yanlış**: restart loop
- **Resource limits yok/yanlış**: OOMKill
- **ConfigMap/Secret drift**
- **Clock skew in nodes**
- **NetworkPolicy yanlış**: servisler konuşamaz
- **Volume permissions**: app yazamaz
- **Önleme:** infra tests + probes standardı

---

## 18) Observability / Logging bug’ları

- **Correlation ID yok**
- **Log levels yanlış**
- **PII loglama**
- **Metric cardinality patlaması** (label explosion)
- **Tracing sampling yanlış**
- **Önleme:** observability standards + redaction + budgets

---

## 19) Veri pipeline / analitik bug’ları

- **Double counting** (event replay)
- **Attribution yanlış** (campaign)
- **Time window bug’ı** (timezone)
- **Schema evolution** (event fields değişir)
- **Late arriving data** (gecikmeli event)
- **Önleme:** event versioning + dedupe + data contracts

---

## 20) Arama / sıralama / filtreleme bug’ları (çok sık)
- **Locale collation** (Türkçe İ/i)
- **Unicode normalization**
- **Stable sort beklentisi**
- **Pagination + search combined** (cursor invalid)
- **Relevance tuning yok**
- **Filter injection**
- **Önleme:** collation strategy + normalization pipeline + tests

---

## 21) Ödeme / abonelik / entitlement bug’ları (en pahalı bug’lar)
- **Idempotency yok** → double charge
- **Webhook doğrulama yok** → fake premium
- **Race condition**: entitlement update vs UI check
- **Grace period / retry loop bug’ı**
- **Refund/chargeback state machine yok**
- **Önleme:** billing spec + signature verify + OCC + audit

---

## 22) Dosya / medya / storage bug’ları
- **Orphan files** (DB silindi file kaldı)
- **Public bucket mistake**
- **Signed URL TTL wrong**
- **MIME spoofing**
- **Large file timeouts**
- **Önleme:** lifecycle rules + server-side sniff + GC job

---

## 23) AI/LLM entegrasyon bug’ları (modern)
- **Prompt injection**
- **Hallucination’a güvenmek**
- **PII leakage**
- **Cost blow-up (token)**
- **Non-determinism** (format bozulur)
- **Tool abuse** (modelin yetkisiz aksiyonları)
- **Önleme:** output validation + policy isolation + quotas + RAG w/ checks

---

## 24) Hızlı “Sınıflandırma şablonu” (Bug raporu standardı)
Her bug için aşağıyı doldur:
- **Etki:** data loss? security? revenue? UX?
- **Yüzde:** kaç kullanıcı etkileniyor?
- **Tekrarlanabilirlik:** %100 mü, nadir mi?
- **Kök neden sınıfı:** (validation / concurrency / migration / auth / time / cache)
- **Tespit sinyali:** hangi metric/log?
- **Fix:** hızlı hotfix + kalıcı çözüm + önleme testi

---

## 25) “Daha da genişletmek” için: İstersen 2. versiyon
Bu dokümanı daha da büyütüp (gerçekten devasa bir katalog):
- Her alt maddeye **mini örnek senaryo** (2-3 satır),
- “**Yanlış implementasyon** vs **doğru implementasyon**” karşılaştırması,
- Her sınıf için **otomatik test reçetesi** (unit/integration/contract/e2e),
- Her sınıf için **metrik/alert önerileri** (p95, error rate, saturation, queue lag)
ekleyebilirim.

> Ama şimdilik bu sürüm bile, pratikte gördüğün bug’ların %95+’ini sınıflandırıp yakalamaya yetecek kadar geniş bir çerçeve verir.
