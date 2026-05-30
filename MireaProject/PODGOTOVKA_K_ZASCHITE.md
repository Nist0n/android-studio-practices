# Подготовка к защите проекта MireaProject

> **Для кого:** этот файл помогает понять, *как устроено ваше приложение*, *какие Android-системы используются* и *за что отвечают ключевые строки кода*.  
> **Проект:** `ru.mirea.pavlovve.mireaproject` — учебное Android-приложение с боковым меню и набором экранов-фрагментов.

---

## Содержание

0. [Привязка к практическим занятиям № 3–8 (МИРЭА)](#0-привязка-к-практическим-занятиям--3–8-мирэа)
1. [Что делает приложение в целом](#1-что-делает-приложение-в-целом)
2. [Словарь определений Android Studio](#2-словарь-определений-android-studio)
3. [Структура проекта](#3-структура-проекта)
4. [Точка входа: Manifest и Application](#4-точка-входа-manifest-и-application)
5. [Аутентификация Firebase (LoginActivity)](#5-аутентификация-firebase-loginactivity)
6. [Главный экран и навигация (MainActivity)](#6-главный-экран-и-навигация-mainactivity)
7. [Экран «Data» (DataFragment)](#7-экран-data-datafragment)
8. [Профиль и SharedPreferences](#8-профиль-и-sharedpreferences)
9. [Работа с файлами (FilesFragment)](#9-работа-с-файлами-filesfragment)
10. [WebView и проверка сети](#10-webview-и-проверка-сети)
11. [Фоновые задачи WorkManager](#11-фоновые-задачи-workmanager)
12. [Компас и датчики (SensorManager)](#12-компас-и-датчики-sensormanager)
13. [Камера, разрешения, FileProvider](#13-камера-разрешения-fileprovider)
14. [Микрофон: MediaRecorder и MediaPlayer](#14-микрофон-mediarecorder-и-mediaplayer)
15. [Сеть: Retrofit и REST API](#15-сеть-retrofit-и-rest-api)
16. [Карта заведений: Yandex MapKit + RecyclerView](#16-карта-заведений-yandex-mapkit--recyclerview)
17. [Зависимости (build.gradle.kts)](#17-зависимости-buildgradlekts)
18. [Типичные вопросы на защите](#18-типичные-вопросы-на-защите)
19. [Краткая шпаргалка «экран → технология»](#19-краткая-шпаргалка-экран--технология)

---

## 0. Привязка к практическим занятиям № 3–8 (МИРЭА)

> Источник: PDF «Практика № 3» … «Практика № 8» (РТУ МИРЭА, дисциплина «Разработка мобильных приложений»).  
> Ниже — **что требовалось по каждой практике** и **как это реализовано в вашем MireaProject**.

### Сводная таблица «практика → фрагмент/класс»

| Практика | Тема занятия | Контрольное задание MireaProject | Ваш код |
|----------|--------------|-----------------------------------|---------|
| **№ 3** | Intent, Fragment, Navigation Drawer, WebView | `DataFragment`, `WebViewFragment`, Drawer | `MainActivity`, `DataFragment`, `WebViewFragment`, `drawer_menu.xml` |
| **№ 4** | ViewBinding, потоки, Service, **WorkManager** | Фрагмент фоновой задачи | `BackgroundWorkFragment`, `UploadWorker` |
| **№ 5** | Датчики, разрешения, камера, **MediaRecorder** | Компас, камера, микрофон | `CompassFragment`, `CameraCollageFragment`, `MicrophoneFragment`, `PermissionHelper` |
| **№ 6** | **SharedPreferences**, файлы | Профиль + работа с файлами | `ProfileFragment`, `ProfilePreferences`, `FilesFragment` |
| **№ 7** | Сеть, **Retrofit**, **Firebase Auth** | Login + сетевой фрагмент | `LoginActivity`, `NetworkFragment`, `RetrofitClient` |
| **№ 8** | Картографические сервисы, **Yandex MapKit** | Фрагмент «Заведения» | `PlacesFragment`, `MireaApplication`, `PlacesAdapter` |

---

### Практика № 3 — Intent, Fragment, Navigation Drawer, WebView

#### Определения из лекции

| Термин | Определение (по PDF) |
|--------|----------------------|
| **Intent** | Объект намерения: запуск Activity, передача данных, вызов системных приложений |
| **Intent Filter** | `<intent-filter>` в Manifest: action + category + data — система выбирает компонент |
| **ACTION_MAIN + CATEGORY_LAUNCHER** | Точка входа приложения (иконка на рабочем столе) |
| **putExtra / getStringExtra** | Передача данных между Activity через Intent |
| **Activity Result API** | `registerForActivityResult` — современная замена `startActivityForResult` |
| **Fragment** | Часть UI внутри Activity; `onCreateView` + `inflate` |
| **FragmentTransaction** | `beginTransaction().replace(...).commit()` — смена фрагмента |
| **DrawerLayout + NavigationView** | Боковое меню («гамбургер») |
| **WebView** | Встроенный браузер на движке WebKit/Chromium |

#### Контрольное задание (практика 3)

1. Проект **MireaProject** на базе Navigation Drawer Activity  
2. **DataFragment** — уникальная информация об отрасли, Material You  
3. **WebViewFragment** — простейший браузер со страницей по умолчанию  

#### Реализация в проекте

**LAUNCHER и Intent (практика 3, §1.1):**
```xml
<!-- LoginActivity — точка входа -->
<action android:name="android.intent.action.MAIN" />
<category android:name="android.intent.category.LAUNCHER" />
```

**Переход Activity → Activity (явный Intent):**
```java
startActivity(new Intent(this, MainActivity.class));
finish();
```
Файлы: `LoginActivity.java` (стр. 94–96, 109–110), `MainActivity.java` (стр. 31–32, 109–110).

**Navigation Drawer (практика 3, контрольное):**  
В лекции — `NavController` + `mobile_navigation.xml`. В вашем проекте — **упрощённый вариант** через `FragmentTransaction` (тоже корректно для защиты):

```java
// MainActivity.java — замена фрагмента по пункту меню
getSupportFragmentManager().beginTransaction()
    .replace(R.id.fragment_container, selectedFragment)
    .commit();
```

**DataFragment** — шаблон Fragment с `newInstance` и Bundle (практика 3, §2):
```java
return inflater.inflate(R.layout.fragment_data, container, false);
```

**WebViewFragment** — WebView + WebSettings + WebViewClient (практика 3, контрольное):
```java
webSettings.setJavaScriptEnabled(true);
webView.setWebViewClient(new WebViewClient() { ... });
webView.loadUrl("https://developer.android.com");
```

**На защите сказать:** «Практика 3 — основа приложения: DrawerLayout, фрагменты, WebView. Навигация у нас через FragmentManager.replace, а не Navigation Component, но принцип тот же: один контейнер, много экранов.»

---

### Практика № 4 — ViewBinding, многопоточность, Service, WorkManager

#### Определения из лекции

| Термин | Определение |
|--------|-------------|
| **ViewBinding** | Генерируемый класс `ActivityMainBinding` — type-safe доступ к View без `findViewById` |
| **UI-поток (Main Thread)** | Единственный поток, где можно обновлять интерфейс |
| **Looper / Handler** | Очередь сообщений для UI-потока |
| **Service** | Фоновый компонент без UI (`onStartCommand`, `MediaPlayer` в сервисе) |
| **WorkManager** | Рекомендованный способ фоновых задач (Doze, constraints, переживает kill процесса) |
| **Worker** | Класс с методом `doWork()` — выполняется не на UI-потоке |
| **OneTimeWorkRequest** | Разовая задача |
| **Constraints** | Условия запуска (сеть, заряд) |
| **WorkInfo.State** | `ENQUEUED`, `RUNNING`, `SUCCEEDED`, `FAILED` |

#### Контрольное задание (практика 4)

> Создать фрагмент фоновой задачи в MireaProject через **Worker** или Service.

#### Реализация — WorkManager (как в PDF, §5)

**UploadWorker** — почти копия примера из практики 4:
```java
@Override
public Result doWork() {
    TimeUnit.SECONDS.sleep(10);  // имитация загрузки
    return Result.success();
}
```

**BackgroundWorkFragment:**
```java
Constraints constraints = new Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build();

OneTimeWorkRequest uploadWorkRequest =
    new OneTimeWorkRequest.Builder(UploadWorker.class)
        .setConstraints(constraints)
        .addTag(WORK_TAG)
        .build();

WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest);
```

**Наблюдение статуса (LiveData — улучшение относительно PDF):**
```java
WorkManager.getInstance(requireContext())
    .getWorkInfosByTagLiveData(WORK_TAG)
    .observe(getViewLifecycleOwner(), workInfos -> { ... });
```

**Зависимость (как в лекции):** `androidx.work:work-runtime:2.8.1` в `build.gradle.kts`.

**На защите:** «WorkManager выбран вместо raw Thread/Service, потому что с Android 6+ Doze и с Android 8+ ограничения на фоновые сервисы. Worker выполняется вне UI-потока, Constraints требуют интернет.»

**Примечание:** в проекте используется `findViewById`, а не ViewBinding (практика 4, §1) — на защите можно сказать: «ViewBinding изучали на практике 4; здесь классический findViewById, оба подхода валидны.»

---

### Практика № 5 — Датчики, разрешения, камера, микрофон

#### Определения из лекции

| Термин | Определение |
|--------|-------------|
| **SensorManager** | Системный сервис доступа к датчикам |
| **SensorEventListener** | `onSensorChanged`, `onAccuracyChanged` |
| **registerListener / unregisterListener** | Регистрация в `onResume`, снятие в `onPause` |
| **SENSOR_DELAY_UI** | Частота обновления, подходящая для UI |
| **Normal vs Dangerous permission** | Обычные — при установке; опасные — runtime с Android 6 |
| **Activity Result API** | `RequestPermission`, `TakePicture` |
| **FileProvider** | Безопасная передача `content://` URI камере |
| **MediaRecorder** | Запись аудио в файл |

#### Контрольное задание (практика 5)

1. Механизмы запроса разрешений  
2. Экран с **датчиком** (логическая задача — компас)  
3. Экран с **камерой** (творческая задача — коллаж)  
4. Экран с **микрофоном**  

#### CompassFragment — датчики (практика 5, §1–2)

Лекция: акселерометр показывает 3 оси. **В проекте — компас:** акселерометр + магнитометр → азимут:

```java
sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
SensorManager.getOrientation(rotationMatrix, orientation);
float azimuthDeg = (float) Math.toDegrees(orientation[0]);
compassArrow.setRotation(-azimuthDeg);
```

**Manifest (uses-feature):**
```xml
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="false" />
<uses-feature android:name="android.hardware.sensor.compass" android:required="false" />
```

#### CameraCollageFragment — камера (практика 5, §4)

По лекции: не писать свою Camera Activity — вызвать системную камеру через Intent + FileProvider.

```java
photoUris[activeSlot] = FileProvider.getUriForFile(
    requireContext(),
    requireContext().getPackageName() + ".fileprovider",
    photoFile);
takePictureLauncher.launch(photoUris[activeSlot]);
```

**file_paths.xml** (лекция: `<cache-path/>`):
```xml
<cache-path name="cache_photos" path="." />
```

#### MicrophoneFragment — MediaRecorder (практика 5, §5)

```java
mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
mediaRecorder.start();
// уровень громкости:
int amplitude = mediaRecorder.getMaxAmplitude();
```

#### PermissionHelper — runtime permissions (практика 5, §3)

```java
ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
```

**На защите:** «Практика 5 полностью закрыта тремя фрагментами: CompassFragment (SensorManager), CameraCollageFragment (FileProvider + TakePicture), MicrophoneFragment (MediaRecorder/MediaPlayer). Разрешения CAMERA и RECORD_AUDIO объявлены в Manifest и запрашиваются в runtime.»

---

### Практика № 6 — SharedPreferences и файлы

#### Определения из лекции

| Термин | Определение |
|--------|-------------|
| **SharedPreferences** | Key-value хранилище; файл в `/data/data/.../shared_prefs/` |
| **getSharedPreferences(name, MODE_PRIVATE)** | Доступ только этому приложению |
| **edit().put*().apply()** | Асинхронная запись |
| **Internal storage** | `getFilesDir()` — приватная папка приложения |
| **FileOutputStream / BufferedReader** | Запись и чтение текстовых файлов |

#### Контрольное задание (практика 6)

1. **Профиль** — параметры пользователя в SharedPreferences  
2. **Файлы** — FAB → диалог создания записи + обработка файлов  

#### ProfileFragment + ProfilePreferences

```java
prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
prefs.edit()
    .putString(KEY_NAME, name)
    .putInt(KEY_AGE, age)
    ...
    .apply();
```

Поля профиля (ваша задумка): имя, возраст, любимая еда, деньги.

#### FilesFragment

- Папка: `getFilesDir()/notes/` — **internal storage** (практика 6, §3.1)  
- FAB → `MaterialAlertDialogBuilder` → создание `.txt`  
- Обработка: **перевод в верхний регистр** (`toUpperCase`) — «простая обработка файла» из контрольного  

```java
notesDir = new File(requireContext().getFilesDir(), "notes");
try (FileOutputStream fos = new FileOutputStream(file)) {
    fos.write(body.getBytes(StandardCharsets.UTF_8));
}
```

**На защите:** «SharedPreferences для профиля — мало полей, key-value. Файлы — internal storage, недоступны другим приложениям. ListView показывает список заметок.»

---

### Практика № 7 — Сеть, Retrofit, Firebase

#### Определения из лекции

| Термин | Определение |
|--------|-------------|
| **Socket** | Низкоуровневое TCP-соединение (мессенджеры, игры) |
| **HttpURLConnection** | HTTP-клиент из Android SDK |
| **JSON** | Формат обмена `{ключ: значение}` |
| **REST** | Стиль API: ресурсы + HTTP-методы (GET, POST…) |
| **Retrofit** | Библиотека: интерфейс + аннотации `@GET`, `@Path` |
| **Gson** | JSON → Java-объект (POJO) |
| **enqueue / Callback** | Асинхронный запрос, не блокирует UI |
| **Firebase Auth** | Облачная аутентификация (Email/Password) |
| **FirebaseUser** | Текущий пользователь, `getUid()`, `isEmailVerified()` |

#### Контрольное задание (практика 7)

1. **LoginActivity** — Firebase, LAUNCHER в Manifest  
2. После входа → главный экран  
3. Фрагмент с данными из сети (**Retrofit**)  

#### LoginActivity (Firebase — практика 7, §2.3–2.4)

```java
mAuth = FirebaseAuth.getInstance();
mAuth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(this, task -> { ... });
mAuth.createUserWithEmailAndPassword(email, password) ...
updateUI(FirebaseUser user);  // как в PDF
```

**Manifest:** `LoginActivity` — LAUNCHER; `MainActivity` проверяет `getCurrentUser()`.

#### NetworkFragment (Retrofit — практика 7, §2.1)

```java
@GET("posts/{id}")
Call<Post> getPost(@Path("id") int id);
```

```java
RetrofitClient.getApi().getPost(1).enqueue(new Callback<Post>() {
    public void onResponse(...) { Post post = response.body(); }
    public void onFailure(...) { ... }
});
```

API: `https://jsonplaceholder.typicode.com/` — учебный REST-сервис.

**WebViewFragment** — проверка сети через `ConnectivityManager` (практика 7, §1).

**На защите:** «Практика 7: Firebase защищает вход, Retrofit загружает JSON с jsonplaceholder. enqueue — асинхронно, UI не блокируется.»

---

### Практика № 8 — Yandex MapKit, фрагмент «Заведения»

#### Определения из лекции

| Термин | Определение |
|--------|-------------|
| **MapKit** | SDK Яндекс.Карт для Android |
| **MapView** | View с картой в layout |
| **MapKitFactory** | Инициализация SDK с API-ключом |
| **PlacemarkMapObject** | Метка на карте |
| **CameraPosition** | Положение камеры (точка, zoom, azimuth, tilt) |
| **BoundingBox** | Область, охватывающая набор точек |
| **onStart/onStop MapView** | Обязательный lifecycle карты |

#### Контрольное задание (практика 8)

> Фрагмент **«Заведения»**: список заведений, описание, отображение на карте.  
> По нажатию на маркер — адрес и описание. **+1 функция** работы с картой.

#### Реализация

**MireaApplication** (лекция: Application + setApiKey):
```java
MapKitFactory.setApiKey(key);
MapKitFactory.initialize(this);
```

**Ключ:** `local.properties` → `BuildConfig.MAPKIT_API_KEY` (не в git).

**PlacesFragment:**
- 4 заведения в Москве (`buildSamplePlaces`)  
- `PlacemarkMapObject` + `setUserData(place)` + `MapObjectTapListener`  
- **Доп. функция (контрольное):** `fitAllPlacesOnMap()` — камера на все метки через `BoundingBox`  
- **RecyclerView** (`PlacesAdapter`) — список синхронизирован с картой  
- Lifecycle: `mapView.onStart()/onStop()`, `MapKitFactory.getInstance().onStart()/onStop()`

```java
private void selectPlace(Place place, boolean moveCamera) {
    detailTitle.setText(place.getTitle());
    detailAddress.setText(place.getAddress());
    detailDescription.setText(place.getDescription());
    adapter.setSelectedId(place.getId());
    if (moveCamera) {
        map.move(new CameraPosition(place.getPoint(), 16.0f, 0.0f, 0.0f), ...);
    }
}
```

**На защите:** «Практика 8: Yandex MapKit lite. MapKit инициализируется в Application. Маркеры с tap-listener, RecyclerView для списка. Доп. функция — FAB «показать все заведения» через bounding box.»

---

### Чек-лист «все контрольные задания закрыты»

| Контрольное (из PDF) | Статус в MireaProject |
|----------------------|------------------------|
| П3: DataFragment + WebViewFragment + Drawer | ✅ |
| П4: фоновая задача Worker | ✅ `BackgroundWorkFragment` |
| П5: разрешения + датчик + камера + микрофон | ✅ 3 фрагмента + `PermissionHelper` |
| П6: профиль (SharedPreferences) + файлы (FAB) | ✅ |
| П7: Firebase Login + Retrofit-фрагмент | ✅ |
| П8: «Заведения» на карте + extra map function | ✅ `fitAllPlacesOnMap` |

---

## 1. Что делает приложение в целом

**MireaProject** — одно приложение с **единой точкой входа через авторизацию** и **главным экраном с боковым меню (Navigation Drawer)**. После входа пользователь переключается между экранами-фрагментами:

| Пункт меню | Фрагмент | Практика | Технология |
|------------|----------|----------|------------|
| Data Screen | `DataFragment` | **№ 3** | Fragment, LayoutInflater |
| Профиль | `ProfileFragment` | **№ 6** | SharedPreferences |
| Работа с файлами | `FilesFragment` | **№ 6** | File I/O, ListView, Dialog |
| Web Screen | `WebViewFragment` | **№ 3** | WebView, ConnectivityManager |
| Сеть (Retrofit) | `NetworkFragment` | **№ 7** | Retrofit, REST, Callback |
| Заведения | `PlacesFragment` | **№ 8** | Yandex MapKit, RecyclerView |
| Фоновая задача | `BackgroundWorkFragment` | **№ 4** | WorkManager |
| Компас | `CompassFragment` | **№ 5** | SensorManager, SensorEventListener |
| Камера | `CameraCollageFragment` | **№ 5** | Camera, Runtime Permissions, FileProvider |
| Голосовая заметка | `MicrophoneFragment` | **№ 5** | MediaRecorder, MediaPlayer |
| *(до Main)* | `LoginActivity` | **№ 7** | Firebase Auth, Intent |

**Сценарий запуска:**

```
LoginActivity (LAUNCHER)
    ↓ успешный вход / уже авторизован
MainActivity
    ↓ пункт бокового меню
Fragment (один из 10 экранов)
```

**Защита на словах (30 секунд):**  
«Приложение демонстрирует основные механизмы Android: Activity и Fragment, хранение данных, работу с файлами, сеть, карты, датчики, камеру, микрофон и фоновые задачи. Авторизация через Firebase Email/Password защищает доступ к основному функционалу. Навигация реализована через DrawerLayout и FragmentTransaction.»

---

## 2. Словарь определений Android Studio

### Базовые понятия платформы

| Термин | Определение |
|--------|-------------|
| **Android SDK** | Набор инструментов и API для разработки под Android (компилятор, эмулятор, библиотеки). |
| **Android Studio** | IDE от Google для создания Android-приложений (редактор кода, Layout Editor, Gradle, эмулятор). |
| **APK / AAB** | Файл установки приложения (APK — для прямой установки, AAB — для Google Play). |
| **Package name** | Уникальный идентификатор приложения. В проекте: `ru.mirea.pavlovve.mireaproject`. |
| **minSdk / targetSdk / compileSdk** | Минимальная версия Android, на которую рассчитано приложение (26), целевая (36) и версия SDK для компиляции (36). |

### Компоненты приложения

| Термин | Определение |
|--------|-------------|
| **Activity** | Экран с собственным жизненным циклом (`onCreate`, `onStart`, `onResume`…). Точка взаимодействия с пользователем. Примеры: `LoginActivity`, `MainActivity`. |
| **Fragment** | Переиспользуемая часть UI внутри Activity. Имеет свой layout и lifecycle. Примеры: `ProfileFragment`, `CompassFragment`. |
| **Application** | Класс, создаваемый один раз при старте процесса приложения. У вас: `MireaApplication` — инициализация Yandex MapKit. |
| **Intent** | Объект для запуска Activity/Service или передачи данных между компонентами. |
| **AndroidManifest.xml** | «Паспорт» приложения: Activity, разрешения, Application, FileProvider. |

### UI и ресурсы

| Термин | Определение |
|--------|-------------|
| **Layout (XML)** | Разметка экрана: `activity_main.xml`, `fragment_profile.xml` и т.д. |
| **View / Widget** | Элемент интерфейса: `TextView`, `Button`, `RecyclerView`, `WebView`. |
| **findViewById** | Связь Java-кода с элементом layout по `@+id/...`. |
| **R-класс** | Автогенерируемый класс со ссылками на ресурсы (`R.layout.*`, `R.string.*`, `R.id.*`). |
| **strings.xml** | Текстовые строки приложения (локализация, подписи кнопок). |
| **Material Design** | Библиотека Google для современного UI (`MaterialAlertDialogBuilder`, `TextInputEditText`, `FloatingActionButton`). |

### Жизненный цикл

| Термин | Определение |
|--------|-------------|
| **onCreate** | Создание Activity/Fragment: `setContentView`, привязка View, listeners. |
| **onViewCreated** | Fragment: View уже создан — здесь `findViewById` и логика UI. |
| **onStart / onStop** | Видимость на экране. У карты MapKit вызываются `mapView.onStart()` / `onStop()`. |
| **onResume / onPause** | Активное взаимодействие. У компаса — регистрация/снятие слушателя датчиков. |
| **Bundle savedInstanceState** | Сохранение состояния при повороте экрана (в проекте почти не используется). |

### Хранение данных

| Термин | Определение |
|--------|-------------|
| **SharedPreferences** | Key-value хранилище для простых настроек (профиль пользователя). |
| **Internal storage** | Приватная папка приложения (`getFilesDir()`, `getCacheDir()`). |
| **File I/O** | Чтение/запись через `File`, `FileInputStream`, `FileOutputStream`, `BufferedReader`. |

### Сеть и интернет

| Термин | Определение |
|--------|-------------|
| **HTTP / REST** | Протокол обмена данными. REST — стиль API с ресурсами (`GET /posts/1`). |
| **Retrofit** | Библиотека для HTTP-запросов с декларативным интерфейсом API. |
| **Gson** | Преобразование JSON ↔ Java-объекты. |
| **Callback** | Асинхронный ответ на сетевой запрос (`onResponse`, `onFailure`). |
| **ConnectivityManager** | Проверка наличия интернет-соединения. |

### Безопасность и разрешения

| Термин | Определение |
|--------|-------------|
| **Permission** | Разрешение на камеру, микрофон, интернет. Объявляется в Manifest + запрашивается в runtime. |
| **Runtime permission** | Запрос опасного разрешения у пользователя во время работы приложения (Android 6+). |
| **Activity Result API** | Современный способ получить результат (фото с камеры, запрос permission): `registerForActivityResult`. |
| **FileProvider** | Безопасная передача `Uri` файла другому приложению (камера). |

### Прочие системы

| Термин | Определение |
|--------|-------------|
| **Firebase Auth** | Облачная аутентификация (email/password). |
| **WorkManager** | Планирование фоновой работы с учётом ограничений (сеть, заряд). |
| **SensorManager** | Доступ к аппаратным датчикам (акселерометр, магнитометр). |
| **MediaRecorder / MediaPlayer** | Запись и воспроизведение аудио. |
| **RecyclerView** | Эффективный список с паттерном ViewHolder + Adapter. |
| **MapKit (Yandex)** | SDK карт: метки, камера, жесты. |

### Gradle

| Термин | Определение |
|--------|-------------|
| **build.gradle.kts** | Конфигурация модуля: SDK, зависимости, BuildConfig. |
| **dependencies** | Внешние библиотеки (Firebase, Retrofit, WorkManager, MapKit). |
| **local.properties** | Локальные настройки (путь к SDK, `MAPKIT_API_KEY`). Не коммитится в git. |
| **BuildConfig** | Сгенерированный класс с константами сборки (API-ключ карты). |

---

## 3. Структура проекта

```
app/src/main/
├── AndroidManifest.xml          # разрешения, Activity, Application, FileProvider
├── java/ru/mirea/pavlovve/mireaproject/
│   ├── MireaApplication.java      # инициализация MapKit
│   ├── LoginActivity.java         # Firebase вход
│   ├── MainActivity.java          # Drawer + Fragment-навигация
│   ├── DataFragment.java
│   ├── ProfileFragment.java
│   ├── ProfilePreferences.java    # SharedPreferences
│   ├── FilesFragment.java
│   ├── WebViewFragment.java
│   ├── BackgroundWorkFragment.java
│   ├── UploadWorker.java          # Worker для WorkManager
│   ├── CompassFragment.java
│   ├── CameraCollageFragment.java
│   ├── MicrophoneFragment.java
│   ├── NetworkFragment.java
│   ├── PermissionHelper.java
│   ├── PlacesFragment.java
│   ├── network/
│   │   ├── RetrofitClient.java
│   │   ├── JsonPlaceholderApi.java
│   │   └── Post.java
│   └── places/
│       ├── Place.java
│       └── PlacesAdapter.java
└── res/
    ├── layout/                    # XML-разметки экранов
    ├── menu/                      # drawer_menu, main_menu
    ├── values/strings.xml
    └── xml/file_paths.xml         # пути для FileProvider
```

---

## 4. Точка входа: Manifest и Application

### AndroidManifest.xml — главные элементы

**Разрешения:**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```
- `INTERNET` — Retrofit, WebView, WorkManager, Firebase.
- `ACCESS_NETWORK_STATE` — проверка сети в WebView.
- `CAMERA` / `RECORD_AUDIO` — runtime-разрешения для камеры и микрофона.

**Application:**
```xml
<application android:name=".MireaApplication" ...>
```
Регистрирует кастомный класс Application — выполняется до любой Activity.

**LAUNCHER Activity:**
```xml
<activity android:name=".LoginActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```
`MAIN` + `LAUNCHER` = иконка приложения запускает `LoginActivity`.

**FileProvider:**
```xml
<provider android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider" ...>
```
Нужен, чтобы передать URI временного файла приложению «Камера» без нарушения политики безопасности (file:// запрещён).

### MireaApplication.java

```java
MapKitFactory.setApiKey(key);
MapKitFactory.initialize(this);
```

| Строка | За что отвечает |
|--------|-----------------|
| `BuildConfig.MAPKIT_API_KEY` | Ключ из `local.properties`, попадает в BuildConfig при сборке |
| `setApiKey(key)` | Авторизация в Yandex MapKit |
| `initialize(this)` | Инициализация SDK карт до использования `MapView` |
| `if (key.isEmpty()) return` | Без ключа карта не инициализируется — показывается placeholder |

**На защите:** «MapKit требует инициализации в Application до создания MapView. Ключ хранится в local.properties и не попадает в репозиторий.»

---

## 5. Аутентификация Firebase (LoginActivity)

**Тема лекции:** Firebase Authentication, Activity lifecycle, валидация формы.

### Ключевой поток

1. `onCreate` — разметка, `FirebaseAuth.getInstance()`, кнопки.
2. `onStart` — если пользователь уже вошёл → сразу `MainActivity`.
3. Регистрация / вход → `createUserWithEmailAndPassword` / `signInWithEmailAndPassword`.
4. Успех → `openMainScreen()` → Intent на `MainActivity`.

### Главные строки кода

```java
mAuth = FirebaseAuth.getInstance();
```
Singleton Firebase Auth для всего приложения.

```java
mAuth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(this, task -> { ... });
```
Асинхронная регистрация. Результат приходит в callback на главном потоке.

```java
if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
    emailEditText.setError(...);
    return false;
}
```
Клиентская валидация email и пароля (минимум 6 символов).

```java
user.sendEmailVerification()
```
Отправка письма для подтверждения email (доп. функция Firebase).

```java
updateUI(FirebaseUser user)
```
Переключение видимости блоков «вход» / «вы уже вошли» через `View.GONE` / `View.VISIBLE`.

```java
isPlaceholderFirebaseConfig()
```
Проверка, что `google-services.json` не содержит заглушку — понятное сообщение об ошибке API key.

### Связь с MainActivity

```java
// MainActivity.onCreate
if (mAuth.getCurrentUser() == null) {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
    return;
}
```
Двойная защита: без авторизации главный экран недоступен.

```java
// MainActivity — выход
mAuth.signOut();
startActivity(new Intent(this, LoginActivity.class));
finish();
```

**На защите:** «Firebase Auth хранит сессию локально. Мы используем Email/Password provider. Проверка `getCurrentUser()` выполняется при каждом входе в MainActivity.»

---

## 6. Главный экран и навигация (MainActivity)

**Темы лекции:** Activity, Fragment, Navigation Drawer, Toolbar, Options Menu, FragmentTransaction.

### Архитектура layout (`activity_main.xml`)

```
DrawerLayout
├── LinearLayout (основной контент)
│   ├── Toolbar
│   └── FrameLayout @id/fragment_container  ← сюда подставляются Fragment
└── NavigationView @id/nav_view  ← боковое меню
```

### Ключевые строки

```java
setContentView(R.layout.activity_main);
```
Привязка layout к Activity.

```java
getSupportFragmentManager().beginTransaction()
    .replace(R.id.fragment_container, new DataFragment())
    .commit();
```
**FragmentTransaction** — замена содержимого контейнера на фрагмент. При первом запуске — `DataFragment`.

```java
navView.setNavigationItemSelectedListener(item -> {
    Fragment selectedFragment = null;
    if (item.getItemId() == R.id.nav_profile) {
        selectedFragment = new ProfileFragment();
    } else if ...
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.fragment_container, selectedFragment)
        .commit();
    return true;
});
```
Навигация: ID пункта меню → создание нужного Fragment → `replace`.

```java
ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
    this, drawerLayout, toolbar, R.string.open, R.string.close);
drawerLayout.addDrawerListener(toggle);
toggle.syncState();
```
Связка **Toolbar** (☰ «гамбургер») с **DrawerLayout** — стандартный паттерн Material Navigation.

```java
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
}
```
Меню в Toolbar (пункт «Sign Out»).

### Fragment — общий шаблон (на примере ProfileFragment)

```java
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_profile, container, false);
}
```
Создание View фрагмента из XML.

```java
public void onViewCreated(View view, Bundle savedInstanceState) {
    // findViewById, listeners, загрузка данных
}
```
Логика после создания View — **рекомендуемое место** для инициализации UI.

**На защите:** «Одна Activity, много Fragment — классический паттерн master-detail / single-activity. Переключение через replace без back stack: при выборе пункта меню предыдущий фрагмент уничтожается.»

---

## 7. Экран «Data» (DataFragment)

**Тема:** базовый Fragment, factory method, Bundle arguments.

Это **учебный/заготовочный** фрагмент:
- `newInstance(param1, param2)` — фабрика с аргументами в Bundle.
- `onCreateView` — только inflate `fragment_data.xml`.

В `MainActivity` он открывается **по умолчанию** при входе.

**На защите:** «DataFragment — стартовый экран-заглушка, демонстрирует стандартный шаблон Fragment с передачей параметров через Bundle.»

---

## 8. Профиль и SharedPreferences

**Тема лекции:** локальное хранение данных, key-value, Context.MODE_PRIVATE.

### Разделение ответственности

| Класс | Роль |
|-------|------|
| `ProfileFragment` | UI: поля ввода, кнопка «Сохранить», отображение summary |
| `ProfilePreferences` | Работа с SharedPreferences (CRUD) |

### ProfilePreferences.java — ключевые строки

```java
prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
```
Файл настроек `user_profile.xml` в internal storage, доступен только этому приложению.

```java
prefs.edit()
    .putString(KEY_NAME, name)
    .putInt(KEY_AGE, age)
    ...
    .apply();
```
`apply()` — асинхронная запись (не блокирует UI). Альтернатива — `commit()` (синхронно, возвращает boolean).

```java
public boolean hasProfile() {
    return !getName().isEmpty();
}
```
Проверка, сохранялся ли профиль ранее.

### ProfileFragment.java — ключевые строки

```java
profilePreferences = new ProfilePreferences(requireContext());
```
`requireContext()` — Context фрагмента (безопаснее, чем `getContext()` — бросит исключение, если Fragment не привязан).

```java
loadProfile();  // при открытии экрана
saveProfile();  // по кнопке
```

```java
age = Integer.parseInt(ageText);
money = Long.parseLong(moneyText);
```
Парсинг чисел с обработкой `NumberFormatException`.

**На защите:** «SharedPreferences подходит для небольших настроек. Для профиля из 4 полей это оптимально. Данные переживают перезапуск приложения, но удаляются при uninstall.»

---

## 9. Работа с файлами (FilesFragment)

**Тема лекции:** File API, internal storage, ListView, диалоги, кодировка UTF-8.

### Логика экрана

1. Папка `getFilesDir()/notes/` — приватное хранилище заметок.
2. FAB (+) → диалог → создание `.txt` файла.
3. ListView → выбор файла → preview в TextView.
4. Кнопка «В верхний регистр» → чтение → `toUpperCase()` → перезапись.

### Ключевые строки

```java
notesDir = new File(requireContext().getFilesDir(), "notes");
if (!notesDir.exists()) {
    notesDir.mkdirs();
}
```
Создание подпапки во **внутренней памяти** приложения.

```java
String safeName = title.replaceAll("[^a-zA-Zа-яА-Я0-9_\\-]", "_");
File file = new File(notesDir, safeName + ".txt");
```
Санитизация имени файла — защита от недопустимых символов.

```java
try (FileOutputStream fos = new FileOutputStream(file)) {
    fos.write(body.getBytes(StandardCharsets.UTF_8));
}
```
Try-with-resources — поток закрывается автоматически. UTF-8 для кириллицы.

```java
adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, fileNames);
filesList.setAdapter(adapter);
adapter.notifyDataSetChanged();
```
**ListView + ArrayAdapter** — простой список имён файлов.

```java
private static String readFile(File file) throws IOException {
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
        ...
    }
}
```
Построчное чтение текста.

**На защите:** «Файлы хранятся во internal storage — другие приложения не имеют доступа без root. ListView показывает список, обработка текста демонстрирует read-modify-write цикл.»

---

## 10. WebView и проверка сети

**Тема лекции:** WebView, WebSettings, WebViewClient, ConnectivityManager.

### Ключевые строки WebViewFragment.java

```java
if (!isNetworkAvailable()) {
    Toast.makeText(..., "Нет подключения к интернету...", ...).show();
    webView.loadUrl("about:blank");
    return view;
}
```
Проверка сети **до** загрузки страницы.

```java
webSettings.setJavaScriptEnabled(true);
webSettings.setDomStorageEnabled(true);
```
Включение JS и localStorage для современных сайтов.

```java
webView.setWebViewClient(new WebViewClient() {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
});
```
Ссылки открываются **внутри** WebView, а не во внешнем браузере.

```java
webView.loadUrl("https://developer.android.com");
```
Загружаемый URL.

```java
ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
NetworkInfo info = cm.getActiveNetworkInfo();
return info != null && info.isConnected();
```
Классическая проверка активного сетевого подключения.

**На защите:** «WebView встраивает веб-контент в нативное приложение. WebViewClient перехватывает навигацию и ошибки. Перед загрузкой проверяем ConnectivityManager.»

---

## 11. Фоновые задачи WorkManager

**Тема лекции:** фоновая работа, ограничения (Constraints), Worker, LiveData статуса.

### Компоненты

| Класс | Роль |
|-------|------|
| `BackgroundWorkFragment` | UI: кнопка запуска, отображение статуса |
| `UploadWorker` | Выполнение задачи в фоне |

### BackgroundWorkFragment — ключевые строки

```java
Constraints constraints = new Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build();
```
Задача запустится **только при наличии сети**.

```java
OneTimeWorkRequest uploadWorkRequest =
    new OneTimeWorkRequest.Builder(UploadWorker.class)
        .setConstraints(constraints)
        .addTag(WORK_TAG)
        .build();

WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest);
```
Постановка **одноразовой** задачи в очередь WorkManager.

```java
WorkManager.getInstance(requireContext())
    .getWorkInfosByTagLiveData(WORK_TAG)
    .observe(getViewLifecycleOwner(), workInfos -> { ... });
```
**LiveData** — UI автоматически обновляется при смене статуса (`ENQUEUED`, `RUNNING`, `SUCCEEDED`…).

### UploadWorker — ключевые строки

```java
@Override
public Result doWork() {
    TimeUnit.SECONDS.sleep(10);  // имитация загрузки
    return Result.success();
}
```
`doWork()` выполняется **не на UI-потоке**. Возврат `success()` / `failure()`.

**На защите:** «WorkManager надёжнее, чем raw Thread или AsyncTask: переживает перезапуск процесса, учитывает Doze mode. Constraints гарантируют запуск только при интернете. UploadWorker имитирует 10-секундную загрузку.»

---

## 12. Компас и датчики (SensorManager)

**Тема лекции:** Sensor API, SensorEventListener, SensorManager, fusion данных датчиков.

### Используемые датчики

- `Sensor.TYPE_ACCELEROMETER` — ускорение (гравитация).
- `Sensor.TYPE_MAGNETIC_FIELD` — магнитное поле (компас).

### CompassFragment — ключевые строки

```java
public class CompassFragment extends Fragment implements SensorEventListener
```
Fragment сам является слушателем датчиков.

```java
sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
```
Получение системного сервиса и датчиков.

```java
// onResume
sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

// onPause
sensorManager.unregisterListener(this);
```
**Обязательно** снимать listener в `onPause` — экономия батареи.

```java
// onSensorChanged
System.arraycopy(event.values, 0, gravity, 0, event.values.length);
// ...
SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
SensorManager.getOrientation(rotationMatrix, orientation);
float azimuthDeg = (float) Math.toDegrees(orientation[0]);
```
Стандартный алгоритм Android: из акселерометра + магнитометра → матрица поворота → **азимут** (угол к северу).

```java
compassArrow.setRotation(-azimuthDeg);
```
Поворот стрелки на UI (ImageView).

```java
int index = Math.round(azimuth / 45f) % 8;
return labels[index];  // С, СВ, В, ЮВ, Ю, ЮЗ, З, СЗ
```
Определение стороны света по 8 секторам.

**На защите:** «Компас вычисляется не одним датчиком, а комбинацией accelerometer + magnetometer через getRotationMatrix. Listener регистрируется в onResume и снимается в onPause.»

---

## 13. Камера, разрешения, FileProvider

**Тема лекции:** runtime permissions, Activity Result API, Intent камеры, FileProvider.

### PermissionHelper.java

```java
ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
```
Проверка, выдано ли разрешение.

```java
fragment.shouldShowRequestPermissionRationale(permission)
```
Нужно ли показать объяснение, почему разрешение важно (после первого отказа).

### CameraCollageFragment — поток работы

```
Нажатие кнопки → requestPhoto(slot)
    → если CAMERA granted → launchCamera()
    → иначе cameraPermissionLauncher.launch(CAMERA)
        → granted → launchCamera()
        → denied → hint + Toast rationale
```

### Ключевые строки

```java
private final ActivityResultLauncher<String> cameraPermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> { ... });
```
Современный API запроса разрешения (вместо устаревшего `requestPermissions`).

```java
private final ActivityResultLauncher<Uri> takePictureLauncher =
    registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> { ... });
```
Контракт «сделать снимок в указанный Uri».

```java
File photoFile = File.createTempFile("collage_" + activeSlot + "_", ".jpg", requireContext().getCacheDir());
photoUris[activeSlot] = FileProvider.getUriForFile(
    requireContext(),
    requireContext().getPackageName() + ".fileprovider",
    photoFile);
takePictureLauncher.launch(photoUris[activeSlot]);
```
1. Временный файл в cache.  
2. Content Uri через FileProvider.  
3. Запуск камеры — результат пишется в этот Uri.

```java
target.setImageURI(photoUris[activeSlot]);
```
Отображение снимка в ImageView (левый/правый слот коллажа).

### file_paths.xml

```xml
<cache-path name="cache_photos" path="." />
```
FileProvider может отдавать файлы из cache-директории.

**На защите:** «С Android 7+ нельзя передавать file:// в Intent. FileProvider создаёт content:// URI. Разрешение CAMERA запрашивается в runtime через Activity Result API.»

---

## 14. Микрофон: MediaRecorder и MediaPlayer

**Тема лекции:** аудио API, RECORD_AUDIO permission, Handler, жизненный цикл ресурсов.

### Поток записи

```
Записать → ensureAudioPermission → startRecording()
    → MediaRecorder: MIC, MPEG_4, AAC → temp .m4a
    → Handler каждые 100ms обновляет ProgressBar (getMaxAmplitude)
Остановить → stopRecording() → releaseRecorder()
Воспроизвести → MediaPlayer.setDataSource → prepare → start
```

### Ключевые строки

```java
mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
mediaRecorder.prepare();
mediaRecorder.start();
```
Настройка формата записи: микрофон → M4A/AAC.

```java
int amplitude = mediaRecorder.getMaxAmplitude();
int level = Math.min(100, amplitude / 1000);
levelBar.setProgress(level);
handler.postDelayed(this, 100);
```
Визуализация **уровня громкости** во время записи.

```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    return new MediaRecorder(requireContext());
}
return new MediaRecorder();
```
На Android 12+ конструктор MediaRecorder требует Context.

```java
@Override
public void onDestroyView() {
    handler.removeCallbacks(levelUpdater);
    releaseRecorder();
    releasePlayer();
    super.onDestroyView();
}
```
**Обязательное** освобождение ресурсов — иначе утечки и блокировка микрофона.

**На защите:** «MediaRecorder пишет в файл, MediaPlayer воспроизводит. getMaxAmplitude даёт относительный уровень сигнала. Ресурсы release() в onDestroyView и onStop.»

---

## 15. Сеть: Retrofit и REST API

**Тема лекции:** HTTP клиент, REST, JSON, асинхронные запросы, POJO модель.

### Архитектура сетевого слоя

```
NetworkFragment
    → RetrofitClient.getApi()
        → JsonPlaceholderApi (interface)
            → GET https://jsonplaceholder.typicode.com/posts/{id}
                → Gson → Post (Java object)
```

### JsonPlaceholderApi.java

```java
@GET("posts/{id}")
Call<Post> getPost(@Path("id") int id);
```
**Декларативное** описание REST-метода. Retrofit сам собирает URL и парсит ответ.

### RetrofitClient.java

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build();
api = retrofit.create(JsonPlaceholderApi.class);
```
Singleton Retrofit + Gson converter. `create()` генерирует реализацию интерфейса.

### Post.java

```java
private int id;
private String title;
private String body;
// getters
```
POJO — поля совпадают с JSON-ключами для автоматического маппинга Gson.

### NetworkFragment.java

```java
RetrofitClient.getApi().getPost(1).enqueue(new Callback<Post>() {
    @Override
    public void onResponse(Call<Post> call, Response<Post> response) {
        if (!response.isSuccessful() || response.body() == null) { ... }
        Post post = response.body();
        resultText.setText(...);
    }

    @Override
    public void onFailure(Call<Post> call, Throwable t) { ... }
});
```
`enqueue` — асинхронный запрос (не блокирует UI). `onResponse` / `onFailure` — callbacks.

**На защите:** «Retrofit превращает HTTP в Java-интерфейс. Gson десериализует JSON в Post. Запрос асинхронный через enqueue. Тестовый API — jsonplaceholder.typicode.com.»

---

## 16. Карта заведений: Yandex MapKit + RecyclerView

**Тема лекции:** карты, маркеры, RecyclerView, Adapter pattern, BuildConfig.

### Модель Place.java

```java
public Point getPoint() {
    return new Point(latitude, longitude);
}
```
Обёртка над координатами Yandex MapKit.

### PlacesFragment — инициализация карты

```java
if (BuildConfig.MAPKIT_API_KEY.isEmpty()) {
    return inflater.inflate(R.layout.fragment_places_placeholder, container, false);
}
```
Без API-ключа — заглушка вместо карты.

```java
map = mapView.getMapWindow().getMap();
placemarksCollection = map.getMapObjects().addCollection();
```
Объект карты и коллекция меток.

```java
PlacemarkMapObject placemark = placemarksCollection.addPlacemark();
placemark.setGeometry(place.getPoint());
placemark.setIcon(iconProvider, iconStyle);
placemark.setUserData(place);
placemark.addTapListener(placemarkTapListener);
```
Для каждого заведения — метка с иконкой, данными и обработчиком нажатия.

### fitAllPlacesOnMap — доп. функция

```java
BoundingBox box = new BoundingBox(southWest, northEast);
Geometry geometry = Geometry.fromBoundingBox(box);
CameraPosition position = map.cameraPosition(geometry);
map.move(position, animation, null);
```
Вычисление bounding box по всем точкам → камера показывает все метки.

### Связь списка и карты

```java
// RecyclerView
adapter = new PlacesAdapter(this);
recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
recyclerView.setAdapter(adapter);

// клик в списке
@Override
public void onPlaceClick(Place place) {
    selectPlace(place, true);  // moveCamera = true
}

// клик по метке
placemarkTapListener → selectPlace(place, false);
```

```java
private void selectPlace(Place place, boolean moveCamera) {
    detailTitle.setText(place.getTitle());
    ...
    adapter.setSelectedId(place.getId());
    if (moveCamera) {
        map.move(new CameraPosition(place.getPoint(), 16.0f, 0.0f, 0.0f), ...);
    }
}
```
Единый метод выбора: обновляет панель деталей, подсветку в списке и опционально двигает камеру.

### PlacesAdapter.java — RecyclerView

```java
public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_place, parent, false);
    return new PlaceViewHolder(view);
}

public void onBindViewHolder(PlaceViewHolder holder, int position) {
    holder.bind(place, selected);
    holder.itemView.setOnClickListener(v -> listener.onPlaceClick(place));
}
```
Классический паттерн **ViewHolder**: создание ячейки один раз, привязка данных при скролле.

### Lifecycle MapKit

```java
@Override
public void onStart() {
    MapKitFactory.getInstance().onStart();
    mapView.onStart();
}

@Override
public void onStop() {
    mapView.onStop();
    MapKitFactory.getInstance().onStop();
}
```
Обязательные вызовы для корректной работы карты.

**На защите:** «Экран совмещает MapKit и RecyclerView. 4 тестовых заведения в Москве. Клик по списку или метке синхронизирует UI. FAB подгоняет камеру под все точки. MapKit инициализируется в Application.»

---

## 17. Зависимости (build.gradle.kts)

| Зависимость | Для чего в проекте |
|-------------|-------------------|
| `androidx.appcompat` | AppCompatActivity, совместимость UI |
| `material` | Material Design компоненты |
| `androidx.constraintlayout` | Разметки |
| `androidx.coordinatorlayout` | CoordinatorLayout (если используется) |
| `firebase-auth` + `google-services` | LoginActivity |
| `retrofit` + `converter-gson` | NetworkFragment |
| `work-runtime` | BackgroundWorkFragment, UploadWorker |
| `recyclerview` | PlacesFragment |
| `maps.mobile` (Yandex) | PlacesFragment, MireaApplication |

```kotlin
buildConfigField("String", "MAPKIT_API_KEY", "\"${mapkitApiKey}\"")
```
Ключ из `local.properties` → `BuildConfig.MAPKIT_API_KEY`.

---

## 18. Типичные вопросы на защите

### Общие

**Q: Чем Activity отличается от Fragment?**  
A: Activity — самостоятельный экран с полным lifecycle. Fragment — часть UI внутри Activity, переиспользуемая, имеет свой layout и lifecycle, но зависит от host Activity.

**Q: Зачем AndroidManifest?**  
A: Регистрирует компоненты (Activity, Provider), объявляет разрешения и features, указывает LAUNCHER Activity и Application class.

**Q: Что такое Intent?**  
A: Объект для запуска Activity (`startActivity(new Intent(this, MainActivity.class))`) или передачи данных.

### Firebase

**Q: Как хранится сессия пользователя?**  
A: Firebase Auth сохраняет токен локально. `getCurrentUser()` возвращает пользователя до явного `signOut()`.

### SharedPreferences vs Files

**Q: Когда SharedPreferences, когда файлы?**  
A: SharedPreferences — мало простых key-value (профиль). Файлы — большие текстовые данные, заметки, бинарные данные.

### WorkManager

**Q: Почему не Thread?**  
A: WorkManager учитывает Doze, перезапуск процесса, constraints (сеть), показывает статус через LiveData.

### Permissions

**Q: Зачем объявлять permission в Manifest и запрашивать runtime?**  
A: Manifest — декларация для системы и Store. Runtime — для опасных разрешений пользователь должен подтвердить явно.

### Retrofit

**Q: Синхронный или асинхронный запрос?**  
A: `enqueue` — асинхронный (используем). `execute` — синхронный, блокирует поток (не на UI).

### MapKit

**Q: Почему initialize в Application?**  
A: MapKit требует однократной инициализации с API key до любого MapView.

### Sensors

**Q: Почему unregisterListener в onPause?**  
A: Датчики потребляют батарею; когда экран не виден — слушатель не нужен.

---

## 19. Краткая шпаргалка «экран → технология»

```
LoginActivity          → FirebaseAuth, Intent, валидация формы
MainActivity           → DrawerLayout, NavigationView, FragmentTransaction, Toolbar
DataFragment           → Fragment template, Bundle args
ProfileFragment        → SharedPreferences, TextInputEditText
FilesFragment          → File I/O, ListView, MaterialAlertDialogBuilder
WebViewFragment        → WebView, WebViewClient, ConnectivityManager
BackgroundWorkFragment → WorkManager, Constraints, LiveData
UploadWorker           → Worker.doWork(), Result
CompassFragment        → SensorManager, SensorEventListener, getRotationMatrix
CameraCollageFragment  → Activity Result API, FileProvider, TakePicture
MicrophoneFragment     → MediaRecorder, MediaPlayer, Handler, RECORD_AUDIO
NetworkFragment        → Retrofit, Callback, Gson, ProgressBar
PlacesFragment         → Yandex MapKit, Placemark, RecyclerView, PlacesAdapter
MireaApplication       → MapKitFactory.initialize
PermissionHelper       → ContextCompat.checkSelfPermission
AndroidManifest        → permissions, LAUNCHER, FileProvider
```

---

## Чек-лист перед защитой

- [ ] Могу объяснить путь запуска: Login → Main → Fragment
- [ ] Знаю, где объявлены разрешения и где запрашиваются в runtime
- [ ] Могу показать строку `SharedPreferences.edit().apply()`
- [ ] Понимаю разницу `onCreateView` и `onViewCreated` у Fragment
- [ ] Могу объяснить `FragmentTransaction.replace`
- [ ] Знаю, зачем FileProvider и что в `file_paths.xml`
- [ ] Понимаю `enqueue` vs блокирующий вызов в Retrofit
- [ ] Могу описать алгоритм компаса (2 датчика → azimuth)
- [ ] Знаю lifecycle MapKit (`onStart`/`onStop`)
- [ ] Понимаю, откуда берётся `MAPKIT_API_KEY`

---

*Документ составлен по исходному коду MireaProject и PDF «Практика № 3–8» (РТУ МИРЭА, 2023/24). Полный текст PDF извлечён в `PDFs/_extracted.txt`.*
