package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student create(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student with name: {}", student.getName());
        return studentRepository.save(student);
    }

    public Student get(Long id) {
        logger.info("Was invoked method for get student");
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            // Исправлено: используем {} вместо конкатенации
            logger.error("Student not found with id = {}", id);
        }
        return student;
    }

    public List<Student> getAll() {
        logger.info("Was invoked method for get all students");
        return studentRepository.findAll();
    }

    public Student update(Student student) {
        logger.info("Was invoked method for update student");
        if (!studentRepository.existsById(student.getId())) {
            // Исправлено: изменили текст, чтобы он не дублировался
            logger.error("Cannot update: student not found with id = {}", student.getId());
            return null;
        }
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        logger.info("Was invoked method for delete student");
        if (!studentRepository.existsById(id)) {
            // Исправлено: используем {} и изменили текст
            logger.warn("Cannot delete: student not found with id = {}", id);
        }
        studentRepository.deleteById(id);
    }

    public List<Student> getStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method for get students by age between");
        return studentRepository.findAll().stream()
                .filter(s -> s.getAge() >= min && s.getAge() <= max)
                .toList();
    }

    public Faculty getFacultyByStudentId(Long id) {
        logger.info("Was invoked method for get faculty by student id");
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            // Исправлено: изменили текст
            logger.error("Cannot get faculty: student not found with id = {}", id);
            return null;
        }
        return student.getFaculty();
    }
}