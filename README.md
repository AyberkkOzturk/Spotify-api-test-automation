# 🎵 Spotify API Test Otomasyonu

Spotify Web API'si üzerinde **REST Assured**, **TestNG** ve **Allure** kullanılarak geliştirilmiş, katmanlı mimariye (*layered architecture*) sahip bir API test otomasyon çatısıdır. Proje, Spotify'ın **OAuth 2.0** kimlik doğrulama akışını yönetir ve **Playlist** uç noktalarını (oluşturma / güncelleme / okuma) uçtan uca test eder; hem pozitif hem negatif senaryoları kapsar.

---

## 📋 İçindekiler

- Genel Bakış
- Özellikler
- Proje Yapısı
- Mimari
- Ön Gereksinimler
- Yapılandırma
- Testleri Çalıştırma
- Allure Raporu
- Test Senaryoları
- Geliştirme Alanları

---

## 🔎 Genel Bakış

Çatı, sorumlulukları birbirinden ayrılmış katmanlar üzerine kuruludur. Test sınıfları HTTP'nin veya kimlik doğrulamanın ayrıntılarını bilmez; yalnızca iş odaklı (*business-level*) API metotlarını çağırır. Böylece testler okunabilir kalır, tekrar eden kod azalır ve bakım kolaylaşır.

Kimlik doğrulama tarafında proje, Spotify'ın **Authorization Code** akışıyla alınmış bir `refresh_token` kullanarak çalışma anında `access_token` üretir, bu token'ı önbelleğe alır ve süresi dolmadan otomatik olarak yeniler.

---

## ✨ Özellikler

- **Katmanlı mimari** — Test, Uygulama API'si, Çekirdek (core), POJO ve Yardımcı (utils) katmanları birbirinden ayrık.
- **Otomatik token yönetimi** — `TokenManager`, token'ı önbelleğe alır, süre dolumunu kontrol eder ve `refresh_token` ile yeniler (thread-safe).
- **POJO tabanlı veri eşleme** — İstek/yanıt gövdeleri Jackson + Lombok ile serialize/deserialize edilir.
- **Paralel test koşumu** — TestNG + Surefire ile metot bazında, 10 thread'e kadar paralel çalışma.
- **Dinamik test verisi** — JavaFaker ile rastgele playlist adı ve açıklaması üretimi.
- **Allure raporlama** — `@Step`, `@Epic`, `@Feature`, `@Story` anotasyonları ve istek/yanıt loglarıyla ayrıntılı raporlar.
- **Spesifikasyon (spec) yeniden kullanımı** — İstek/yanıt kuralları `SpecBuilder` içinde merkezi olarak tanımlanır.
- **Harici yapılandırma** — Kimlik bilgileri ve test verisi `.properties` dosyalarından okunur; kod içine gömülmez.

---

## 🧰 Teknoloji Yığını

| Katman | Teknoloji | Sürüm |
|---|---|---|
| Dil | Java | 21 |
| Build | Maven | 3.x |
| API test | REST Assured | 6.0.0 |
| Test çatısı | TestNG | 7.12.0 |
| Raporlama | Allure (TestNG + REST Assured) | 2.34.0 |
| JSON işleme | Jackson Databind | 2.21.2 |
| Boilerplate azaltma | Lombok | 1.18.42 |
| Test verisi | JavaFaker | 1.0.2 |
| Doğrulama (assertion) | Hamcrest / JSONAssert | - / 1.5.3 |
| Allure entegrasyonu | AspectJ Weaver | 1.9.25.1 |

---

## 📂 Proje Yapısı

```
spotify-api-test-automation/
├─ src/
│  └─ test/
│     ├─ java/
│     │  └─ com/spotify/oauth2/
│     │     ├─ api/                     # Çekirdek API katmanı
│     │     │  ├─ applicationApi/
│     │     │  │  └─ PlaylistApi.java   # İş odaklı Playlist uç noktaları
│     │     │  ├─ RestResource.java     # Jenerik HTTP metotları (GET/POST/PUT)
│     │     │  ├─ Route.java            # Endpoint path sabitleri
│     │     │  ├─ SpecBuilder.java      # İstek/yanıt spesifikasyonları
│     │     │  ├─ StatusCode.java       # Durum kodu + mesaj enum'u
│     │     │  └─ TokenManager.java     # OAuth token üretimi/yenileme
│     │     ├─ pojo/                    # Model (POJO) sınıfları
│     │     │  ├─ Playlist.java
│     │     │  ├─ Owner.java
│     │     │  ├─ Followers.java
│     │     │  ├─ Items.java
│     │     │  ├─ ExternalUrls.java
│     │     │  ├─ ExternalUrls__1.java
│     │     │  ├─ Error.java
│     │     │  └─ InnerError.java
│     │     ├─ tests/                   # Test sınıfları
│     │     │  ├─ BaseTest.java         # Ortak @BeforeMethod (loglama)
│     │     │  └─ PlaylistTests.java    # Playlist senaryoları
│     │     └─ utils/                   # Yardımcı sınıflar
│     │        ├─ ConfigLoader.java     # config.properties okuyucu (singleton)
│     │        ├─ DataLoader.java       # data.properties okuyucu (singleton)
│     │        ├─ PropertyUtils.java    # .properties dosyası yükleyici
│     │        ├─ FakerUtils.java       # Rastgele veri üretimi
│     │        └─ AssertUtils.java      # Yeniden kullanılabilir assertion'lar
│     └─ resources/
│        ├─ config.properties           # Kimlik bilgileri (gizli!)
│        ├─ data.properties             # Test verisi (ör. playlist_id)
│        └─ allure.properties           # Allure sonuç dizini ayarı
├─ .gitignore
└─ pom.xml
```

