package dev.xgeorget.database.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.xgeorget.database.TestDataUtil;
import dev.xgeorget.database.domain.entities.AuthorEntity;
import dev.xgeorget.database.domain.entities.BookEntity;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookRepositoryIntegrationTests {
    private BookRepository bookRepository;

    @Autowired
    public BookRepositoryIntegrationTests(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    public void testThatBookCanBeCreatedAndRecalled() {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        BookEntity book = TestDataUtil.createTestBookEntityA(author);
        bookRepository.save(book);
        Optional<BookEntity> result = bookRepository.findById(book.getIsbn());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison()
            .isEqualTo(book);
    }

    @Test
    public void testThatMultipleBooksCanBeCreatedAndRecalled() {
        AuthorEntity author = TestDataUtil.createTestAuthorA();

        BookEntity bookA = TestDataUtil.createTestBookEntityA(author);
        bookRepository.save(bookA);
        BookEntity bookB = TestDataUtil.createTestBookB(author);
        bookRepository.save(bookB);
        BookEntity bookC = TestDataUtil.createTestBookC(author);
        bookRepository.save(bookC);

        Iterable<BookEntity> result = bookRepository.findAll();
        assertThat(result).hasSize(3)
            .extracting("isbn", "title")
            .contains(tuple(bookA.getIsbn(), bookA.getTitle()), tuple(bookB.getIsbn(), bookB.getTitle()),
                tuple(bookC.getIsbn(), bookC.getTitle()));

        assertThat(result).map(a -> a.getAuthor())
            .extracting("id")
            .contains(author.getId(), author.getId(), author.getId());
    }

    @Test
    public void testThatBookCanBeUpdated() {
        AuthorEntity authorA = TestDataUtil.createTestAuthorA();
        BookEntity bookA = TestDataUtil.createTestBookEntityA(authorA);
        bookRepository.save(bookA);

        bookA.setTitle("UPDATED");
        bookRepository.save(bookA);

        Optional<BookEntity> result = bookRepository.findById(bookA.getIsbn());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison()
            .isEqualTo(bookA);
    }

    @Test
    public void testThatBookCanBeDeleted() {
        AuthorEntity authorA = TestDataUtil.createTestAuthorA();
        BookEntity bookA = TestDataUtil.createTestBookEntityA(authorA);
        bookRepository.save(bookA);

        bookRepository.deleteById(bookA.getIsbn());
        Optional<BookEntity> result = bookRepository.findById(bookA.getIsbn());
        assertThat(result).isEmpty();
    }

}
