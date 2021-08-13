package learn.dontwreck.data;

import learn.dontwreck.models.Host;
import learn.dontwreck.models.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository {
        Reservation add(Reservation reservation) throws DataAccessException;

        List<Reservation> findByHost(String hostID);

        boolean update(Reservation reservation) throws DataAccessException;

        boolean deleteByReservation(String hostId, int id) throws DataAccessException;
}

