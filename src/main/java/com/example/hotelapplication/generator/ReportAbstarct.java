package com.example.hotelapplication.generator;

import com.example.hotelapplication.entities.Reservation;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class ReportAbstarct {

    // ----------------------
    // REPORT PDF
    // ----------------------

    public HttpServletResponse initResponseForExportPdf(HttpServletResponse response, String fileName) {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + fileName + "_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        return response;
    }

    public void writeTableHeaderPdf(PdfPTable table, String[] headers) {
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        for (String header : headers) {
            cell.setPhrase(new Phrase(header, getFontContent()));
            table.addCell(cell);
        }
    }

    public Font getFontTitle() {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        return font;
    }

    public Font getFontSubtitle() {
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(12);
        return font;
    }

    public Font getFontContent() {
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(10);
        return font;
    }

    public void enterSpace(Document document) {
        Paragraph space = new Paragraph(" ", getFontSubtitle());
        space.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(space);
    }

    // ----------------------
    // REPORT CSV
    // ----------------------

    public HttpServletResponse initResponseForExportCsv(HttpServletResponse response, String fileName) {
        response.setContentType("text/csv");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=csv_" + fileName + "_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);
        return response;
    }

    public void writeCsvHeader(PrintWriter writer, String[] headers) {
        writer.write(String.join(",", headers) + "\n");
    }

    public void writeCsvContent(PrintWriter writer, List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            writer.write(reservation.getReservationId() + "," +
                    reservation.getPerson().getName() + "," +
                    reservation.getPerson().getEmail() + "," +
                    reservation.getPerson().getAddress() + "," +
                    reservation.getRooms().get(0).getRoomNumber() + "," +
                    reservation.getRooms().get(0).getRoomType() + "," +
                    reservation.getRooms().get(0).getRoomCapacity() + "," +
                    reservation.getRooms().get(0).getRoomCost() + "," +
                    reservation.getReservationStart() + "," +
                    reservation.getReservationEnd() + "," +
                    reservation.getReservationInitialCost() + "," +
                    reservation.getReservationFinalCost() + "\n");
        }
    }

    // ----------------------
    // REPORT TXT
    // ----------------------

    public HttpServletResponse initResponseForExportTxt(HttpServletResponse response, String fileName) {
        response.setContentType("text/plain");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=txt_" + fileName + "_" + currentDateTime + ".txt";
        response.setHeader(headerKey, headerValue);
        return response;
    }

    public void writeTxtContent(PrintWriter writer, List<Reservation> reservations) {
        writer.write("Reservations Report\n\n");
        for (Reservation reservation : reservations) {
            writer.write("ID: " + reservation.getReservationId() + "\n");
            writer.write("Name: " + reservation.getPerson().getName() + "\n");
            writer.write("Email: " + reservation.getPerson().getEmail() + "\n");
            writer.write("Address: " + reservation.getPerson().getAddress() + "\n");
            writer.write("Room Number: " + reservation.getRooms().get(0).getRoomNumber() + "\n");
            writer.write("Room Type: " + reservation.getRooms().get(0).getRoomType() + "\n");
            writer.write("Room Capacity: " + reservation.getRooms().get(0).getRoomCapacity() + "\n");
            writer.write("Room Cost: " + reservation.getRooms().get(0).getRoomCost() + "\n");
            writer.write("Start Date: " + reservation.getReservationStart() + "\n");
            writer.write("End Date: " + reservation.getReservationEnd() + "\n");
            writer.write("Initial Price: " + reservation.getReservationInitialCost() + "\n");
            writer.write("Final Price: " + reservation.getReservationFinalCost() + "\n");
            writer.write("\n");
        }
    }
}
