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
class StudentControllerTestRestTemplateTest {

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
    void createStudent_shouldReturnCreatedStudent() {
        Student request = new Student(null, "Created Student", 19);
        request.setFaculty(faculty);

        ResponseEntity<Student> response = restTemplate.postForEntity("/students", request, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Created Student");
        assertThat(created.getAge()).isEqualTo(19);
        assertThat(created.getFaculty()).isNotNull();
        assertThat(created.getFaculty().getId()).isEqualTo(faculty.getId());
    }

    @Test
    void getStudentById_shouldReturnStudent() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                "/students/{id}", Student.class, student.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student found = response.getBody();
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(student.getId());
        assertThat(found.getName()).isEqualTo("Test Student");
        assertThat(found.getAge()).isEqualTo(17);
        assertThat(found.getFaculty()).isNotNull();
        assertThat(found.getFaculty().getId()).isEqualTo(faculty.getId());
    }

    @Test
    void getAllStudents_shouldReturnStudents() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity("/students", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Arrays.asList(response.getBody()))
                .extracting(Student::getId)
                .contains(student.getId());
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() {
        Student update = new Student(student.getId(), "Updated Student", 21);
        update.setFaculty(faculty);

        ResponseEntity<Student> response = restTemplate.exchange(
                "/students", HttpMethod.PUT, new HttpEntity<>(update), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student updated = response.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(student.getId());
        assertThat(updated.getName()).isEqualTo("Updated Student");
        assertThat(updated.getAge()).isEqualTo(21);
        assertThat(updated.getFaculty()).isNotNull();
        assertThat(updated.getFaculty().getId()).isEqualTo(faculty.getId());
    }

    @Test
    void deleteStudent_shouldReturnOkAndRemoveStudent() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/students/{id}", HttpMethod.DELETE, null, Void.class, student.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        ResponseEntity<Student> afterDelete = restTemplate.getForEntity(
                "/students/{id}", Student.class, student.getId());
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getStudentsByAgeBetween_shouldReturnFilteredStudents() {
        Student older = new Student(null, "Older Student", 30);
        older.setFaculty(faculty);
        older = studentRepository.save(older);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                "/students/age-between?min={min}&max={max}", Student[].class, 16, 17);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student[] filtered = response.getBody();
        assertThat(filtered).isNotNull();
        assertThat(Arrays.asList(filtered))
                .extracting(Student::getId)
                .contains(student.getId())
                .doesNotContain(older.getId());
        assertThat(filtered)
                .allMatch(found -> found.getAge() >= 16 && found.getAge() <= 17);
    }

    @Test
    void getFacultyByStudentId_shouldReturnFaculty() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "/students/{id}/faculty", Faculty.class, student.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty found = response.getBody();
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(faculty.getId());
        assertThat(found.getName()).isEqualTo("Test Faculty");
        assertThat(found.getColor()).isEqualTo("test-color");
    }

    @Test
    void getStudentById_whenNotFound_shouldReturn404() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                "/students/{id}", Student.class, 999999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
