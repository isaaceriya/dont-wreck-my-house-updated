package learn.dontwreck.data;

import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;
import learn.dontwreck.models.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationFileRepository implements ReservationRepository{
    private static final String HEADER = "id,start_date,end_date,guest_id,total";
    private final String filePath;

    public ReservationFileRepository (@Value("${reservationFilePath}") String filePath) { this.filePath = filePath; }

    private String getFilePath(String hostId) {
        return Paths.get(filePath, hostId + ".csv").toString();
    }


    public List<Reservation> findByHost(String hostId) {
        ArrayList<Reservation> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath(hostId)))) {

            reader.readLine(); // read header

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                String[] fields = line.split(",", -1);
                if (fields.length == 5) {
                    result.add(deserialize(fields, hostId));
                }
            }
        } catch (IOException ex) {
            // don't throw on read
        }
        return result;
    }

    /*public Reservation findByGuest(String hostId, int guest_id) {
        return findByHost(hostId).stream()
                .filter(i -> i.getId() == (guest_id))
                .findFirst()
                .orElse(null);
    }*/


    public Reservation add(Reservation reservation) throws DataAccessException {
        List<Reservation> all = findByHost(reservation.getHost().getId());

        int nextId = 0;
        for (Reservation r : all) {
            nextId = Math.max(nextId, r.getId());
        }

        reservation.setId(nextId + 1);
        all.add(reservation);

        writeAll(all, reservation.getHost().getId());

        return reservation;
    }

    public boolean update(Reservation reservation) throws DataAccessException {
        List<Reservation> all = findByHost(reservation.getHost().getId());
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == reservation.getId()) {
                all.set(i, reservation);
                writeAll(all, reservation.getHost().getId());
                return true;
            }
        }
        return false;
    }


    public boolean deleteByReservation(String hostId, int id) throws DataAccessException {
        List<Reservation> all = findByHost(hostId);
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == id) {
                all.remove(i);
                writeAll(all, hostId);
                return true;
            }
        }

        return false;
    }

    private Reservation deserialize(String[] fields, String hostId) {
        Reservation result = new Reservation();
        result.setId(Integer.parseInt(fields[0]));
        result.setStart_date(LocalDate.parse(fields[1]));
        result.setEnd_date(LocalDate.parse(fields[2]));

        Guest guest = new Guest();
        guest.setGuest_id(Integer.parseInt(fields[3]));
        result.setGuest(guest);

        result.setTotal(new BigDecimal(fields[4]));

        Host host = new Host();
        host.setId(hostId);
        result.setHost(host);
        return result;
    }

    private String serialize(Reservation reservation) {
        return String.format("%s,%s,%s,%s,%s",
                reservation.getId(),
                reservation.getStart_date(),
                reservation.getEnd_date(),
                reservation.getGuest().getGuest_id(),
                reservation.getTotal());
    }

//    id,start_date,end_date,guest_id,total"




    protected void writeAll(List<Reservation> reservations, String hostId) throws DataAccessException {
        try (PrintWriter writer = new PrintWriter(getFilePath(hostId))) {

            writer.println(HEADER);

            if (reservations == null) {
                return;
            }

            for (Reservation reservation : reservations) {
                writer.println(serialize(reservation));
            }

        } catch (FileNotFoundException ex) {
            throw new DataAccessException(ex);
        }
    }


}
