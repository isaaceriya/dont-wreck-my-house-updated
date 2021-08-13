package learn.dontwreck.data;

import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;
import learn.dontwreck.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationFileRepositoryTest {
    static final String SEED_FILE_PATH = "./data/2e72f86c-b8fe-4265-b4f1-304dea8762db-seed.csv";
    static final String TEST_FILE_PATH = "./data/reservations_data_test/2e72f86c-b8fe-4265-b4f1-304dea8762db.csv";
    static final String TEST_DIR_PATH = "./data/reservations_data_test";
    static final String hostId = "2e72f86c-b8fe-4265-b4f1-304dea8762db";
    static final int NEXT_ID = 13;


    ReservationFileRepository repository = new ReservationFileRepository(TEST_DIR_PATH);

    @BeforeEach
    void setup() throws IOException {
        Path seedPath = Paths.get(SEED_FILE_PATH);
        Path testPath = Paths.get(TEST_FILE_PATH);
        Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void shouldFindAll() {
        List<Reservation> all = repository.findByHost(hostId);
        assertEquals(12, all.size());
    }


    @Test
    void shouldAdd() throws DataAccessException {
        Reservation expected = new Reservation();
        Host host = new Host();
        host.setId(hostId);
        expected.setStart_date(LocalDate.of(2018,06,6));
        expected.setEnd_date(LocalDate.of(2018,06,8));
        expected.setGuest_id(982);
        expected.setTotal(new BigDecimal(480));
        expected.setHost(host);
        expected.setId(NEXT_ID);

        Reservation reservation = new Reservation();
        reservation.setStart_date(LocalDate.of(2018,06,6));
        reservation.setEnd_date(LocalDate.of(2018,06,8));
        reservation.setGuest_id(982);
        reservation.setTotal(new BigDecimal(480));
        reservation.setHost(host);

        Reservation actual = repository.add(reservation);
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeleteExisting() throws DataAccessException {
        assertTrue(repository.deleteByReservation(hostId, 4));
    }

    @Test
    void shouldNotDeleteMissing() throws DataAccessException {
        assertFalse(repository.deleteByReservation(hostId, 92833));
    }

    @Test
    void shouldUpdateExisting() throws DataAccessException {
        Reservation reservation = makeReservation();
        reservation.setId(1);
        assertTrue(repository.update(reservation));
    }

    @Test
    void shouldNotUpdateMissing() throws DataAccessException {
        Reservation reservation = makeReservation();
        reservation.setId(1234);
        assertFalse(repository.update(reservation));
    }


    @Test
    void shouldUpdatesExisting() throws DataAccessException {
        Reservation reservation = makeReservation();
        reservation.setId(1);
        assertTrue(repository.update(reservation));
    }

    private Reservation makeReservation() {
        Reservation reservation = new Reservation();
        Host host = new Host();
        host.setId(hostId);
        Guest guest = new Guest();
        reservation.setStart_date(LocalDate.of(2025,06,6));
        reservation.setEnd_date(LocalDate.of(2026,06,8));
        reservation.setGuest_id(982);
        reservation.setTotal(new BigDecimal(480));
        reservation.setHost(host);
        reservation.setGuest(guest);
        return reservation;
    }

}