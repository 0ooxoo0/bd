-- Запрос 1: Все студенты и их факультеты (имя, возраст, факультет)
SELECT s.name AS student_name, s.age, f.name AS faculty_name
FROM student s
         LEFT JOIN faculty f ON s.faculty_id = f.id;

-- Запрос 2: Только студенты, у которых есть аватарка
SELECT s.name AS student_name
FROM student s
         INNER JOIN avatar a ON s.id = a.student_id;