> **Not:** `PlaylistApi.java` sınıfı, test sınıfları tarafından çağrılan iş odaklı API katmanıdır. İçeride `TokenManager`'dan token alıp `Route` sabitleriyle `RestResource`'u çağırarak `post`, `update` ve `get` işlemlerini kapsar.

---

## 🏗 Mimari

Proje, sorumlulukları aşağıdaki katmanlara ayırır:

**1. Test Katmanı (`tests`)**
Senaryoların tanımlandığı yerdir. `PlaylistTests`, `BaseTest`'ten türer. `BaseTest` her test öncesi (`@BeforeMethod`) test adını ve çalıştığı thread id'sini loglayarak paralel koşumu görünür kılar. Testler yalnızca `PlaylistApi` + `AssertUtils` + POJO'lar ile konuşur; HTTP detaylarıyla ilgilenmez.

**2. Uygulama API'si Katmanı (`api/applicationApi`)**
`PlaylistApi`, "playlist oluştur / güncelle / getir" gibi iş odaklı metotlar sunar. Token alma ve doğru path'i seçme işini bu katman üstlenir; testleri tekrar eden kurulum kodundan arındırır.

**3. Çekirdek Katman (`api`)**
- **`RestResource`** — `given/when/then` kalıbını kapsayan jenerik `get`, `post`, `update`, `postAccount` metotları. Tüm HTTP çağrıları buradan geçer.
- **`SpecBuilder`** — İki ayrı `RequestSpecification` tanımlar: biri API çağrıları (`api.spotify.com`, JSON), diğeri token çağrıları (`accounts.spotify.com`, URL-encoded) içindir. Allure filtresi ve loglama merkezi olarak burada eklenir.
- **`Route`** — `BASE_PATH`, `TOKEN`, `PLAYLISTS` gibi endpoint path sabitleri.
- **`TokenManager`** — `access_token`'ı önbelleğe alır. Token yoksa veya süresi dolmak üzereyse (`expires_in - 300` sn güvenlik payı ile) yeniler. `synchronized` olduğundan paralel koşumda güvenlidir.
- **`StatusCode`** — Beklenen HTTP durum kodlarını ve hata mesajlarını bir arada tutan enum.

**4. POJO Katmanı (`pojo`)**
Spotify'ın JSON yanıtlarını temsil eden model sınıfları. `Playlist` ve `Error` sınıfları Lombok (`@Builder`, `@Getter/@Setter`, `@Jacksonized`) ile sadeleştirilmiştir. `@JsonInclude(NON_NULL)` sayesinde null alanlar isteklerde gönderilmez.

