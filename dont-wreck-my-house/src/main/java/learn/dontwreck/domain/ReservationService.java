package learn.dontwreck.domain;

import learn.dontwreck.data.DataAccessException;
import learn.dontwreck.data.GuestRepository;
import learn.dontwreck.data.HostRepository;
import learn.dontwreck.data.ReservationRepository;
import learn.dontwreck.models.Guest;
import learn.dontwreck.models.Host;
import learn.dontwreck.models.Reservation;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HostRepository hostRepository;
    private final GuestRepository guestRepository;

    public ReservationService(ReservationRepository reservationRepository, HostRepository hostRepository, GuestRepository guestRepository) {
        this.reservationRepository = reservationRepository;
        this.hostRepository = hostRepository;
        this.guestRepository = guestRepository;
    }

    public List<Reservation> findByHost(String hostEmail) {
        Host host = new Host();
        host = hostRepository.findByEmail(hostEmail);
        Result result = validateHost(host);
        List<Reservation> reservations = reservationRepository.findByHost(host.getId());

        Map<String, Host> hostMap = hostRepository.findAll().stream()
                .collect(Collectors.toMap(i -> i.getId(), i -> i));
        Map<Integer, Guest> guestMap = guestRepository.findAll().stream()
                .collect(Collectors.toMap(i -> i.getGuest_id(), i -> i));

        for (Reservation reservation : reservations) {
            reservation.setHost(hostMap.get(reservation.getHost().getId()));
            reservation.setGuest(guestMap.get(reservation.getGuest().getGuest_id()));
        }

        return reservations;
    }



    public Result<Reservation> add(Reservation reservation) throws DataAccessException {
        Result<Reservation> result = validate(reservation);
        if (!result.isSuccess()) {
            return result;
        }

        result.setPayload(reservationRepository.add(reservation));

        return result;
    }

    public Result deleteReservation(String hostId, int id) throws DataAccessException {
        Result result =  validateDelete(hostId, id);
        if (!result.isSuccess()) {
            return result;
        }
        result.setPayload(reservationRepository.deleteByReservation(hostId,id));
        return result;

       /* Result result = new Result();
        if (!result.isSuccess()) {
            return result;
        }
        result.setPayload(reservationRepository.deleteByReservation(hostId,id));
        return result;*/
    }


    public Result update(Reservation reservation) throws DataAccessException {
        Result<Reservation> result = validate(reservation);
        if (!result.isSuccess()) {
            return result;
        }

        if (reservation.getId() <= 0) {
            result.addErrorMessage("cannot update a Reservation without an id");
            return result;
        }


        if (!reservationRepository.update(reservation)) {
            result.addErrorMessage(String.format("Reservation id %s does not exist.", reservation.getId()));
            return result;
        }

        return result;
    }

//Future dates
//  No overlapping
//    Start date before end


    public int generate(LocalDate start_date, LocalDate end_date, int count) throws DataAccessException {

        if (start_date == null || end_date == null || start_date.isAfter(end_date) || count <= 0) {
            return 0;
        }

        count = Math.min(count, 500);

        ArrayList<LocalDate> dates = new ArrayList<>();
        while (!start_date.isAfter(end_date)) {
            dates.add(start_date);
            start_date = start_date.plusDays(1);
        }

        List<Host> hosts = hostRepository.findAll();
        List<Guest> guests = guestRepository.findAll();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            Reservation reservation = new Reservation();
            LocalDate start = dates.get(random.nextInt(dates.size()));
            LocalDate end = dates.get(random.nextInt(dates.size()));
            reservation.setStart_date(start);
            reservation.setEnd_date(end);
            reservation.setGuest(guests.get(random.nextInt(guests.size())));
            reservation.setTotal(validateRates(reservation,start,end));
            reservationRepository.add(reservation);
        }

        return count;
    }
//14,2020-02-02,2020-03-03,642,10540
    private Result<Reservation> validateDelete(String hostId, int id){
        Result<Reservation> result = new Result<>();

        if(!validatePastDateDelete(hostId, id)){
            result.addErrorMessage("Date can't be in the past.");
        }
        return result;
    }

    private boolean validatePastDateDelete(String hostId, int id){
        for(Reservation r : reservationRepository.findByHost(hostId)){
                if (r.getId() == id && r.getEnd_date().isBefore(LocalDate.now())) {
                    return false;
                }
        }
        return true;
    }
    private Result<Reservation> validate(Reservation reservation) throws DataAccessException {

        Result<Reservation> result = validateNulls(reservation);
        if (!result.isSuccess()) {
            return result;
        }

        validateFields(reservation, result);
        if (!result.isSuccess()) {
            return result;
        }


        if(!validateOverlap(reservation,result)){
            result.addErrorMessage("Date can't overlap.");
            return result;
        }

        validateChildrenExist(reservation, result);

        return result;
    }

    private Result<Reservation> validateHost(Host host){
        Result<Reservation> result = new Result<>();
        if(host == null){
            result.addErrorMessage("Nothing to save.");
            return result;
        }

        if(host.getId() == null){
            result.addErrorMessage("Host email is required.");
            return result;
        }
        return result;
    }

    private Result<Reservation> validateNulls(Reservation reservation) {
        Result<Reservation> result = new Result<>();

        if (reservation == null) {
            result.addErrorMessage("Nothing to save.");
            return result;
        }

        if (reservation.getHost() == null) {
            result.addErrorMessage("Host email is required.");
        }


        if (reservation.getGuest() == null) {
            result.addErrorMessage("Guest is required.");
        }
        return result;
    }

    private void validateFields(Reservation reservation, Result<Reservation> result) {
        // No future dates.
        if (reservation.getStart_date().isBefore(LocalDate.now())) {
            result.addErrorMessage("date cannot be in the past.");
        }

        if (reservation.getStart_date().isAfter(reservation.getEnd_date())) {
            result.addErrorMessage("start date cannot be after end date.");
        }

//        No
    }

    private boolean validateOverlap(Reservation reservation, Result<Reservation> result)  {

        for (Reservation r : reservationRepository.findByHost(reservation.getHost().getId())) {
            if (!(reservation.getStart_date().compareTo(r.getEnd_date()) >= 0 || reservation.getEnd_date().compareTo(r.getStart_date()) <=0)) {
                return false;
            }
        }
        return true;
    }



    private BigDecimal validateRates(Reservation reservation, LocalDate start_date, LocalDate end_date){
        int getDaysBetween = getDaysBetween(start_date, end_date);
        int weekendCount = 0;
        LocalDate date = reservation.getStart_date();
        for(; weekendCount > getDaysBetween; date=date.plusDays(1)){
            if(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY){
                weekendCount = weekendCount + 1;
            }else{
                weekendCount =weekendCount +0;
            }
        }
        BigDecimal weekendTotal = new BigDecimal(weekendCount).multiply(reservation.getHost().getWeekend_rate());
        BigDecimal nonWeekendTotal = new BigDecimal(getDaysBetween - weekendCount).multiply(reservation.getHost().getStandard_rate());
        BigDecimal total = (weekendTotal.add(nonWeekendTotal));

        return total;
    }

    private int getDaysBetween(LocalDate one, LocalDate two){
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

    private void validateChildrenExist(Reservation reservation, Result<Reservation> result) {

        if (reservation.getHost().getId() == null
                || hostRepository.findByEmail(reservation.getHost().getEmail()) == null) {
            result.addErrorMessage("Host does not exist.");
        }

        if (guestRepository.findByEmail(reservation.getGuest().getEmail()) == null) {
            result.addErrorMessage("Guest does not exist.");
        }
    }
}
