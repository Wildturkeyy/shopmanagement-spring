package com.shopsProject.management.Repository;

import com.shopsProject.management.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRespository extends JpaRepository<Member, Long> {
    Member findByUsername(String username);
}
