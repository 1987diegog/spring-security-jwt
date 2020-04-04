package com.demente.ideas.app.models.repository;

import com.demente.ideas.app.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// Al extender de JpaRepository, inmediatamente se transforma en una interface especial, la cual
// ya es un componente de Spring y sera tratada como tal, por lo tanto, no es necesario
// decorar la interface con @Repository, la misma podra ser inyectada en cualquier bean.
public interface IUserRepository extends JpaRepository<User, Long> {


    /**
     * Query generated dynamically using Spring and the reserved name findBy
     *
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * Query generated dynamically using Spring and the reserved name findBy
     *
     * @param name
     * @param lastname
     * @return
     */
    Optional<User> findByNameAndLastname(String name, String lastname);

    /**
     * Query generated dynamically using Spring Data Query Methods
     *
     * @return
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Query generated dynamically using Spring Data Query Methods
     *
     * @param username
     * @return
     */
    Boolean existsByUsername(String username);

    /**
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);

    /**
     * Spring Data @Query
     *
     * @param from
     * @param to
     * @return
     */
	@Query("SELECT u FROM User u WHERE u.createdAt >=:from AND u.createdAt <=:to")
    List<User> findByFilter(@Param("from") Date from, @Param("to") Date to);

}
