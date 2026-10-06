package ru.hogwarts.school.controlls;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService studentService) {
        this.facultyService = studentService;
    }

    @PostMapping
    public Faculty create(@RequestBody Faculty student) {
        return facultyService.createFaculty(student);
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
    public Faculty update(@RequestBody Faculty student) {
        return facultyService.updateFaculty(student);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }
}
