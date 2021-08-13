package learn.dontwreck.ui;

import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;
import learn.dontwreck.models.Reservation;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;

@Component
public class View {
    private final Scanner console = new Scanner(System.in);

    private final ConsoleIO io;

    public View(ConsoleIO io) {
        this.io = io;
    }

    public MainMenuOption selectMainMenuOption() {
        displayHeader("Main Menu");
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (MainMenuOption option : MainMenuOption.values()) {
            if (!option.isHidden()) {
                io.printf("%s. %s%n", option.getValue(), option.getMessage());
            }
            min = Math.min(min, option.getValue());
            max = Math.max(max, option.getValue());
        }

        String message = String.format("Select [%s-%s]: ", min, max - 1);
        return MainMenuOption.fromValue(io.readInt(message, min, max));
    }

    public Reservation update(Reservation reservation) {

        reservation.setStart_date(io.readLocalDate("Please Enter Start Date {MM/dd/yyyy}"));
        reservation.setEnd_date(io.readLocalDate("Please Enter End Date {MM/dd/yyyy}"));
        reservation.setTotal(validateRates(reservation.getStart_date(),reservation.getEnd_date(),reservation.getHost()));
        if(summary(reservation)) {
            io.println("Reservation updated");
            return reservation;
        }else{
            io.println("Reservation not updated");
            return null;
        }
    }

    private LocalDate readRequiredDate(LocalDate date){

        if (date.isBefore(LocalDate.now())) {
            System.out.println("date cannot be in the past.");
        }

        return date;
    }

    public String getReservationByHost() {
        displayHeader(MainMenuOption.VIEW_RESERVATION_FOR_HOST.getMessage());
        return io.readRequiredString("Enter host email: ");
    }

    public String getReservationHostEmail() {
        return io.readRequiredString("Enter host email: ");
    }
    public String getReservationGuestEmail() {
        return io.readRequiredString("Enter Guest email: ");
    }



    public Reservation chooseReservation(List<Reservation> reservations) {
        displayReservations(reservations);
        if (reservations == null || reservations.size() == 0) {
            return null;
        }

        int reservationId = readInt("Choose a Reservation id: ");
        for (Reservation r : reservations) {
            if (r.getId() == reservationId) {
                return r;
            }
        }

        System.out.printf("%nERR: Reservation id %s is not in the list.%n", reservationId);
        return null;
    }



    private int readInt(String prompt) {

        int result = 0;
        boolean isValid = false;

        do {
            try {
                result = Integer.parseInt(readRequiredString(prompt));
                isValid = true;
            } catch (NumberFormatException ex) {
                System.out.println("ERR: value must be a number.");
            }
        } while (!isValid);

        return result;
    }

    private String readRequiredString(String prompt) {
        String value;
        do {
            value = readString(prompt);
            if (value.length() == 0) {
                System.out.println("ERR: value is required.");
            }
        } while (value.length() == 0);
        return value;
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return console.nextLine().trim();
    }

    public Reservation makeReservation(Host host, Guest guest) {
        Reservation reservation = new Reservation();
        LocalDate start_date = io.readLocalDate("Start Date [MM/dd/yyyy]: ");
        LocalDate end_date = io.readLocalDate("End Date [MM/dd/yyyy]: ");
        reservation.setStart_date(start_date);
        reservation.setEnd_date(end_date);
        reservation.setGuest_id(guest.getGuest_id());
        reservation.setTotal(validateRates(start_date,end_date, host));
        reservation.setHost(host);
        reservation.setGuest(guest);
        if(summary(reservation)){
            return reservation;
        }else{
            return null;
        }
    }

    public boolean summary(Reservation reservation){

            System.out.println("Start date: " + reservation.getStart_date());
            System.out.println("End date: " + reservation.getEnd_date());
            System.out.println("Total: " + reservation.getTotal());
            boolean check = io.readBoolean("Is this okay? [y/n]");
            return check;
    }




   /* public Reservation updateReservation(Host host, Guest guest){
        Reservation reservation = new Reservation();

    }*/




    public BigDecimal validateRates(LocalDate start_date, LocalDate end_date, Host host){
        int getDaysBetween = getDaysBetween(start_date, end_date);
        int weekendCount = 0;
        int weekDayCount = 0;
        LocalDate date = start_date;
        for(int i = 0; i < getDaysBetween; i++){
            if(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY){
                weekendCount++;
            }else{
                weekDayCount++;
            }
            date = date.plusDays(1);
        }
        BigDecimal weekendTotal = new BigDecimal(weekendCount).multiply(host.getWeekend_rate());
        BigDecimal nonWeekendTotal = new BigDecimal(weekDayCount).multiply(host.getStandard_rate());
        BigDecimal total = (weekendTotal.add(nonWeekendTotal));

        return total;
    }

