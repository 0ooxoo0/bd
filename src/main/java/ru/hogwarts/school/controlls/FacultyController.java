package ru.hogwarts.school.controlls;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty create(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> get(@PathVariable Long id) {
        Faculty faculty = facultyService.getFaculty(id);
        return faculty == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(faculty);
    }

    @GetMapping
    public List<Faculty> getAll() {
        return facultyService.getAllFaculties();
    }

    @PutMapping
    public ResponseEntity<Faculty> update(@RequestBody Faculty faculty) {
        Faculty updated = facultyService.updateFaculty(faculty);
        return updated == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }

    @GetMapping("/search")
    public List<Faculty> search(@RequestParam String search) {
        return facultyService.findFacultiesByNameOrColor(search);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsByFacultyId(@PathVariable Long id) {
        if (facultyService.getFaculty(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyService.getStudentsByFacultyId(id));
    }
}
