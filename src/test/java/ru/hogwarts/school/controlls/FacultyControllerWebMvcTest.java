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
import ru.hogwarts.school.service.FacultyService;

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

@WebMvcTest(FacultyController.class)
@ActiveProfiles("test")
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    // Spring Boot 4.1.1: @MockBean удалён; @MockitoBean — официальная замена.
    @MockitoBean
    private FacultyService facultyService;

    private Faculty faculty;
    private Student student;

    @BeforeEach
    void setUp() {
        faculty = new Faculty(1L, "Gryffindor", "red");
        student = new Student(1L, "Harry", 17);
        student.setFaculty(faculty);
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() throws Exception {
        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Gryffindor","color":"red"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    void getFacultyById_shouldReturnFaculty() throws Exception {
        when(facultyService.getFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

    @Test
    void getAllFaculties_shouldReturnFaculties() throws Exception {
        Faculty second = new Faculty(2L, "Slytherin", "green");
        when(facultyService.getAllFaculties()).thenReturn(List.of(faculty, second));

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() throws Exception {
        Faculty updated = new Faculty(1L, "Updated", "blue");
        when(facultyService.updateFaculty(any(Faculty.class))).thenReturn(updated);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":1,"name":"Updated","color":"blue"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.color").value("blue"));
    }

    @Test
    void deleteFaculty_shouldReturnOk() throws Exception {
        doNothing().when(facultyService).deleteFaculty(1L);

        mockMvc.perform(delete("/faculty/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(facultyService).deleteFaculty(1L);
    }

    @Test
    void searchFaculties_shouldReturnMatches() throws Exception {
        when(facultyService.findFacultiesByNameOrColor("Test")).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty/search")
                        .param("search", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"));
    }

    @Test
    void getStudentsByFacultyId_shouldReturnStudents() throws Exception {
        when(facultyService.getFaculty(1L)).thenReturn(faculty);
        when(facultyService.getStudentsByFacultyId(1L)).thenReturn(List.of(student));

        mockMvc.perform(get("/faculty/{id}/students", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getFacultyById_whenNotFound_shouldReturn404() throws Exception {
        when(facultyService.getFaculty(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
