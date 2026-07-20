# YouTrack UI Test Automation

Автоматизированные UI-тесты для локального веб-приложения **YouTrack** (JetBrains), развёрнутого в Docker.

Проект реализован на **Java 21** с использованием фреймворка **Selenide**, библиотеки **JUnit 5** и **AssertJ**.

---

## 🏗 Архитектура и Технологический стек

* **Java 21** — базовый язык разработки.
* **Selenide (Selenium WebDriver)** — обёртка над Selenium WebDriver с автоматическим управлением ожиданиями, сессиями браузера и снимками экранов.
* **Page Object Model (POM)** — разделение логики страниц и элементов (`LoginPage`, `IssuesListPage`, `IssueDetailsPage`) и вынесение переиспользуемых компонентов (`NavigationBar`, `CreateIssueDialog`, `CommentSection`).
* **JUnit 5 (Jupiter)** — фреймворк для запуска тестов, поддержки параметризации и параллелизации.
* **AssertJ** — fluent-утверждения для читаемых проверок.
* **OpenCSV / JUnit CsvFileSource** — реализация Data-Driven Testing (DDT).
* **SLF4J + SimpleLogger** — логирование шагов и событий выполнения.

---

## 📁 Структура проекта

```text
UITest/
├── src/
│   └── main/
│       ├── java/
│       │   └── org/example/
│       │       ├── components/          # Компоненты UI (NavigationBar, CommentSection, CreateIssueDialog)
│       │       ├── util/                # Утилиты (PageHelper, RetryUtils)
│       │       └── pages/               # Page Objects (BasePage, LoginPage, MainPage, IssuesListPage, IssueDetailsPage)
│   └── test/
│       ├── java/
│       │   └── org/example/
│       │       ├── tests/               # Тестовые классы (GeneralTests)
│       │       ├── util/                # Утилиты (IssueManager, TestConfig, TestsUtils)
│       │       └── TestLogger.java      # JUnit 5 Extension для логирования старта/успеха/падения тестов
│       └── resources/
│           ├── csv-data/                # CSV-файлы с данными для параметризованных тестов
│           ├── config.properties        # Конфигурация URL, логинов и таймаутов
│           ├── junit-platform.properties# Настройки параллельного запуска JUnit 5
│           └── simplelogger.properties # Настройка формата и уровня логов
├── target/
│   └── screenshots/                     # Автоматически сохраняемые скриншоты при ошибках
└── pom.xml                              # Maven зависимости
```

---

## ⚙️ Конфигурация

### 1. `src/test/resources/config.properties`
Содержит параметры подключения и учетные данные YouTrack:

```properties
app.baseUrl=http://localhost:8080
app.username=TestUser
app.password=TestMultiP@ss

selenide.timeout=10000
```

### 2. Параллельное выполнение (`src/test/resources/junit-platform.properties`)
Настроено параллельное выполнение тестов JUnit 5 в 2 параллельных потока:

```properties
junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = concurrent
junit.jupiter.execution.parallel.mode.classes.default = concurrent
junit.jupiter.execution.parallel.config.strategy = fixed
junit.jupiter.execution.parallel.config.fixed.parallelism = 2
junit.jupiter.execution.parallel.config.fixed.max-pool-size = 3
```

### 3. Автоматические скриншоты и Логирование
* Скриншоты при падении тестов автоматически снимаются с помощью `ScreenShooterExtension` в каталог `target/screenshots`.
* Журнал выполнения выводится в консоль с отметкой времени через `TestLogger` и `simplelogger.properties`.

---

## 🚀 Запуск тестов

### Предварительные требования
1. Локально запущенный Docker-контейнер YouTrack на порту `8080`.
2. Установленный браузер **Google Chrome**.

### Запуск через Maven
Из корня модуля `UITest`:

```bash
mvn clean test
```

### Гарантия чистой среды
Для предотвращения накопления тестовых данных в UI, класс `GeneralTests` отслеживает динамически создаваемые ID задач в потокобезопасном списке `Collections.synchronizedList` и выполняет очистку созданных сущностей в блоке `@AfterEach` с помощью утилиты `IssueManager`.