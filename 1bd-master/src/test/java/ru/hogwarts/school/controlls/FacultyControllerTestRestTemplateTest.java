package ru.hogwarts.school.controlls;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class FacultyControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private Faculty faculty;
    private Student student;

    @BeforeEach
    void setUp() {
        faculty = facultyRepository.save(new Faculty(null, "Test Faculty", "test-color"));
        student = new Student(null, "Test Student", 17);
        student.setFaculty(faculty);
        student = studentRepository.save(student);
    }

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() {
        Faculty request = new Faculty(null, "Created Faculty", "created-color");

        ResponseEntity<Faculty> response = restTemplate.postForEntity("/faculty", request, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Created Faculty");
        assertThat(created.getColor()).isEqualTo("created-color");
    }

    @Test
    void getFacultyById_shouldReturnFaculty() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "/faculty/{id}", Faculty.class, faculty.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty found = response.getBody();
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(faculty.getId());
        assertThat(found.getName()).isEqualTo("Test Faculty");
        assertThat(found.getColor()).isEqualTo("test-color");
    }

    @Test
    void getAllFaculties_shouldReturnFaculties() {
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity("/faculty", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Arrays.asList(response.getBody()))
                .extracting(Faculty::getId)
                .contains(faculty.getId());
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() {
        Faculty update = new Faculty(faculty.getId(), "Updated Faculty", "blue");

        ResponseEntity<Faculty> response = restTemplate.exchange(
                "/faculty", HttpMethod.PUT, new HttpEntity<>(update), Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty updated = response.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(faculty.getId());
        assertThat(updated.getName()).isEqualTo("Updated Faculty");
        assertThat(updated.getColor()).isEqualTo("blue");
    }

    @Test
    void deleteFaculty_shouldReturnOkAndRemoveFaculty() {
        Faculty toDelete = facultyRepository.save(new Faculty(null, "Delete Faculty", "black"));

        ResponseEntity<Void> response = restTemplate.exchange(
                "/faculty/{id}", HttpMethod.DELETE, null, Void.class, toDelete.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
        assertThat(facultyRepository.findById(toDelete.getId())).isEmpty();
    }

    @Test
    void searchFaculties_shouldReturnMatches() {
        Faculty other = facultyRepository.save(new Faculty(null, "Other Faculty", "green"));

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                "/faculty/search?search={search}", Faculty[].class, "Test");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty[] found = response.getBody();
        assertThat(found).isNotNull();
        assertThat(Arrays.asList(found))
                .extracting(Faculty::getId)
                .contains(faculty.getId())
                .doesNotContain(other.getId());
    }

    @Test
    void getStudentsByFacultyId_shouldReturnStudents() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                "/faculty/{id}/students", Student[].class, faculty.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student[] found = response.getBody();
        assertThat(found).isNotNull();
        assertThat(Arrays.asList(found))
                .extracting(Student::getId)
                .contains(student.getId());
    }

    @Test
    void getFacultyById_whenNotFound_shouldReturn404() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "/faculty/{id}", Faculty.class, 999999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
