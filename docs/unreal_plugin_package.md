# Unreal Plugin Package (Ozet)

Bu belge, Unreal plugin paketini hazirlarken izlenecek kisa adimlari listeler.

## Klasor yapisi
```
TelemetryAI/
  TelemetryAI.uplugin
  Source/
    TelemetryAI/
      Public/
      Private/
```

## Paketleme adimlari
1) `TelemetryAI` klasorunu temizle (gereksiz build artifakt yok)
2) Unreal Editor ile plugin build et
3) Paket olarak `TelemetryAI` klasorunu zip yap

## Kontrol listesi
- `TelemetryAI.uplugin` dogru
- `Source/` altinda kod var
- Plugin Editor'da aktif oluyor
