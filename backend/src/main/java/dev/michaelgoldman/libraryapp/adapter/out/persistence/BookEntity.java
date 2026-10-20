package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "book")
class BookEntity {

    @Id
    private Long id;

    protected BookEntity() {}
}
