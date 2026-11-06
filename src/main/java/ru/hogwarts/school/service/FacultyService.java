package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for createFaculty");
        logger.debug("Creating faculty with name: {}", faculty.getName());
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(Long id) {
        logger.info("Was invoked method for getFaculty");
        return facultyRepository.findById(id).orElse(null);
    }

    public List<Faculty> getAllFaculties() {
        logger.info("Was invoked method for getAllFaculties");
        return facultyRepository.findAll();
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Was invoked method for updateFaculty");
        if (!facultyRepository.existsById(faculty.getId())) {
            logger.warn("Faculty with id {} not found for update", faculty.getId());
            return null;
        }
        logger.debug("Updating faculty with id: {}", faculty.getId());
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for deleteFaculty");
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getFacultiesByName(String name) {
        logger.info("Was invoked method for getFacultiesByName");
        return facultyRepository.findByNameIgnoreCase(name);
    }

    public List<Faculty> getFacultiesByColor(String color) {
        logger.info("Was invoked method for getFacultiesByColor");
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public List<Faculty> findFacultiesByNameOrColor(String search) {
        logger.info("Was invoked method for findFacultiesByNameOrColor");
        logger.debug("Searching faculties by name or color: {}", search);
        return facultyRepository
                .findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(search, search);
    }

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        logger.info("Was invoked method for getStudentsByFacultyId");
        return studentRepository.findByFacultyId(facultyId);
    }

    public String getLongestFacultyName() {
        logger.info("Was invoked method for getLongestFacultyName");
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }
}
