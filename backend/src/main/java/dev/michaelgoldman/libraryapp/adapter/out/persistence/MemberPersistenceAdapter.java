package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import dev.michaelgoldman.libraryapp.application.port.out.MemberRepository;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import org.springframework.stereotype.Component;

@Component
class MemberPersistenceAdapter implements MemberRepository {

    private final MemberJpaRepository jpa;

    MemberPersistenceAdapter(MemberJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean exists(MemberId memberId) {
        return jpa.existsById(memberId.value());
    }
}