**5. Yardımcı Katman (`utils`)**
`ConfigLoader`/`DataLoader` (singleton `.properties` okuyucuları), `PropertyUtils` (dosya yükleme), `FakerUtils` (rastgele veri) ve `AssertUtils` (Allure `@Step`'li, yeniden kullanılabilir doğrulamalar).

### Akış özeti

```
PlaylistTests
   └─> PlaylistApi  (iş odaklı metot)
         ├─> TokenManager.getToken()   → gerekirse token yenile
         └─> RestResource.get/post/update
                  └─> SpecBuilder (spec) + Route (path)
                           └─> Spotify Web API
   └─> AssertUtils  (durum kodu + gövde doğrulama)
```

---

## ⚙️ Ön Gereksinimler

- **JDK 21**
- **Maven 3.x**
- **Allure Commandline** (raporu görüntülemek için) — [kurulum](https://allurereport.org/docs/install/)
- Bir **Spotify Developer** hesabı ve uygulaması — [developer.spotify.com/dashboard](https://developer.spotify.com/dashboard)

---

## 🔐 Yapılandırma

Testler, kimlik bilgilerini ve test verisini `src/test/resources` altındaki `.properties` dosyalarından okur.

**`config.properties`**
```properties
client_id     = <SPOTIFY_CLIENT_ID>
client_secret = <SPOTIFY_CLIENT_SECRET>
refresh_token = <SPOTIFY_REFRESH_TOKEN>
grant_type    = refresh_token
```

**`data.properties`**
```properties
playlist_id = <GUNCELLEME_TESTI_ICIN_GECERLI_PLAYLIST_ID>
```

**`allure.properties`**
```properties
allure.results.directory = target/allure-results
```

Ayrıca iki temel URL, çalışma anında sistem değişkeni (*system property*) olarak verilir:

| Değişken | Değer |
|---|---|
| `BASE_URI` | `https://api.spotify.com` |
| `ACCOUNT_BASE_URI` | `https://accounts.spotify.com` |

### Kimlik bilgileri nasıl alınır?

1. Spotify Developer Dashboard'da bir uygulama oluştur; `client_id` ve `client_secret` değerlerini al.
2. **Authorization Code** akışıyla, gerekli izinlere (`playlist-modify-public`, `playlist-modify-private` vb.) sahip bir `refresh_token` üret.
3. Bu değerleri `config.properties` dosyasına yaz.

> ### ⚠️ Güvenlik Uyarısı
> `config.properties` **gizli bilgiler** içerir ve **asla sürüm kontrolüne (git) eklenmemelidir.**
> - Dosyayı `.gitignore`'a ekleyin ve depoya örnek bir şablon (`config.properties.example`) koyun.
> - Daha önce gerçek `client_secret` / `refresh_token` değerleri depoya gönderildiyse, bunları **Spotify Dashboard üzerinden derhal yenileyin (rotate edin)**; sızan anahtarlar geçmiş commit'lerde kalıcı olur.

---

## 🚀 Testleri Çalıştırma

Gerekli URL'leri sistem değişkeni olarak geçerek tüm testleri çalıştırın:

```bash
mvn clean test \
  -DBASE_URI=https://api.spotify.com \
  -DACCOUNT_BASE_URI=https://accounts.spotify.com
```

Paralel koşum `pom.xml` içindeki Surefire yapılandırmasında zaten tanımlıdır (metot bazında, 10 thread).

> Alternatif olarak `SpecBuilder` içindeki yorum satırı yapılmış `setBaseUri(...)` satırlarını açarak URL'leri koda gömebilirsiniz; ancak sistem değişkeni yöntemi daha esnektir.

---

## 📊 Allure Raporu

Testler çalıştıktan sonra sonuçlar `target/allure-results` altına yazılır. Raporu görüntülemek için:

```bash
allure serve target/allure-results
```

Adım bazlı raporlama için gereken **AspectJ Weaver** javaagent'ı `pom.xml` içindeki Surefire eklentisinde zaten tanımlıdır; ek bir ayar gerekmez.

---

## 🧪 Test Senaryoları

`PlaylistTests` sınıfı aşağıdaki senaryoları kapsar:

| # | Senaryo | Tip | Beklenen Sonuç |
|---|---|---|---|
| 1 | Geçerli veriyle playlist oluşturma | Pozitif | `201 Created` + gövde eşleşmesi |
| 2 | Var olan bir playlist'i güncelleme | Pozitif | `200 OK` |
| 3 | Oluşturulan playlist'i getirme | Pozitif | `201` → `200` + gövde eşleşmesi |
| 4 | Boş isimle playlist oluşturma | Negatif | `400 Bad Request` + hata mesajı |
| 5 | Geçersiz/süresi dolmuş token ile oluşturma | Negatif | `401 Unauthorized` + hata mesajı |

Her test; durum kodunu `assertStatusCode`, yanıt gövdesini `assertPlaylistEqual` / `assertError` ile doğrular. Test verileri JavaFaker ile dinamik üretilir.

---

## 🛠 Geliştirme Alanları

Çatıyı daha da olgunlaştırmak için bazı öneriler:

- **Gizli bilgileri depodan çıkarmak** — `config.properties`'i `.gitignore`'a eklemek ve örnek şablon sunmak (en öncelikli madde).
- **POJO tutarlılığı** — Tüm model sınıflarını Lombok'a geçirerek elle yazılmış getter/setter'ları azaltmak; `ExternalUrls` ve `ExternalUrls__1` gibi yinelenen sınıfları tek sınıfta birleştirmek.
- **Küçük düzeltmeler** — `ConfigLoader.getGrandType()` → `getGrantType()` yazım düzeltmesi; kullanılmayan `JSONAssert` bağımlılığının gözden geçirilmesi.
- **TestNG suite dosyası** — Gruplama ve seçmeli koşum için bir `testng.xml` eklemek.
- **CI entegrasyonu** — GitHub Actions ile otomatik test koşumu ve Allure raporu yayınlama.
- **Kapsam genişletme** — Playlist dışındaki uç noktalar (ör. kullanıcı profili, arama) için senaryolar eklemek.
