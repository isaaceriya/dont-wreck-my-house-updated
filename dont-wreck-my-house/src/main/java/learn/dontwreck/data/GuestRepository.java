package learn.dontwreck.data;

import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository {
    Guest findByEmail(String email);

    List<Guest> findAll();
}
