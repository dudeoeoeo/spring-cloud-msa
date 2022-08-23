package com.example.userservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository , CrudRepository 차이점
 *
 * 간단한 조회 수정 갱신등의 처리를 위해서는 CrudRepository 를 사용해도 된다.
 * 하지만 페이징 처리같은 좀 더 복잡한 기능을 위해서는 JpaRepository 를 사용하는게 더 편리하다.
 *
 * 또한 JpaRepository 의 findAll() 메소드는 List 를 반환하지만
 * CrudRepository 의 findAll() 메소드는 Iterable 을 반환한다.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUserId(String userId);
    UserEntity findByEmail(String email);
}
