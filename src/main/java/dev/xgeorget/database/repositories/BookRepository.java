package dev.xgeorget.database.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import dev.xgeorget.database.domain.entities.BookEntity;

@Repository
public interface BookRepository
    extends CrudRepository<BookEntity, String>, PagingAndSortingRepository<BookEntity, String> {

}
