package dev.xgeorget.database.controllers;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.xgeorget.database.domain.dto.BookDto;
import dev.xgeorget.database.domain.entities.BookEntity;
import dev.xgeorget.database.mappers.Mapper;
import dev.xgeorget.database.services.impl.BookServiceImpl;

@RestController
public class BookController {
    private Mapper<BookEntity, BookDto> bookMapper;

    private BookServiceImpl             bookService;

    public BookController(Mapper<BookEntity, BookDto> bookMapper, BookServiceImpl bookService) {
        this.bookMapper = bookMapper;
        this.bookService = bookService;
    }

    @PutMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> createUpdateBook(@RequestBody BookDto bookDto, @PathVariable("isbn") String isbn) {
        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        boolean bookExists = bookService.isExists(isbn);

        BookEntity savedBookEntity = bookService.createUpdateBook(isbn, bookEntity);
        BookDto savedUpdateBookDto = bookMapper.mapTo(savedBookEntity);

        if (bookExists) {
            return new ResponseEntity<>(savedUpdateBookDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(savedUpdateBookDto, HttpStatus.CREATED);
        }
    }

    @GetMapping(path = "/books")
    public Page<BookDto> listBooks(Pageable pageable) {
        Page<BookEntity> books = bookService.findAll(pageable);
        return books.map(bookMapper::mapTo);
    }

    @GetMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> getBook(@PathVariable("isbn") String isbn) {
        Optional<BookEntity> foundBook = bookService.findByIsbn(isbn);

        return foundBook.map(bookEntity -> {
            BookDto bookDto = bookMapper.mapTo(bookEntity);
            return new ResponseEntity<>(bookDto, HttpStatus.OK);
        })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/books/{isbn}")
    public ResponseEntity<BookDto> partialUpdate(@RequestBody BookDto bookDto, @PathVariable("isbn") String isbn) {

        if (!bookService.isExists(isbn)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        BookEntity updatedBookEntity = bookService.partialUpdate(isbn, bookEntity);
        return new ResponseEntity<>(bookMapper.mapTo(updatedBookEntity), HttpStatus.OK);

    }

    @DeleteMapping(path = "/books/{isbn}")
    public ResponseEntity deleteBook(@PathVariable("isbn") String isbn) {
        bookService.delete(isbn);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
