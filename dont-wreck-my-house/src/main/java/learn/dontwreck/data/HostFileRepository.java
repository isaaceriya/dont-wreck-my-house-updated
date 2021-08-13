package learn.dontwreck.data;

import learn.dontwreck.models.Host;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class HostFileRepository implements HostRepository {
    private static final String HEADER = "id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate";
    private final String filePath;

    public HostFileRepository(@Value("${hostsFilePath}") String filePath) {
        this.filePath = filePath;
    }

    public List<Host> findAll() {
        ArrayList<Host> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            reader.readLine(); // read header

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                String[] fields = line.split(",", -1);
                if (fields.length == 10) {
                    result.add(deserialize(fields));
                }
            }
        } catch (IOException ex) {
            // don't throw on read
        }
        return result;
    }
/*
    public Host add(Host host) throws DataAccessException {
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

    public List<Host> findById(String host){
        return findAll().stream()
                .filter(i -> i.getId().equalsIgnoreCase(host))
                .collect(Collectors.toList());
    }


    public Host findByEmail(String email){
        return findAll().stream()
                .filter(i -> i.getEmail().equalsIgnoreCase(email)).findFirst()
                        .orElse(null);
    }
        /*ArrayList<Host> result = new ArrayList<>();
        for (Host host : findAll()) {
            if (host.getEmail().equalsIgnoreCase(email)) {
                result.add(host);
            }
        }
        return result;
    }*/



    private Host deserialize(String[] fields) {
        Host result = new Host();
        result.setId(fields[0]);
        result.setLast_name(fields[1]);
        result.setEmail(fields[2]);
        result.setPhone(fields[3]);
        result.setAddress(fields[4]);
        result.setCity(fields[5]);
        result.setState(fields[6]);
        result.setPostal_code(fields[7]);
        result.setStandard_rate(new BigDecimal(fields[8]));
        result.setWeekend_rate(new BigDecimal(fields[9]));
        return result;
    }

    private String serialize(Host host) {
        return String.format("%s,%s,%s,%s,%s",
                host.getId(),
                host.getLast_name(),
                host.getEmail(),
                host.getPhone(),
                host.getAddress());
    }


    protected void writeAll(List<Host> hosts, String email) throws DataAccessException {
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
    }



}
