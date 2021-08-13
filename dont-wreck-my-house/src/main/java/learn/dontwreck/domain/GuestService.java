package learn.dontwreck.domain;

import learn.dontwreck.data.GuestRepository;
import learn.dontwreck.data.HostRepository;
import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService {
    private final GuestRepository repository;

    public GuestService(GuestRepository repository) {
        this.repository = repository;
    }

    public Guest findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
