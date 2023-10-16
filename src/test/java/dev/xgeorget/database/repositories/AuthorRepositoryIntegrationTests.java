package dev.xgeorget.database.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.xgeorget.database.TestDataUtil;
import dev.xgeorget.database.domain.entities.AuthorEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorRepositoryIntegrationTests {

    private AuthorRepository authorRepository;

    @Autowired
    public AuthorRepositoryIntegrationTests(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Test
    public void testThatAuthorCanBeCreatedAndRecalled() {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        authorRepository.save(author);
        Optional<AuthorEntity> result = authorRepository.findById(author.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison()
            .isEqualTo(author);
    }

    @Test
    public void testThatMultipleAuthorsCanBeCreatedAndRecalled() {
        AuthorEntity authorA = TestDataUtil.createTestAuthorA();
        authorRepository.save(authorA);
        AuthorEntity authorB = TestDataUtil.createTestAuthorB();
        authorRepository.save(authorB);
        AuthorEntity authorC = TestDataUtil.createTestAuthorC();
        authorRepository.save(authorC);

        Iterable<AuthorEntity> result = authorRepository.findAll();
        assertThat(result).hasSize(3)
            .extracting("id", "name", "age")
            .contains(tuple(authorA.getId(), authorA.getName(), authorA.getAge()),
                tuple(authorB.getId(), authorB.getName(), authorB.getAge()),
                tuple(authorC.getId(), authorC.getName(), authorC.getAge()));
    }

    @Test
    public void testThatAuthorCanBeUpdated() {
        AuthorEntity authorA = TestDataUtil.createTestAuthorA();
        authorRepository.save(authorA);

        authorA.setName("UPDATED");
        authorRepository.save(authorA);
        Optional<AuthorEntity> result = authorRepository.findById(authorA.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison()
            .isEqualTo(authorA);

    }

    @Test
    public void testThatAuthorCanBeDeleted() {
        AuthorEntity authorA = TestDataUtil.createTestAuthorA();
        authorRepository.save(authorA);

        authorRepository.deleteById(authorA.getId());

        Optional<AuthorEntity> result = authorRepository.findById(authorA.getId());
        assertThat(result).isEmpty();
    }

    // @Test
    // public void testThatGetAuthorsWithAgeLessThan() {
    // AuthorEntity authorA = TestDataUtil.createTestAuthorA();
    // authorRepository.save(authorA);
    // AuthorEntity authorB = TestDataUtil.createTestAuthorB();
    // authorRepository.save(authorB);
    // AuthorEntity authorC = TestDataUtil.createTestAuthorC();
    // authorRepository.save(authorC);

    // Iterable<AuthorEntity> result = authorRepository.ageLessThan(50);

    // assertThat(result)
    // .hasSize(2)
    // .extracting("id", "name", "age")
    // .containsExactly(
    // tuple(authorB.getId(), authorB.getName(), authorB.getAge()),
    // tuple(authorC.getId(), authorC.getName(), authorC.getAge()));
    // }

    // @Test
    // public void testThatGetAuthorsWithAgeGreaterThan() {
    // AuthorEntity authorA = TestDataUtil.createTestAuthorA();
    // authorRepository.save(authorA);
    // AuthorEntity authorB = TestDataUtil.createTestAuthorB();
    // authorRepository.save(authorB);
    // AuthorEntity authorC = TestDataUtil.createTestAuthorC();
    // authorRepository.save(authorC);

    // Iterable<AuthorEntity> result =
    // authorRepository.findAuthorsWithAgeGreaterThan(50);

    // assertThat(result)
    // .hasSize(1)
    // .extracting("id", "name", "age")
    // .containsExactly(
    // tuple(authorA.getId(), authorA.getName(), authorA.getAge()));

    // }
}
