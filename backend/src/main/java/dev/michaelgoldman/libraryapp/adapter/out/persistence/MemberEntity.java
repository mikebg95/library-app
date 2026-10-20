package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
class MemberEntity {

    @Id
    private Long id;

    protected MemberEntity() {}
}
