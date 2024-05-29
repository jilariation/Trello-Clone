# Trello Clone
**Trello Clone** - аналог сервиса Trello, позволяющий пользователям управлять проектами по методологии Kanban.

## Ахитектура приложения
Приложение разработано в микросервисной архитектуре и состоит из 3х сервисов:
- [TrelloClone](https://github.com/jilariation/Trello-Clone) - сервис для работы с задачами
- [Upload Service](https://github.com/jilariation/upload-service) - сервис выгрузки файлов в S3 хранилище Yandex Cloud
- [Mail Service](https://github.com/jilariation/mail-service) - сервис отправки оповещений на электронную почту

![Архитектура приложения](attach/architecture.png "Архитектура приложения")

## Использованные технологии
- Java 17/21
- Spring Framework (Spring Boot, Spring Security, Spring Data JPA, Spring Web, Spring Mail, Spring Scheduler)
- PostgreSQL
- Liquibase
- Apache Kafka
- Testcontainers, JUnit, Mockito, MockMVC
- AWS SDK, Yandex Object Storage
- Swagger
- Docker - Dockefile, Docker Compose, Docker Hub
- Gradle
## Реализованный функционал
### Backend:
#### Операции:
- Авторизация пользователя по JWT
- Управления досками
- Работа с задачами:
  - CRUD операции с задачами 
  - управление исполнителями;
  - управление вложениями;
  - управления чеклистами;
  - написание комментариев к задачам;
- Раз в сутки сбор и отправка данных в **Apache Kafka** о пользователях, чьи задачи необходимо сдать сегодня
#### Тестирование:
- Написаны Unit-тесты для сервисов с помощью **JUnit** и **Mockito**
- Написаны интеграционные тесты для контроллеров с помощью **Testcontainers** и **MockMVC**
#### Схема базы данных:
![Схема базы данных](attach/schema.png "Trello DB")
### Рассыльщик писем
- Оповещение пользователя об успешной регистрации на указанную почту
- Оповещение пользователя списком назначенных ему задач, которые необходимо выполнить сегодня
### Файловый сервис
- Получение отправленных вложений и отправка их в объектное хранилище Yandex Object Storage
## Пример запросов к API
#### **GET** /api/v1/tasks - Получение списка всех задач
```javascript
[
    {
        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        "name": "string",
        "description": "string",
        "priority": "LOW",
        "status": "TODO",
        "creator": {
            "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            "username": "string",
            "name": "string",
            "last_name": "string",
            "email": "string"
        },
        "executors": [
            {
                "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "username": "string",
                "name": "string",
                "last_name": "string",
                "email": "string"
            }
        ],
        "tags": [
            "string"
        ],
        "comments_count": 0,
        "attachments_count": 0,
        "check_items_count": 0,
        "check_items_checked_count": 0,
        "end_date": "2024-05-21T07:19:38.607Z",
        "created_at": "2024-05-21T07:19:38.607Z",
        "updated_at": "2024-05-21T07:19:38.607Z",
        "closed_at": "2024-05-21T07:19:38.607Z"
    }
]
```
## Инструкция по запуску проекта
- Склонировать репозитории
```
https://github.com/jilariation/Trello-Clone
https://github.com/jilariation/upload-service
https://github.com/jilariation/mail-service
```
- Изменить файл **.env-git** на **.env** и ввести значения переменных окружения
- Запустить приложение
```
docker-compose up
```
- Страница с документацией будет доступна по ссылке: http://localhost:8080/swagger-ui/index.html
## Возможности улучшить функционал
- Возможность добавления разных досок
- Кеширование запросов
- Оптимизация запросов