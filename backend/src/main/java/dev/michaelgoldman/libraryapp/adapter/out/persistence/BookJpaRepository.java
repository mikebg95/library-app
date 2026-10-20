package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface BookJpaRepository extends JpaRepository<BookEntity, Long> {}
