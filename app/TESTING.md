# Testing Guide

## Overview

В проекте реализованы три типа тестов:

1. **Unit Tests** - тестирование логики без Android framework
2. **Instrumented Tests** - тесты на Android устройстве/эмуляторе
3. **Screenshot Tests** - визуальное тестирование UI с Paparazzi

## Структура тестов

```
app/src/
├── test/
│   └── java/ru/diaries/mydiaries/
│       ├── data/
│       │   ├── local/
│       │   │   ├── converter/       # Конвертеры данных
│       │   │   └── mapper/          # Мапперы Entity -> Domain
│       │   └── repository/         # Репозитории
│       ├── di/                      # Test DI модули
│       ├── ui/                      # UI ViewModel тесты
│       │   ├── timeline/
│       │   ├── profile/
│       │   └── achievements/
│       └── domain/                  # Use Cases тесты
└── androidTest/
    └── java/ru/diaries/mydiaries/
        └── ui/                      # Compose UI тесты
```

## Запуск тестов

### Все Unit тесты
```bash
./gradlew test
```

### Все тесты определенного модуля
```bash
./gradlew :app:test
./gradlew :feature:todo:test
```

### Одиночный тест
```bash
./gradlew test --tests "ru.diaries.mydiaries.data.local.mapper.DiaryEntryMapperTest"
```

### Все тесты с отчетом
```bash
./gradlew test --continue
# Отчеты будут в: app/build/reports/tests/test/
```

### Instrumented тесты (требуется устройство/эмулятор)
```bash
./gradlew connectedAndroidTest
```

## Текущее покрытие

### ✅ Реализованные тесты

#### Data Layer
- `DiaryEntryMapperTest` - маппинг DiaryEntry
- `ExpenseMapperTest` - маппинг Expense
- `DateConverterTest` - конвертация дат
- `DiaryRepositoryTest` - репозиторий дневников

#### UI Layer
- `TimelineViewModelTest` - ViewModel для Timeline экрана

#### Screenshot Tests
- `TimelineScreenScreenshotTest` - скриншоты Timeline (заготовка)

## Написание новых тестов

### Unit тест для Mapper

```kotlin
package ru.diaries.mydiaries.data.local.mapper

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import ru.diaries.mydiaries.data.local.entity.YourEntity
import ru.diaries.mydiaries.data.model.YourDomain

class YourMapperTest {

    @Test
    fun `toDomain converts entity correctly`() {
        // Arrange
        val entity = YourEntity(id = 1, name = "Test")

        // Act
        val result = entity.toDomain()

        // Assert
        assertThat(result.id).isEqualTo(1)
        assertThat(result.name).isEqualTo("Test")
    }
}
```

### Unit тест для ViewModel с Turbine

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class YourViewModelTest {

    private lateinit var viewModel: YourViewModel
    private val repository: YourRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = YourViewModel(repository)
    }

    @Test
    fun `loadData updates state correctly`() = runTest {
        // Arrange
        val data = listOf(YourDomain(1, "Test"))
        coEvery { repository.getData() } returns flowOf(data)

        // Act
        viewModel.loadData()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.items).hasSize(1)
        }
    }
}
```

### Снимок тест с Paparazzi

```kotlin
class YourScreenScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun `YourScreen renders correctly`() {
        paparazzi.snapshot {
            YourScreen(
                state = YourState(title = "Test"),
                onAction = {}
            )
        }
    }
}
```

## Зависимости для тестирования

### Основные библиотеки
- **JUnit 4** - фреймворк для unit тестов
- **MockK** - мокирование объектов
- **Turbine** - тестирование Flow
- **AssertJ** - fluent assertions
- **Robolectric** - Android API в JVM тестах
- **Paparazzi** - скриншот тесты без эмулятора
- **Compose UI Test** - тестирование Compose UI

### Добавлены в gradle/libs.versions.toml:
```toml
mockk = "1.13.12"
coroutinesTest = "1.9.0"
turbine = "1.1.0"
robolectric = "4.13"
assertj = "3.26.0"
paparazzi = "1.3.4"
```

## План расширения тестового покрытия

### Phase 1: Data Layer (70% покрытия)
- [ ] Добавить тесты для всех мапперов
- [ ] Тесты для всех DAO (с Robolectric)
- [ ] Тесты для всех репозиториев
- [ ] Тесты для PreferencesManager
- [ ] Тесты для AchievementRepository

### Phase 2: Domain Layer (80% покрытия)
- [ ] Тесты для всех Use Cases
- [ ] Тесты для бизнес-логики
- [ ] Тесты для валидаторов

### Phase 3: UI Layer (60% покрытия)
- [ ] TimelineViewModel - завершить тесты
- [ ] ProfileViewModel
- [ ] AchievementsViewModel
- [ ] EditorViewModel
- [ ] StatisticsViewModel
- [ ] HistoryViewModel
- [ ] AddExpenseDialogViewModel

### Phase 4: Screenshot Tests
- [ ] TimelineScreen скриншоты
- [ ] ProfileScreen скриншоты
- [ ] AchievementsScreen скриншоты
- [ ] Dialog скриншоты
- [ ] Разные состояния UI (loading, error, empty)

### Phase 5: Integration Tests
- [ ] End-to-end сценарии
- [ ] Navigation тесты
- [ ] Theme тесты (dark/light mode)

## CI/CD Интеграция

Добавить в `.github/workflows/tests.yml`:

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      - name: Run unit tests
        run: ./gradlew test
      - name: Generate test report
        if: always()
        run: ./gradlew testDebugUnitTest
```

## Best Practices

1. **AAA Pattern** - Arrange, Act, Assert
2. **Именование тестов** - `methodName_expectedBehavior_stateUnderTest`
3. **Одна ответственность** - один тест проверяет одну вещь
4. **Изоляция** - тесты не должны зависеть друг от друга
5. **Mock внешних зависимостей** - не тестируйте сторонние библиотеки

## Troubleshooting

### Тесты не запускаются
```bash
./gradlew clean test
```

### Ошибка Robolectric
Добавить в `src/test/resources/robolectric.properties`:
```
sdk=28
```

### Ошибка Flow/Turbine
Используйте `runTest` с `UnconfinedTestDispatcher`

### Проблемы с Hilt в тестах
Создайте `TestAppModule` с `@TestInstallIn`
