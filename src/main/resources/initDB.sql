create SCHEMA IF NOT EXISTS Employees;
create TABLE IF NOT EXISTS employee
(
    id  bigserial PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    hire_date DATE  NOT NULL
);
create TABLE IF NOT EXISTS salary
(
    ID  integer PRIMARY KEY,
    employee_id  integer NOT NULL UNIQUE,
    amount  decimal(12,4)  NOT NULL,
    start_date DATE  NOT NULL
);
create TABLE IF NOT EXISTS salary_history
(
    ID  integer PRIMARY KEY,
    employee_id  integer NOT NULL,
    amount  decimal(12,4)  NOT NULL,
    start_date DATE  NOT NULL,
    end_date DATE
);
