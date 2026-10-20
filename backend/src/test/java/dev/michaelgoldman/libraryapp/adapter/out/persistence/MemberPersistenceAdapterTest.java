package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import dev.michaelgoldman.libraryapp.TestcontainersConfiguration;
import dev.michaelgoldman.libraryapp.domain.model.MemberId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({TestcontainersConfiguration.class, MemberPersistenceAdapter.class})
class MemberPersistenceAdapterTest {

    @Autowired
    private MemberPersistenceAdapter adapter;

    @Test
    void exists_whenMemberIsSeeded_returnTrue() {
        assertThat(adapter.exists(new MemberId(1))).isTrue();
    }

    @Test
    void exists_whenMemberIsUnknown_returnFalse() {
        assertThat(adapter.exists(new MemberId(999))).isFalse();
    }
}
