package dev.xgeorget.database.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.xgeorget.database.TestDataUtil;
import dev.xgeorget.database.domain.dto.AuthorDto;
import dev.xgeorget.database.domain.entities.AuthorEntity;
import dev.xgeorget.database.services.impl.AuhtorServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthorControllerIntegrationTests {

    private AuhtorServiceImpl authorService;

    private MockMvc           mockMvc;

    private ObjectMapper      objectMapper;

    @Autowired
    public AuthorControllerIntegrationTests(MockMvc mockMvc, AuhtorServiceImpl authorService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.authorService = authorService;
    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturnsHttp201Created() throws Exception {
        AuthorEntity testAuthorA = TestDataUtil.createTestAuthorA();
        testAuthorA.setId(null);
        String authorJson = objectMapper.writeValueAsString(testAuthorA);

        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(authorJson))
            .andExpect(MockMvcResultMatchers.status()
                .isCreated());
    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturnsSavedAuthor() throws Exception {
        AuthorEntity testAuthorA = TestDataUtil.createTestAuthorA();
        testAuthorA.setId(null);
        String authorJson = objectMapper.writeValueAsString(testAuthorA);

        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(authorJson))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                .isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                .value("Abigail Rose"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.age")
                .value(80));
    }

    @Test
    public void testThatListAuthorsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/authors")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status()
                .isOk());
    }

    @Test
    public void testThatListAuthorsReturnsListOfAuthors() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        authorService.save(author);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id")
                .isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name")
                .value("Abigail Rose"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].age")
                .value(80));
    }

    @Test
    public void testThatGetAuthorReturnsHttpStatus200WhenAuthorExists() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        authorService.save(author);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status()
                .isOk());
    }

    @Test
    public void testThatGetAuthorReturnsHttpStatus404WhenNoAuthorExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/authors/99")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status()
                .isNotFound());
    }

    @Test
    public void testThatGetAuthorReturnsAuthorWhenAuthorExists() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        authorService.save(author);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/1")
            .contentType(MediaType.APPLICATION_JSON)

        )
            .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                .value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                .value("Abigail Rose"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.age")
                .value(80));
    }

    @Test
    public void testThatFullUpdateAuthorReturnsStatus404WhenNoAuthorExists() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        authorService.save(author);
        String authorJson = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(authorJson))
            .andExpect(MockMvcResultMatchers.status()
                .isNotFound());
    }

    @Test
    public void testThatFullUpdateAuthorReturnsStatus200WhenAuthorExists() throws Exception {
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(testAuthorEntityA);

        AuthorDto testAuthorDtoA = TestDataUtil.createTestAuthorDtoA();
        String authorDtoJson = objectMapper.writeValueAsString(testAuthorDtoA);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + savedAuthor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(authorDtoJson))
            .andExpect(MockMvcResultMatchers.status()
                .isOk());
    }

    @Test
    public void testThatFullUpdateUpdatesExistingAuthor() throws Exception {
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(testAuthorEntityA);

        AuthorEntity authorDto = TestDataUtil.createTestAuthorB();
        authorDto.setId(savedAuthor.getId());
        String authorDtoUpdateJson = objectMapper.writeValueAsString(authorDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + savedAuthor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(authorDtoUpdateJson))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                .value(authorDto.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                .value(authorDto.getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.age")
                .value(authorDto.getAge()));
    }

    @Test
    public void testThatPartialUpdateAuthorReturnsStatus404WhenNoAuthorExists() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        authorService.save(author);
        String authorJson = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(authorJson))
            .andExpect(MockMvcResultMatchers.status()
                .isNotFound());
    }

    @Test
    public void testThatPartialUpdateAuthorReturnsStatus200WhenAuthorExists() throws Exception {
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(testAuthorEntityA);

        AuthorDto testAuthorDtoA = TestDataUtil.createTestAuthorDtoA();
        testAuthorDtoA.setName("Maria Santa Bella");
        String authorDtoJson = objectMapper.writeValueAsString(testAuthorDtoA);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/" + savedAuthor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(authorDtoJson))
            .andExpect(MockMvcResultMatchers.status()
                .isOk());
    }

    @Test
    public void testThatPartialUpdateReturnsUpdatedAuthor() throws Exception {
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(testAuthorEntityA);

        AuthorEntity authorDto = TestDataUtil.createTestAuthorB();
        authorDto.setName("Maria Santa Bella");
        authorDto.setId(null);
        authorDto.setAge(null);

        String authorDtoUpdateJson = objectMapper.writeValueAsString(authorDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/" + savedAuthor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(authorDtoUpdateJson))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                .value(savedAuthor.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                .value("Maria Santa Bella"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.age")
                .value(savedAuthor.getAge()));
    }

    @Test
    public void testThatDeleteAuthorReturnsHttpStatus204ForNonExistingAuthor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/99"))
            .andExpect(MockMvcResultMatchers.status()
                .isNoContent());
    }

    @Test
    public void testThatDeleteAuthorReturnsHttpStatus204ForExistingAuthor() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(author);

        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/" + savedAuthor.getId()))
            .andExpect(MockMvcResultMatchers.status()
                .isNoContent());
    }

}
