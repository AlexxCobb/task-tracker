# Трекер задач
Современный мир заставляет нас держать в голове достаточно большое количество задач, которые нам нужно реализовать. Это могут быть как краткосрочные задачи, так и долгосрочные, состоящие из нескольких подзадач. Для того, чтобы суметь распланировать своё время грамотно, нам и нужен "Трекер задач".

## Описание
Приложение даёт возможность создавать простые задачи и эпики с подзадачами. Данный проект представляет собой бэкенд трекера задач, формирующий модель данных в подобном формате:

![front](https://github.com/user-attachments/assets/0f53d476-46a6-413b-940f-316502651975)

## Типы задач
- Task - простая задача;
- Subtask - подзадача;
- Epic - большая задача, состоящая из подзадач.

Каждая задача обладает следующими свойствами:

1. Название - суть задачи.
2. Описание - детали задачи.
3. ID - уникальный номер задачи.
4. Статус - отображение прогресса:
    - NEW - новая задача, к выполнению которой не приступили.
    - IN_PROGRESS - задача находится в работе.
    - DONE - задача завершена.
  
Для "подзадач" и "эпиков" справедливы следующие условия:
- для каждой подзадачи известно, в рамках какого "эпика" она выполняется;
- для каждого эпика известно, из каких подзадач он состоит;
- условие завершения эпика - исключительно выполнение всех подзадач.
  
Также трекер отображает последние просмотренные пользователем задачи.
