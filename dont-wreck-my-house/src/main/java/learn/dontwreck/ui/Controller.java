package learn.dontwreck.ui;

import learn.dontwreck.data.DataAccessException;
import learn.dontwreck.data.GuestRepository;
import learn.dontwreck.domain.GuestService;
import learn.dontwreck.domain.HostService;
import learn.dontwreck.domain.ReservationService;
import learn.dontwreck.domain.Result;
import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;
import learn.dontwreck.models.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.List;

@Component
public class Controller {
    @Autowired
    private final HostService hostService;
    @Autowired
    private final ReservationService reservationService;
    @Autowired
    private final GuestService guestService;

    private final View view;

    @Autowired
    public Controller(HostService hostService, ReservationService reservationService, GuestService guestService, View view) {
        this.hostService = hostService;
        this.reservationService = reservationService;
        this.guestService = guestService;
        this.view = view;
    }

    public void run() {
        view.displayHeader("Welcome to Dont-Wreck-My-House");
        try {
            runAppLoop();
        } catch (DataAccessException ex) {
            view.displayException(ex);
        }
        view.displayHeader("Goodbye.");
    }

    private void runAppLoop() throws DataAccessException {
        MainMenuOption option;
        do {
            option = view.selectMainMenuOption();
            switch (option) {
                case VIEW_RESERVATION_FOR_HOST:
                    viewByHost();
                    break;
                case ADD_RESERVATION:
                    addReservation();
                    break;
                case UPDATE_RESERVATION:
                    updateReservation();
                    break;
                case CANCEL_RESERVATION:
                    deleteReservation();
                    break;
                case GENERATE:
                    break;
            }
        } while (option != MainMenuOption.EXIT);
    }

    // top level menu
    private void viewByHost() {
        /*String host = view.getReservationByHost();
        if(host == null){
            return ;
        }*/

        Host host = getHostId();
        if (host == null) {
            view.displayStatus(false, "Not a valid host");
            return;
        }
        List<Reservation> reservations = reservationService.findByHost(host.getEmail());
        view.displayReservations(reservations);
        view.enterToContinue();
        /*String host = view.getReservationByHost();
        List<Reservation> reservations = reservationService.findByHost(host);
        view.displayReservations(reservations);
        view.enterToContinue();


        String host = view.getReservationByHost();
        Result<Reservation> result = reservationService.findByHost(host);
        if(result.isSuccess()){
            view.getReservationByHost();
        }
           else{
               view.displayStatus(result.isSuccess(), result.getErrorMessages());
           }
       }
         */
    }


    private void addReservation() throws DataAccessException {
        view.displayHeader(MainMenuOption.ADD_RESERVATION.getMessage());

        Guest guest = getGuestId();
        if (guest == null) {
            return;
        }
        Host host = getHostId();
        if (host == null) {
            return;
        }

        Reservation reservation = view.makeReservation(host, guest);
        Result<Reservation> result = reservationService.add(reservation);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Reservation %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    }

    private void updateReservation() throws DataAccessException {
        view.displayHeader(MainMenuOption.UPDATE_RESERVATION.getMessage());
        Host host = getHostId();
        if (host == null) {
            return;
        }
        List<Reservation> reservations = reservationService.findByHost(host.getEmail());
        Reservation reservation = view.chooseReservation(reservations);
        if (reservation != null) {
            reservation = view.update(reservation);
            Result<Reservation> result = reservationService.update(reservation);
        }
    }
//kdeclerkdc@sitemeter.com
//

   private void deleteReservation() throws DataAccessException {
       view.displayHeader(MainMenuOption.CANCEL_RESERVATION.getMessage());
       Host host = getHostId();
       if (host == null) {
           return;
       }
        List<Reservation> reservations = reservationService.findByHost(host.getEmail());
        Reservation reservation = view.chooseReservation(reservations);

       if (reservation != null) {
           Result<Reservation> result = reservationService.deleteReservation(host.getId(),reservation.getId());
           if(result.isSuccess()){
           String successMessage = String.format("Reservation %s Deleted.", reservation.getId());
           System.out.println(successMessage);
           }
           else{
               view.displayStatus(result.isSuccess(), result.getErrorMessages());
           }
       }

    }


/*
    private void deleteReservation() throws DataAccessException {
        view.displayHeader(MainMenuOption.CANCEL_RESERVATION.getMessage());;
        String guest = view.getReservationGuestEmail();
        String host = view.getReservationHostEmail();

        List<Reservation> reservations = reservationService.findByHost(host);
        view.displayReservations(reservations);
        Reservation reservation = view.choosePanel(section, panels);
        if (reservation != null) {
            Result<Reservation> result = reservationService.deleteById(reservation.getId());
            String successMessage = String.format("Reservation %s deleted.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    } */

// FIX THIS
    private Host getHostId() {
        String hostEmail = view.getReservationHostEmail();
        Host host = hostService.findByEmail(hostEmail);
        return host;
    }

    private Guest getGuestId() {
        String guestEmail = view.getReservationGuestEmail();
        Guest guest = guestService.findByEmail(guestEmail);
        return guest;
    }




    /*

        private void updateReservation() throws DataAccessException {
        view.displayHeader(MainMenuOption.UPDATE_RESERVATION.getMessage());
        String guest = view.getReservationGuestEmail();
        String host = view.getReservationHostEmail();
        List<Reservation> reservations = reservationService.f(host);
        Reservation reservation = view.choosePanel(section, panels);
        if (reservation != null) {
            panel = view.update(panel);
            PanelResult result = service.update(panel);
            view.printResult(result);
        }
    }








    private void viewItems() {
        view.displayHeader(MainMenuOption.VIEW_ITEMS.getMessage());
        Category category = view.getItemCategory();
        List<Item> items = itemService.findByCategory(category);
        view.displayHeader("Items");
        view.displayItems(items);
        view.enterToContinue();
    }

    private Forager viewForagers(){
        view.displayHeader(MainMenuOption.VIEW_FORAGERS.getMessage());
        String lastNamePrefix = view.getForagerNamePrefix();
        List<Forager> foragers = hostService.findByLastName(lastNamePrefix);
        return view.chooseForager(foragers);

    }

    private void addForage() throws DataException {
        view.displayHeader(MainMenuOption.ADD_FORAGE.getMessage());
        Forager forager = getForager();
        if (forager == null) {
            return;
        }
        Item item = getItem();
        if (item == null) {
            return;
        }
        Forage forage = view.makeForage(forager, item);
        Result<Forage> result = forageService.add(forage);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Forage %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    }

    private void addItem() throws DataException {
        Item item = view.makeItem();
        Result<Item> result = itemService.add(item);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Item %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    }

    private void generate() throws DataException {
        GenerateRequest request = view.getGenerateRequest();
        if (request != null) {
            int count = forageService.generate(request.getStart(), request.getEnd(), request.getCount());
            view.displayStatus(true, String.format("%s forages generated.", count));
        }
    }

    // support methods
    private Forager getForager() {
        String lastNamePrefix = view.getForagerNamePrefix();
        List<Forager> foragers = hostService.findByLastName(lastNamePrefix);
        return view.chooseForager(foragers);
    }

    private Item getItem() {
        Category category = view.getItemCategory();
        List<Item> items = itemService.findByCategory(category);
        return view.chooseItem(items);
    }*/
}
