package learn.dontwreck.data;

import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HostRepositoryDouble implements HostRepository {
//    id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate
public final static Host HOST = new Host("498604db-b6d6-4599-a503-3d8190fda823","Yearnese",
        "eyearnes05@sfgate.com","(806) 1783815","4 Nova Trail","Amarillo","TX","79182",
        new BigDecimal(340),new BigDecimal(425));
    private final ArrayList<Host> hosts = new ArrayList<>();
    public HostRepositoryDouble() {
        hosts.add(HOST);
    }

    public Host findByEmail(String email) {
        return hosts.stream()
                .filter(i -> i.getEmail().equalsIgnoreCase(email)).findFirst()
                .orElse(null);
    }

    @Override
    public List<Host> findAll() {
        return null;
    }


    public Host add(Host host) throws DataAccessException {
        host.setId(java.util.UUID.randomUUID().toString());
        hosts.add(host);
        return host;
    }


    public boolean update(Host host) throws DataAccessException {
        return false;
    }
}
