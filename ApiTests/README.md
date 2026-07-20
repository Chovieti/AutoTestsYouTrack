# YouTrack API Test Automation

Автоматизированные тесты для проверки API сервера **YouTrack** (JetBrains), развернутого локально в Docker.

Проект реализован на **Java 21** с использованием **RestAssured**, **TestNG** и **OpenCSV**.

---

## 🏗 Архитектура и Стеки

* **Java 21** (DTO реализованы через `record` c поддержкой Jackson и паттерна Builder).
* **RestAssured** (взаимодействие с REST API YouTrack).
* **TestNG** (параллельный запуск тестов и параметризация).
* **OpenCSV** (чтение тестовых данных из CSV-файлов).
* **Maven** (управление зависимостями и сборкой).

---

## 📁 Структура проекта

```text
ApiTests/
├── src/
│   └── test/
│       ├── java/
│       │   └── org/example/
│       │       ├── api/               # API-клиенты (BaseApiClient, IssueApiClient)
│       │       ├── dto/               # DTO (IssueDTO, CustomFieldDTO, ProjectDTO)
│       │       ├── test/              # Тестовые классы (GeneralTests)
│       │       └── util/              # Утилиты (CsvDataProviders, TestConfig)
│       └── resources/
│           ├── csv-data/              # CSV файлы для Data-Driven тестов
│           ├── application.properties # Конфигурация окружения
│           └── testng.xml             # Конфигурация запуска TestNG
```

---

## ⚙️ Конфигурация

### 1. `src/test/resources/application.properties`
Содержит базовые параметры подключения к локальному YouTrack:

```properties
base.url=http://localhost:8080
base.path=/api
default.token=perm-VGVzdFVzZXI=.NDQtMg==.4n7KzuonqiIz0bqLMNwaeR2h5Ti8yT
default.project.id=0-1
```

### 2. Параллельный запуск (`src/test/resources/testng.xml`)
Тесты настроены на параллельное выполнение методов в **3 потока**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "[https://testng.org/testng-1.0.dtd](https://testng.org/testng-1.0.dtd)">
<suite name="YouTrack API Suite" parallel="methods" thread-count="3">
    <test name="General API Tests">
        <classes>
            <class name="org.example.test.GeneralTests"/>
        </classes>
    </test>
</suite>
```

---

## 🚀 Запуск тестов

### Предварительные требования
1. Развернутый локально сервис YouTrack в Docker на порту `8080`.
2. Сгенерированный Permanent Token в профиле YouTrack записан в файл application.properties(изначально уже стоит на основе существующих данных в youtrack).

### Запуск через Maven
Из директории модуля `ApiTests`:

```bash
mvn clean test
```

### Чистка данных
Тесты используют изолированный очиститель с `ThreadLocal<List<String>>`, который автоматически удаляет все созданные во время прогона сущности после каждого теста, не ломая параллельное выполнение.