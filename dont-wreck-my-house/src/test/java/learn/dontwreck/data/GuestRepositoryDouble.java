package learn.dontwreck.data;

import learn.dontwreck.models.Guest;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
//guest_id,first_name,last_name,email,phone,state
public class GuestRepositoryDouble implements  GuestRepository{
    public final static Guest GUEST = new Guest(245, "James", "Yearnese", "eyearnes05@sfgate.com", "(806) 1783815", "TX");
    private final ArrayList<Guest> guests = new ArrayList<>();
    public GuestRepositoryDouble() {
        guests.add(GUEST);
    }

    public Guest findByEmail(String email) {
        return guests.stream()
                .filter(i -> i.getEmail().equalsIgnoreCase(email)).findFirst()
                .orElse(null);
    }

    @Override
    public List<Guest> findAll() {
        return null;
    }


   /* public Guest add(Guest guest) throws DataAccessException {
        guest.setId(java.util.UUID.randomUUID().toString());
        guest.add(guest);
        return guest;
    }


    public boolean update(Guest guest) throws DataAccessException {
        return false;
    }*/
}
