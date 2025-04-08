package com.code_space.code_space_editor.auth.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.code_space.code_space_editor.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

  @Query(value = """
      select t from RefreshToken t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
  List<RefreshToken> findAllValidTokenByUser(Integer id);

  Optional<RefreshToken> findByToken(String token);
}
