package ru.hogwarts.school.controlls;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        return studentService.create(student);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> get(@PathVariable Long id) {
        Student student = studentService.get(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public List<Student> getAll() {
        return studentService.getAll();
    }

    @PutMapping
    public ResponseEntity<Student> update(@RequestBody Student student) {
        Student updated = studentService.update(student);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/age")
    public List<Student> getStudentsByAgeBetween(@RequestParam int min, @RequestParam int max) {
        return studentService.getStudentsByAgeBetween(min, max);
    }

    @GetMapping("/faculty/{id}")
    public ResponseEntity<?> getFacultyByStudentId(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getFacultyByStudentId(id));
    }

    @GetMapping("/count")
    public Integer getStudentsCount() {
        return studentRepository.findAll().size();
    }

    @GetMapping("/average-age")
    public Double getAverageAge() {
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0);
    }

    @GetMapping("/last-five")
    public List<Student> getLastFiveStudents() {
        return studentRepository.findAll().stream()
                .sorted(Comparator.comparing(Student::getId).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    // Шаг 1 из ДЗ 4.5: Имена на "А" в верхнем регистре
    @GetMapping("/names-a")
    public List<String> getNamesStartingWithA() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .map(String::toUpperCase)
                .filter(name -> name.startsWith("A"))
                .sorted()
                .collect(Collectors.toList());
    }

    // Шаг 1 из ДЗ 4.6: Параллельный вывод
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

    // Шаг 2 из ДЗ 4.6: Синхронизированный вывод
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