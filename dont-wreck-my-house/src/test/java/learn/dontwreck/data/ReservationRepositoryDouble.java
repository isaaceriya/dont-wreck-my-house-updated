package learn.dontwreck.data;

import learn.dontwreck.models.Reservation;
import org.springframework.format.datetime.joda.LocalDateParser;
import org.springframework.format.datetime.joda.LocalDateTimeParser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationRepositoryDouble implements ReservationRepository{

    private final ArrayList<Reservation> reservations = new ArrayList<>();
    public ReservationRepositoryDouble() {
        Reservation reservation = new Reservation();
        reservation.setId(1);
        reservation.setStart_date( LocalDate.of(2018,06,6));
        reservation.setEnd_date(LocalDate.of(2018,06,8));
        reservation.setGuest_id(245);
        reservation.setTotal(new BigDecimal(480));
        reservations.add(reservation);
    }


    public List<Reservation> findAll() {
        return new ArrayList<>(reservations);
    }


    public Reservation findById(int id) {
        return findAll().stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Reservation add(Reservation reservation) throws DataAccessException {
        List<Reservation> all = findAll();

        int nextId = all.stream()
                .mapToInt(Reservation::getId)
                .max()
                .orElse(0) + 1;

        reservation.setId(nextId);

        all.add(reservation);
        return reservation;
    }

    @Override
    public List<Reservation> findByHost(String hostID) {
        return reservations.stream()
                .filter(i -> i.getHost().equals(hostID))
                .collect(Collectors.toList());
    }

    @Override
    public boolean update(Reservation reservation) throws DataAccessException {
        return true;
    }

    @Override
    public boolean deleteByReservation(String hostId, int id) throws DataAccessException {
        return true;
    }

}
