package sec.project.repository;

import java.util.List;
import sec.project.domain.ImageObject;
import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Account;

public interface ImageRepository extends JpaRepository<ImageObject, Long> {

    List<ImageObject> findByAccount(Account account);

}
