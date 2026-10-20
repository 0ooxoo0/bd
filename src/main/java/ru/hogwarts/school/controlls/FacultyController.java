package ru.hogwarts.school.controlls;

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
    public Faculty get(@PathVariable Long id) {
        return facultyService.getFaculty(id);
    }

    @GetMapping
    public List<Faculty> getAll() {
        return facultyService.getAllFaculties();
    }

    @PutMapping
    public Faculty update(@RequestBody Faculty faculty) {
        return facultyService.updateFaculty(faculty);
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
    public List<Student> getStudentsByFacultyId(@PathVariable Long id) {
        return facultyService.getStudentsByFacultyId(id);
    }
}
