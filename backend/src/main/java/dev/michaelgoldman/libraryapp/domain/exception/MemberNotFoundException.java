package dev.michaelgoldman.libraryapp.domain.exception;

import dev.michaelgoldman.libraryapp.domain.model.MemberId;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(MemberId id) {
        super("Member " + id.value() + " not found");
    }
}
