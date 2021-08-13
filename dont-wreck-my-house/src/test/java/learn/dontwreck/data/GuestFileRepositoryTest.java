package learn.dontwreck.data;

import learn.dontwreck.models.Guest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GuestFileRepositoryTest {
    static final String SEED_PATH = "./data/guests-seed.csv";
    static final String TEST_PATH = "./data/guests-test.csv";
    GuestFileRepository repository = new GuestFileRepository(TEST_PATH);

    @BeforeEach
    void setup() throws IOException {
        Path seedPath = Paths.get(SEED_PATH);
        Path testPath = Paths.get(TEST_PATH);
        Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void shouldFindByEmail() {
        String email = "slomas0@mediafire.com";
        Guest guests = repository.findByEmail(email);
//        assertEquals(1, guests.size());
    }

   /* @Test
    void shouldFindById() {
        int guest = 5;
        Guest guests = repository.findById(guest);
        assertEquals(1, Gues);
    }*/

}