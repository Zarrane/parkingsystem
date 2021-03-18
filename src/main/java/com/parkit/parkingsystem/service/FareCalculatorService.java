package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    private TicketDAO ticketDAO;


    public static double round(double tooLong) {
        double roundedDouble = new BigDecimal(tooLong).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return roundedDouble;
    }

    public void calculateFare(Ticket ticket) {
        ticketDAO = new TicketDAO();

        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        double durationHours = (double) (Math.abs((ticket.getOutTime().getTime() - ticket.getInTime().getTime())))
                / 1000 / 60 / 60;

        double coefDiscount = 1;

        boolean regular = ticketDAO.regularCustomer(ticket.getVehicleRegNumber());

        System.out.println("My Reg Number is --> " + ticket.getVehicleRegNumber());

        if (durationHours <= 0.50) { // if under 30 mn --> free of charges
            ticket.setPrice(0);
        } else {
            if (regular) { // if VehicleRegNumber Already passed, -5%
                //if (ticket.getVehicleRegNumber() == "cc-666-ar") { // if VehicleRegNumber Already passed, -5%
                coefDiscount = 0.95;
            }

            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(round(durationHours * Fare.CAR_RATE_PER_HOUR * coefDiscount));
                    break;
                }
                case BIKE: {
                    ticket.setPrice(round(durationHours * Fare.BIKE_RATE_PER_HOUR * coefDiscount));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }

    }



}