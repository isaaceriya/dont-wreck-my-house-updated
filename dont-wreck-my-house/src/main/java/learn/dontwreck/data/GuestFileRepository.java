package learn.dontwreck.data;

import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GuestFileRepository implements GuestRepository {
    private static final String HEADER = "guest_id,first_name,last_name,email,phone,state";
    private final String filePath;

    public GuestFileRepository(@Value("${guestsFilePath}") String filePath) {
        this.filePath = filePath;
    }

    public List<Guest> findAll() {
        ArrayList<Guest> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            reader.readLine(); // read header

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                String[] fields = line.split(",", -1);
                if (fields.length == 6) {
                    result.add(deserialize(fields));
                }
            }
        } catch (IOException ex) {
            // don't throw on read
        }
        return result;
    }




    public Guest findByEmail(String email){
        return findAll().stream()
                .filter(i -> i.getEmail().equalsIgnoreCase(email)).findFirst()
                .orElse(null);
    }

    private Guest deserialize(String[] fields) {
        Guest result = new Guest();
        result.setGuest_id(Integer.parseInt(fields[0]));
        result.setFirst_name(fields[1]);
        result.setLast_name(fields[2]);
        result.setEmail(fields[3]);
        result.setPhone(fields[4]);
        result.setState(fields[5]);
        return result;
    }
}


        /*public Guest findById(int guest){
        return findAll().stream()
                .filter(i -> i.getGuest_id() == guest)
                .findFirst()
                .orElse(null);
    }*/

   /* public Host add(Host host) throws DataAccessException {
        List<Host> all = findByEmail(host.getEmail());
        host.setId(java.util.UUID.randomUUID().toString());
        all.add(host);
        writeAll(all, host.getEmail());
        return host;
    }

    public boolean update(Host host) throws DataAccessException {
        List<Host> all = findByEmail(host.getEmail());
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(host.getId())) {
                all.set(i, host);
                writeAll(all, host.getEmail());
                return true;
            }
        }
        return false;
    }*/


        /*ArrayList<Host> result = new ArrayList<>();
        for (Host host : findAll()) {
            if (host.getEmail().equalsIgnoreCase(email)) {
                result.add(host);
            }
        }
        return result;
    }*/




    /*private String serialize(Guest guest) {
        return String.format("%s,%s,%s,%s,%s",
                guest.getGuest_id(),
                guest.getLast_name(),
                guest.getEmail(),
                guest.getPhone(),
                guest.getAddress());
    }*/


    /*protected void writeAll(List<Host> hosts, String email) throws DataAccessException {
        try (PrintWriter writer = new PrintWriter(filePath)) {

            writer.println(HEADER);

            if (hosts == null) {
                return;
            }

            for (Host host : hosts) {
                writer.println(serialize(host));
            }

        } catch (FileNotFoundException ex) {
            throw new DataAccessException(ex);
        }
    }*/

