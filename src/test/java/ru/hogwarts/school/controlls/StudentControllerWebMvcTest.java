package ru.hogwarts.school.controlls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@ActiveProfiles("test")
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    // Spring Boot 4.1.1: @MockBean удалён; @MockitoBean — официальная замена.
    @MockitoBean
    private StudentService studentService;

    private Student student;
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        faculty = new Faculty(1L, "Gryffindor", "red");
        student = new Student(1L, "Harry", 17);
        student.setFaculty(faculty);
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() throws Exception {
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Harry","age":17}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void getStudentById_shouldReturnStudent() throws Exception {
        when(studentService.getStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/students/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void getAllStudents_shouldReturnStudents() throws Exception {
        Student second = new Student(2L, "Ron", 16);
        when(studentService.getAllStudents()).thenReturn(List.of(student, second));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() throws Exception {
        Student updated = new Student(1L, "Updated", 18);
        when(studentService.updateStudent(any(Student.class))).thenReturn(updated);

        mockMvc.perform(put("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":1,"name":"Updated","age":18}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void deleteStudent_shouldReturnOk() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/students/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(studentService).deleteStudent(1L);
    }

    @Test
    void getStudentsByAgeBetween_shouldReturnFilteredStudents() throws Exception {
        when(studentService.getStudentsByAgeBetween(16, 17)).thenReturn(List.of(student));

        mockMvc.perform(get("/students/age-between")
                        .param("min", "16")
                        .param("max", "17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].age").value(17));
    }

    @Test
    void getFacultyByStudentId_shouldReturnFaculty() throws Exception {
        when(studentService.getFacultyByStudentId(1L)).thenReturn(faculty);

        mockMvc.perform(get("/students/{id}/faculty", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    void getStudentById_whenNotFound_shouldReturn404() throws Exception {
        when(studentService.getStudent(999L)).thenReturn(null);

        mockMvc.perform(get("/students/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
