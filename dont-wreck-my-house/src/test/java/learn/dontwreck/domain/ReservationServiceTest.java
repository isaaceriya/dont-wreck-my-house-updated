package learn.dontwreck.domain;

import learn.dontwreck.data.DataAccessException;
import learn.dontwreck.data.GuestRepositoryDouble;
import learn.dontwreck.data.HostRepositoryDouble;
import learn.dontwreck.data.ReservationRepositoryDouble;
import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;
import learn.dontwreck.models.Reservation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    ReservationService service = new ReservationService(
            new ReservationRepositoryDouble(),
            new HostRepositoryDouble(),
            new GuestRepositoryDouble());

    static final String hostId = "498604db-b6d6-4599-a503-3d8190fda823";
    static final int NEXT_ID = 13;

    @Test
    void shouldAdd() throws DataAccessException {
        Reservation reservation = makeReservation();

        Result<Reservation> result = service.add(reservation);
        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
    }

   /* @Test
    void shouldNotAddWhenForagerNotFound() throws DataException {

        Forager forager = new Forager();
        forager.setId("30816379-188d-4552-913f-9a48405e8c08");
        forager.setFirstName("Ermengarde");
        forager.setLastName("Sansom");
        forager.setState("NM");

        Forage forage = new Forage();
        forage.setDate(LocalDate.now());
        forage.setForager(forager);
        forage.setItem(ItemRepositoryDouble.ITEM);
        forage.setKilograms(0.5);

        Result<Forage> result = service.add(forage);
        assertFalse(result.isSuccess());
    }*/

    @Test
    void shouldNotAddWhenItemNotFound() throws DataAccessException {
//guest_id,first_name,last_name,email,phone,state
//        Guest guest = new Guest(11, "Dandelion","Jssss","dhdhdh@gmail.com","07450697843","TX");

        Reservation reservation = new Reservation();
        reservation.setStart_date(LocalDate.of(2018,06,6));
        reservation.setEnd_date(LocalDate.of(2018,06,8));
        reservation.setGuest_id(982);
        reservation.setTotal(new BigDecimal(480));

        Result<Reservation> result = service.add(reservation);
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldDelete() throws DataAccessException {
        String host = HostRepositoryDouble.HOST.getId();
        Result<Reservation> actual = service.deleteReservation(host,1);
        assertTrue(actual.isSuccess());

    }

    @Test
    void shouldUpdate() throws DataAccessException {
        Reservation r = makeReservation();
        r.setId(1);
        r.setStart_date(LocalDate.of(2024,06,6));
        r.setEnd_date(LocalDate.of(2024,06,8));
        Result<Reservation> actual = service.update(r);
        assertTrue(actual.isSuccess());
    }

    private Reservation makeReservation() {
        Reservation reservation = new Reservation();
        reservation.setStart_date(LocalDate.of(2023,06,6));
        reservation.setEnd_date(LocalDate.of(2023,06,8));
        reservation.setGuest_id(245);
        reservation.setGuest(GuestRepositoryDouble.GUEST);
        reservation.setTotal(new BigDecimal(480));
        reservation.setHost(HostRepositoryDouble.HOST);
        return reservation;
    }


}