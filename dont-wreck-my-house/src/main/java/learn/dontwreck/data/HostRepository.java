package learn.dontwreck.data;

import learn.dontwreck.models.Host;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostRepository {

    Host findByEmail(String email);

    List<Host> findAll();


}
