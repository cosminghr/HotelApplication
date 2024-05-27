package com.example.hotelapplication.generator;

import com.example.hotelapplication.entities.Reservation;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class ReportTXTService extends ReportAbstarct {

    public void exportToTXT(HttpServletResponse response, List<Reservation> reservations) throws IOException {
        response = initResponseForExportTxt(response, "Reservations");
        PrintWriter writer = response.getWriter();

        writeTxtContent(writer, reservations);

        writer.flush();
        writer.close();
    }
}
