package ru.hogwarts.school.controlls;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    public StudentController(StudentService studentService, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> get(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        return student == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(student);
    }

    @GetMapping
    public List<Student> getAll() {
        return studentService.getAllStudents();
    }

    @PutMapping
    public ResponseEntity<Student> update(@RequestBody Student student) {
        Student updated = studentService.updateStudent(student);
        return updated == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping("/age-between")
    public List<Student> getStudentsByAgeBetween(@RequestParam int min,
                                                 @RequestParam int max) {
        return studentService.getStudentsByAgeBetween(min, max);
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getFacultyByStudentId(@PathVariable Long id) {
        Faculty faculty = studentService.getFacultyByStudentId(id);
        return faculty == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(faculty);
    }

    @GetMapping("/count")
    public long getStudentsCount() {
        return studentService.getStudentsCount();
    }

    @GetMapping("/average-age")
    public double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/last-five")
    public List<Student> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }

    @GetMapping("/names-a")
    public List<String> getStudentNamesStartingWithA() {
        return studentService.getStudentNamesStartingWithA();
    }

    @GetMapping("/print-parallel")
    public void printParallel() {
        List<Student> students = studentRepository.findAll();
        if (students.size() >= 6) {
            System.out.println(students.get(0).getName());
            System.out.println(students.get(1).getName());

            new Thread(() -> {
                System.out.println(students.get(2).getName());
                System.out.println(students.get(3).getName());
            }).start();

            new Thread(() -> {
                System.out.println(students.get(4).getName());
                System.out.println(students.get(5).getName());
            }).start();
        }
    }

    @GetMapping("/print-synchronized")
    public void printSynchronized() {
        List<Student> students = studentRepository.findAll();
        if (students.size() >= 6) {
            printName(students.get(0).getName());
            printName(students.get(1).getName());

            new Thread(() -> {
                printName(students.get(2).getName());
                printName(students.get(3).getName());
            }).start();

            new Thread(() -> {
                printName(students.get(4).getName());
                printName(students.get(5).getName());
            }).start();
        }
    }

    private synchronized void printName(String name) {
        System.out.println(name);
    }
}