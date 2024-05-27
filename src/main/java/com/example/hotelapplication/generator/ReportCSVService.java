package com.example.hotelapplication.generator;

import com.example.hotelapplication.entities.Reservation;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class ReportCSVService extends ReportAbstarct {

    public void exportToCSV(HttpServletResponse response, List<Reservation> reservations) throws IOException {
        response = initResponseForExportCsv(response, "Reservations");
        PrintWriter writer = response.getWriter();

        String[] headers = {"ID", "Name", "Email", "Address", "Room Number", "Room Type", "Room Capacity", "Room Cost", "Start Date", "End Date", "Initial Price", "Final Price"};
        writeCsvHeader(writer, headers);
        writeCsvContent(writer, reservations);

        writer.flush();
        writer.close();
    }
}
