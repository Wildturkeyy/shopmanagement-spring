package com.shopsProject.management.jpaTest;

import com.shopsProject.management.domain.Member;
import com.shopsProject.management.Repository.MemberRespository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRespositoryTest {

    @Autowired
    MemberRespository memberRespository;

    @Test
    @DisplayName("Member 엔티티 저장 후, 조회가 잘 되는 지 테스트")
    void saveAndFindTest() {
        Member member = Member.builder()
            .username("tester")
            .email("test@test.com")
            .build();
        memberRespository.save(member);

        Member mem = memberRespository.findByUsername("tester");

        assertThat(mem).isNotNull();
        assertThat(mem.getEmail()).isEqualTo("test@test.com");
    }
}