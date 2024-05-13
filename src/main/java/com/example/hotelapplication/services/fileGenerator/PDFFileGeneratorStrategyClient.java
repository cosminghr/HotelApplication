package com.example.hotelapplication.services.fileGenerator;

import com.example.hotelapplication.dtos.ReservationDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

public class PDFFileGeneratorStrategyClient implements FileGeneratorStrategy{

    private void generatePDFforClient(ReservationDTO reservationDTO) throws IOException {
        try {
            // Create a new PDF document
            PDDocument document = new PDDocument();

            // Add a page to the document
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a new content stream for the page
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Write reservation details to the PDF
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Reservation ID: " + reservationDTO.getReservationId());
            contentStream.newLine();
            contentStream.showText("Person Name: " + reservationDTO.getPerson().getName());
            contentStream.newLine();
            contentStream.showText("Email: " + reservationDTO.getPerson().getEmail());
            contentStream.newLine();
            contentStream.showText("Start Date: " + reservationDTO.getReservationStart());
            contentStream.newLine();
            contentStream.showText("End Date: " + reservationDTO.getReservationEnd());
            // Add more details as needed
            contentStream.endText();

            // Close the content stream
            contentStream.close();

            // Save the document to a file
            document.save("reservation_" + reservationDTO.getReservationId() + ".pdf");

            // Close the document
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