    public int getDaysBetween(LocalDate one, LocalDate two){
        int output = 0;
        if(one.isAfter(two)){
            LocalDate temp = two;
            two = one;
            one = temp;
        }
        for(; one.compareTo(two) < 0; one = one.plusDays(1)){
            output++;
        }
        return output;
    }


    public void enterToContinue() {
        io.readString("Press [Enter] to continue.");
    }

    // display only
    public void displayHeader(String message) {
        io.println("");
        io.println(message);
        io.println("=".repeat(message.length()));
    }

    public void displayException(Exception ex) {
        displayHeader("A critical error occurred:");
        io.println(ex.getMessage());
    }

    public void displayStatus(boolean success, String message) {
        displayStatus(success, List.of(message));
    }

    public void displayStatus(boolean success, List<String> messages) {
        displayHeader(success ? "Success" : "Error");
        for (String message : messages) {
            io.println(message);
        }
    }


   public void displayReservations(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            io.println("No reservations found.");
            return;
        }
        for (Reservation reservation : reservations) {
            if(reservation.getHost().getId() == null){
                io.println("Host Id is null.");
                return;
            }
            io.printf("ID:%s %s, %s - %s, Guest: %s, %s, Email: %s%n",
                    reservation.getId(),
                    reservation.getGuest().getGuest_id(),
                    reservation.getStart_date(),
                    reservation.getEnd_date(),
                    reservation.getGuest().getFirst_name(),
                    reservation.getGuest().getLast_name(),
                    reservation.getGuest().getEmail());
        }
    }
}



        /*public GenerateRequest getGenerateRequest() {
        displayHeader(MainMenuOption.GENERATE.getMessage());
        LocalDate start_date = io.readLocalDate("Select a start_date date [MM/dd/yyyy]: ");
        if (start_date.isAfter(LocalDate.now())) {
            displayStatus(false, "Start date must be in the past.");
            return null;
        }

        LocalDate end_date = io.readLocalDate("Select an end_date date [MM/dd/yyyy]: ");
        if (end_date.isAfter(LocalDate.now()) || end_date.isBefore(start_date)) {
            displayStatus(false, "End date must be in the past and after the start_date date.");
            return null;
        }

        GenerateRequest request = new GenerateRequest();
        request.setStart_date(start_date);
        request.setEnd_date(end_date);
        request.setCount(io.readInt("Generate how many random forages [1-500]?: ", 1, 500));
        return request;
    }*/



    /*public void displayReservationsByGuest( Guest guests, String guestId) {
        if (guests == null || guests.isEmpty() ) {
            io.println("No reservations found.");
            return;
        }
        if(guestId)
        for (Reservation reservation : ) {
            io.printf("ID: %s, %s - %s, Guest: %s, %s, Email: %s%n",
                    reservation.getGuest().getGuest_id(),
                    reservation.getStart_date(),
                    reservation.getEnd_date(),
                    reservation.getGuest().getFirst_name(),
                    reservation.getGuest().getLast_name(),
                    reservation.getGuest().getEmail());
        }
    }




    public void displayItems(List<Item> items) {

        if (items.size() == 0) {
            io.println("No items found");
        }

        for (Item item : items) {
            io.printf("%s: %s, %s, %.2f $/kg%n", item.getId(), item.getName(), item.getCategory(), item.getDollarPerKilogram());
        }
    }


        public Reservation chooseHost(List<Reservation> reservations) {
        if (reservations.size() == 0) {
            io.println("No reservations found");
            return null;
        }

        int index = 1;
        for (Reservation reservation : reservations.stream().limit(25).collect(Collectors.toList())) {
            io.printf("ID: %s, %s - %s, Guest: %s, %s, Email: %s%n", reservation.getGuest().getGuest_id(), reservation.getStart_date(),
                    reservation.getEnd_date(), reservation.getGuest().getFirst_name(), reservation.getGuest().getLast_name(), reservation.getGuest().getEmail());
        }
        index--;

        if (reservations.size() > 25) {
            io.println("More than 25 Reservations found");
        }
        io.println("0: Exit");
        String message = String.format("Select a Reservation by their index [0-%s]: ", index);

        index = io.readInt(message, 0, index);
        if (index <= 0) {
            return null;
        }
        return reservations.get(index - 1);
    }


    */


