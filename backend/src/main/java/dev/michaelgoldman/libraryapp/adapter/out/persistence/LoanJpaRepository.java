package dev.michaelgoldman.libraryapp.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface LoanJpaRepository extends JpaRepository<LoanEntity, UUID> {

    List<LoanEntity> findByMemberIdAndReturnedAtIsNull(long memberId);

    boolean existsByBookIdAndReturnedAtIsNull(long bookId);
}
