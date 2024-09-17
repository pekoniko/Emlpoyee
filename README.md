<b>Подготовка к запуску</b>

Для запуска приложения Employee через Docker необходимы следующие программы: Docker и jdk 17 или более новая.
После выкачивания репозитория необходимо поочереди запустить следующие комманды:
1) Для сборки пректа в jar файл - ".\mvnw clean package -DskipTests"
2) Для сборки файлов в docker контейнер - "docker compose build"
3) Для развёртки созданных контейнеров - "docker compose up java_app"
При необходимости смены пароля/пользователя нужно заменить значения параметров в "docker-compose.yml" и в "application.properties".
При дебаге или запуске программы в среде разработке необходимо сменить значение параметра "spring.datasource.url" в "application.properties" на "jdbc:postgresql://localhost:5432/postgres", т.к. стандартно указана ссылка на БД внутри контейнера.


<b>Описания программы:</b>
  
Приложение Employee создаёт БД со следующей структурой:
Информация о сотруднике (Employee) должна храниться в трёх таблицах.
Первая таблица "employee" должна содержит информацию о сотруднике: идентификатор (ID), имя (first_name), фамилия (last_name), должность (position), дата найма (hire_date).
Вторая таблица "salary" должна содержит информацию о текущей зарплате каждого сотрудника: идентификатор (ID), идентификатор сотрудника (employee_id), сумма зарплаты (amount), дата начала действия зарплаты (start_date).
Третья таблица "salary_history" хранит историю изменений зарплаты у сотрудников: идентификатор (ID), идентификатор сотрудника (employee_id), сумма зарплаты (amount), дата начала действия зарплаты (start_date), дата окончания действия зарплаты (end_date).


<b>Описание эндпоинтов:</b>

При работе приложения предоставляет доступ к созданной БД по средствам API со следующими запросами (где "localhost:8080/" - ссылка на API).

POST:

1) "localhost:8080/employees" - создаёт сотрудника с указанными в виде json файла именем "firstName", фамилией "lastName", должностью "position" и датой приёма на раюоту "hireDate" в формате yyyy-mm-dd.

GET:

2) "localhost:8080/employees/all" - показывает все записи из таблицы "employee".
3) "localhost:8080/employees/{ID}" - показывает запись сотрудника с указанным {ID}.
4) "localhost:8080/employees/{first_name}/{last_name}" -  показывает все записи сотрудников выбранным именем {first_name} и фамилией {last_name}.
5) "localhost:8080/employees/{ID}/amount" - показывает зарплату сотрудника с указанным {ID}.
6) "localhost:8080/employees/{ID}/date={dateString}" - показывает зарплату сотрудника указаннаую в таблице "salary_history" на указанную дату {dateString}. Дата указывается в формате yyyy-mm-dd.

PUT:

7) "localhost:8080/employees/{ID}" - обновляет запись сотрудника с указанным {ID} на запись в виде json файла именем "firstName", фамилией "lastName", должностью "position" и датой приёма на раюоту "hireDate" в формате yyyy-mm-dd.
8) "localhost:8080/employees/{ID}/amount={amount}" - менает запись о зарплате сотрудника с {ID} на значение {amount} в таблицах "salary" и "salary_history". В случае если сотруднику до этого не присваивалась зарплата, то создаётся новая запись.

DELETE:

9) "localhost:8080/employees/{ID}" - удаляет запись о сотрдунике с указанным {ID}, а также все записи о его зарплате.


<b>Вывод</b>

Результат каждого из успешно воспроизведённых запросов - JSON файл со следующей структурой:
{result : {[]}, error: "", success: true}.

-Где массив result - содержит запрошенные данные в случае успешного исполнения. 

-Объект error - содеоржит сообщение об ошибке в случае если запрос был произведён успешно, но данные возникла ошибка при исполнение.

-Объект success - принимает значение true  в случае если запрос был произведён успешно и false, если возникла ошибка.
