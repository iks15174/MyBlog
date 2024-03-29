package com.jiho.board.springbootaws.domain.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByName(String name);

    Optional<Category> findByName(String name);

    @Query("SELECT distinct c " +
            "FROM Category c left outer join fetch c.subCategories sc " +
            "WHERE c.id = :id")
    Optional<Category> findByIdWithSubCt(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete from Category c where c.id = :id")
    void deleteInQuery(@Param("id") Long id);

    @Query("SELECT c, count(p) " +
            "FROM Category c left outer join Posts p on p.category = c " +
            "group by c ")
    List<Object[]> findAllWithPostCnt();

    Optional<Category> findByIsParent(boolean b);

    @Query("SELECT c " +
            "FROM Category c " +
            "WHERE c.isParent = FALSE")
    List<Category> findAllSubCategory();
}
