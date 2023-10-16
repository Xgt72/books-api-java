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
import dev.xgeorget.database.domain.dto.BookDto;
import dev.xgeorget.database.domain.entities.BookEntity;
import dev.xgeorget.database.services.impl.BookServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BookControllerIntegrationTests {
    private BookServiceImpl bookService;
    private MockMvc         mockMvc;

    private ObjectMapper    objectMapper;

    @Autowired
    public BookControllerIntegrationTests(MockMvc mockMvc, BookServiceImpl bookService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.bookService = bookService;
    }

    @Test
    public void testThatCreateBookReturnsHttpStatus201Created() throws Exception {
        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/books/" + bookDto.getIsbn())
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBookJson))
            .andExpect(MockMvcResultMatchers.status()
                .isCreated());
    }

    @Test
    public void testThatUpdateBookReturnsHttpStatus200Ok() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBookEntityA(null);

        BookEntity savedBookEntity = bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);

        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        bookDto.setIsbn(savedBookEntity.getIsbn());
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/books/" + bookDto.getIsbn())
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBookJson))
            .andExpect(MockMvcResultMatchers.status()
                .isOk());
    }

    @Test
    public void testThatUpdateBookReturnsUpdatedBook() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBookEntityA(null);

        BookEntity savedBookEntity = bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);

        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        bookDto.setIsbn(savedBookEntity.getIsbn());
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/books/" + bookDto.getIsbn())
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBookJson))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isbn")
                .value(bookDto.getIsbn()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value(bookDto.getTitle()));
    }

    @Test
    public void testThatCreateBookReturnsSavedBook() throws Exception {
        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/books/" + bookDto.getIsbn())
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBookJson))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isbn")
                .value(bookDto.getIsbn()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value(bookDto.getTitle()));
    }

    @Test
    public void testThatListBooksReturnsHttpStatus200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/books")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status()
                .isOk());
    }

    @Test
    public void testThatListBooksReturnsListOfBooks() throws Exception {
        BookEntity book = TestDataUtil.createTestBookEntityA(null);
        bookService.createUpdateBook(book.getIsbn(), book);

        mockMvc.perform(MockMvcRequestBuilders.get("/books")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].isbn")
                .value(book.getIsbn()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title")
                .value(book.getTitle()));
    }

    @Test
    public void testThatGetBookReturnsHttpStatus200WhenBookExists() throws Exception {
        BookEntity book = TestDataUtil.createTestBookEntityA(null);
        bookService.createUpdateBook(book.getIsbn(), book);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + book.getIsbn())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status()
                .isOk());
    }

    @Test
    public void testThatGetBookReturnsHttpStatus404WhenNoBookExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/books/878-99-3456-7")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status()
                .isNotFound());
    }

    @Test
    public void testThatGetBookReturnsBookWhenBookExists() throws Exception {
        BookEntity book = TestDataUtil.createTestBookEntityA(null);
        bookService.createUpdateBook(book.getIsbn(), book);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + book.getIsbn())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isbn")
                .value(book.getIsbn()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value(book.getTitle()));
    }

    @Test
    public void testThatPartialUpdateReturnsHttpStatus404WhenNoBookExists() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBookEntityA(null);

        BookEntity savedBookEntity = bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);

        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        bookDto.setIsbn(null);
        bookDto.setTitle("The Fellowship of the Ring");
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/945-45-4567-7")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBookJson))
            .andExpect(MockMvcResultMatchers.status()
                .isNotFound());
    }

    @Test
    public void testThatPartialUpdateReturnsHttpStatus200WhenBookExists() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBookEntityA(null);

        BookEntity savedBookEntity = bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);

        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        bookDto.setIsbn(null);
        bookDto.setTitle("The Fellowship of the Ring");
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + savedBookEntity.getIsbn())
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBookJson))
            .andExpect(MockMvcResultMatchers.status()
                .isOk());
    }

    @Test
    public void testThatPartialUpdateReturnsUpdatedBook() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBookEntityA(null);

        BookEntity savedBookEntity = bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);

        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        bookDto.setIsbn(null);
        bookDto.setTitle("The Fellowship of the Ring");
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + savedBookEntity.getIsbn())
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBookJson))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isbn")
                .value(savedBookEntity.getIsbn()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value(bookDto.getTitle()));
    }

    @Test
    public void testThatDeleteBookReturnsHttpStatus204ForNonExistingBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/045-45-5678-5"))
            .andExpect(MockMvcResultMatchers.status()
                .isNoContent());
    }

    @Test
    public void testThatDeleteBookReturnsHttpStatus204ForExistingBook() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBookEntityA(null);
        BookEntity savedBookEntity = bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + savedBookEntity.getIsbn()))
            .andExpect(MockMvcResultMatchers.status()
                .isNoContent());
    }

}
