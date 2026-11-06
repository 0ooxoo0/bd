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

    public Student createStudent(Student student) {
        logger.info("Was invoked method for createStudent");
        logger.debug("Creating student with name: {}", student.getName());
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        logger.info("Was invoked method for getStudent");
        return studentRepository.findById(id).orElse(null);
    }

    public List<Student> getAllStudents() {
        logger.info("Was invoked method for getAllStudents");
        return studentRepository.findAll();
    }

    public Student updateStudent(Student student) {
        logger.info("Was invoked method for updateStudent");
        if (!studentRepository.existsById(student.getId())) {
            logger.warn("Student with id {} not found for update", student.getId());
            return null;
        }
        logger.debug("Updating student with id: {}", student.getId());
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for deleteStudent");
        studentRepository.deleteById(id);
    }

    public List<Student> getStudentsByAge(int age) {
        logger.info("Was invoked method for getStudentsByAge");
        return studentRepository.findByAge(age);
    }

    public List<Student> getStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method for getStudentsByAgeBetween");
        return studentRepository.findByAgeBetween(min, max);
    }

    public List<Student> getStudentsByName(String name) {
        logger.info("Was invoked method for getStudentsByName");
        return studentRepository.findByNameIgnoreCase(name);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        logger.info("Was invoked method for getFacultyByStudentId");
        Student student = studentRepository.findById(studentId).orElse(null);
        return student == null ? null : student.getFaculty();
    }

    public long getStudentsCount() {
        logger.info("Was invoked method for getStudentsCount");
        return studentRepository.countAllStudents();
    }

    public double getAverageAge() {
        logger.info("Was invoked method for getAverageAge");
        Double avg = studentRepository.getAverageAge();
        return avg != null ? avg : 0.0; // возвращаем 0.0, если студентов нет
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for getLastFiveStudents");
        return studentRepository.findTop5ByOrderByIdDesc();
    }
}
