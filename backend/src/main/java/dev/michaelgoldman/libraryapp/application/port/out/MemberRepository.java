package dev.michaelgoldman.libraryapp.application.port.out;

import dev.michaelgoldman.libraryapp.domain.model.MemberId;

public interface MemberRepository {

    boolean exists(MemberId memberId);
}
