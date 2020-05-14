package ralmnsk.video.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ralmnsk.video.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.login = :login")
    User getByLogin(@Param("login") String login);

    @Query("select u from User u where upper(u.login) like :find order by u.login asc")
    List<User> search(@Param("find") String find); //find has to include '%' at the end of string
}
