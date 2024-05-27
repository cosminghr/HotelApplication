package com.example.hotelapplication.generator;

import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.entities.Reservation;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportPDFService extends ReportAbstarct {

    public void writeTableData(PdfPTable table, Object data) {
        List<Reservation> list = (List<Reservation>) data;

        // for auto wide by paper  size
        table.setWidthPercentage(100);
        // cell
        PdfPCell cell = new PdfPCell();
        int number = 0;
        for (Reservation item : list) {
            cell.setPhrase(new Phrase(String.valueOf(item.getReservationId()), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(item.getPerson().getName()), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(item.getPerson().getEmail()), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(item.getPerson().getAddress()), getFontContent()));
            table.addCell(cell);


            cell.setPhrase(new Phrase(String.valueOf(item.getRooms().get(0).getRoomNumber()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getRooms().get(0).getRoomType()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getRooms().get(0).getRoomCapacity()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getRooms().get(0).getRoomCost()), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(item.getReservationStart()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getReservationEnd()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getReservationInitialCost()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getReservationFinalCost()), getFontContent()));
            table.addCell(cell);
        }

    }


    public void exportToPDF(HttpServletResponse response, Object data) throws IOException {


        // init respose
        response = initResponseForExportPdf(response, "Reservatiom");

        // define paper size
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        // start document
        document.open();

        // title
        Paragraph title = new Paragraph("Report Reservation", getFontTitle());
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        // subtitel
        Paragraph subtitel = new Paragraph("Report Date : 27/05/2024", getFontSubtitle());
        subtitel.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(subtitel);

        enterSpace(document);

        // table header
        String[] headers = new String[]{"ID", "Name", "Email-Address", "Address", "Room Number", "Room Type", "Room Capacity", "Room Cost", "Start Date", "End Date", "Initial Price", "Final Price"};
        PdfPTable tableHeader = new PdfPTable(12);
        writeTableHeaderPdf(tableHeader, headers);
        document.add(tableHeader);

        // table content
        PdfPTable tableData = new PdfPTable(12);
        writeTableData1(tableData, data);
        document.add(tableData);

        document.close();
    }

    public void writeTableData1(PdfPTable table, Object data) {
        List<ReservationDTO> list = (List<ReservationDTO>) data;


        // for auto wide by paper size
        table.setWidthPercentage(100);
        // cell
        PdfPCell cell = new PdfPCell();
        for (ReservationDTO item : list) {
            cell.setPhrase(new Phrase(String.valueOf(item.getReservationId()), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(item.getPerson().getName()), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(item.getPerson().getEmail()), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(item.getPerson().getAddress()), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(item.getRooms().get(0).getRoomNumber()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getRooms().get(0).getRoomType()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getRooms().get(0).getRoomCapacity()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getRooms().get(0).getRoomCost()), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(item.getReservationStart()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getReservationEnd()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getReservationInitialCost()), getFontContent()));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(item.getReservationFinalCost()), getFontContent()));
            table.addCell(cell);
        }
    }

    public byte[] generatePdfBytes(Object data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // define paper size
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, byteArrayOutputStream);

        // start document
        document.open();

        // title
        Paragraph title = new Paragraph("Report Reservation", getFontTitle());
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        // subtitle
        Paragraph subtitle = new Paragraph("Report Date : 27/05/2024", getFontSubtitle());
        subtitle.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(subtitle);

        enterSpace(document);

        // table header
        String[] headers = new String[]{"ID", "Name", "Email-Address", "Address", "Room Number", "Room Type", "Room Capacity", "Room Cost", "Start Date", "End Date", "Initial Price", "Final Price"};
        PdfPTable tableHeader = new PdfPTable(12);
        writeTableHeaderPdf(tableHeader, headers);
        document.add(tableHeader);

        // table content
        PdfPTable tableData = new PdfPTable(12);
        writeTableData1(tableData, data);
        document.add(tableData);

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

}