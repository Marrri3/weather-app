# weather-app

Проект выполнен в рамках учебного проекта по МДК 01.03 Разработка мобильных приложений.  
Это Android-приложение на Kotlin, которое получает и отображает текущую погоду по названию города или по геолокации устройства.  
Данные загружаются через REST API OpenWeatherMap.

## Описание
- **Цель:** Получение и отображение актуальной погоды (температура, ощущается как, описание, влажность, скорость ветра, иконка) по городу или текущему местоположению  
- **Платформа:** Android Studio (Kotlin + Retrofit + View Binding)  
- **API:** OpenWeatherMap — Current Weather Data  

## Установка

### Требования
- Android Studio (рекомендуется 2024.1+ или новее)  
- JDK 11+  
- Устройство / эмулятор с Android 7.0+ (API 24+)

### Установка зависимостей
Зависимости указаны в файле `app/build.gradle.kts`:

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    // Сеть и парсинг JSON
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Геолокация
    implementation("com.google.android.gms:play-services-location:21.0.1")
    
    // Загрузка иконок погоды
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    
    // Тесты
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

## Команды запуска приложения
Нажмите зелёную кнопку **Run** в верхней панели задач(треугольник)  
или используйте сочетание клавиш: **Shift + F10** (Windows/Linux) / **Control + R** (macOS).

## Описание API
- `GET /weather?q={city}` — Получение текущей погоды по названию города
- `GET /weather?lat={lat}&lon={lon}` — Получение текущей погоды по координатам

## Структура репозитория
weather-app/
├── app/                    
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/weather_app/  
│   │   │   ├── res/                            
│   │   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/                 
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── local.properties        
└── README.md               
