-- Кодировка для корректного чтения кириллицы
SET client_encoding = 'UTF8';

-- Шаг 2. Простой запрос: все студенты
SELECT * FROM student;

-- Шаг 3.1. Студенты, возраст которых между 10 и 20
SELECT * FROM student
WHERE age BETWEEN 10 AND 20;

-- Шаг 3.2. Только имена всех студентов
SELECT name FROM student;

-- Шаг 3.3. Студенты, в имени которых есть буква "о" (без учёта регистра)
SELECT * FROM student
WHERE name ILIKE '%о%';

-- Шаг 3.4. Студенты, возраст которых меньше их идентификатора
SELECT * FROM student
WHERE age < id;

-- Шаг 3.5. Все студенты, упорядоченные по возрасту
SELECT * FROM student
ORDER BY age;
