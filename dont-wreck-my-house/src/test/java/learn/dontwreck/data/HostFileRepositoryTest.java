package learn.dontwreck.data;

import learn.dontwreck.models.Host;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HostFileRepositoryTest {
    static final String SEED_PATH = "./data/hosts-seed.csv";
    static final String TEST_PATH = "./data/hosts-test.csv";
    HostFileRepository repository = new HostFileRepository(TEST_PATH);

    @BeforeEach
    void setup() throws IOException {
        Path seedPath = Paths.get(SEED_PATH);
        Path testPath = Paths.get(TEST_PATH);
        Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void shouldFindByEmail() {
        String email = "eyearnes0@sfgate.com";
        Host hosts = repository.findByEmail(email);
        assertEquals("3edda6bc-ab95-49a8-8962-d50b53f84b15", hosts.getId());
    }

    @Test
    void shouldFindById() {
        String host = "b6ddb844-b990-471a-8c0a-519d0777eb9b";
        List<Host> hosts = repository.findById(host);
        assertEquals(1, hosts.size());
    }



}