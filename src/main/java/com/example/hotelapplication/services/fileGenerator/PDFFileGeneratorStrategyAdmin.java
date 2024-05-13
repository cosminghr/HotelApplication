package com.example.hotelapplication.services.fileGenerator;

import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import java.io.IOException;
import java.util.List;

public class PDFFileGeneratorStrategyAdmin implements FileGeneratorStrategy {
    @Override
    public void generateAdmin(List<ReservationDTO> reservationDTOs) throws IOException {
        try {
            // Create a new PDF document
            PDDocument document = new PDDocument();

            // Add a page to the document
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a new content stream for the page
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Set up table parameters
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float bottomMargin = 70;
            float yPosition = yStart;
            float cellMargin = 5f;

            // Define the number of columns and rows
            int numOfColumns = 7;
            int numOfRows = reservationDTOs.size() + 1; // Include header row

            // Calculate column width
            float[] columnWidths = {100, 100, 100, 100, 100, 100, 100}; // Adjust widths as needed

            // Create table
            drawTable(contentStream, yStart, tableWidth, bottomMargin, numOfColumns, numOfRows, columnWidths, cellMargin, reservationDTOs);

            // Close the content stream
            contentStream.close();

            // Save the document to a file
            document.save("all_reservations.pdf");

            // Close the document
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawTable(PDPageContentStream contentStream, float y, float tableWidth, float bottomMargin,
                           int numOfColumns, int numOfRows, float[] columnWidths, float cellMargin,
                           List<ReservationDTO> reservationDTOs) throws IOException {
        float rowHeight = 20;
        float tableRowHeight = rowHeight + 10;
        float cellHeight = 20;

        // Draw table borders
        drawTableBorders(contentStream, y, tableWidth, bottomMargin, numOfColumns, numOfRows, rowHeight);

        // Write header row
        writeHeaderRow(contentStream, y, tableWidth, rowHeight, cellMargin);

        // Write data rows
        writeDataRows(contentStream, y - tableRowHeight, tableWidth, rowHeight, cellMargin, numOfColumns, cellHeight, reservationDTOs);
    }

    private void drawTableBorders(PDPageContentStream contentStream, float y, float tableWidth, float bottomMargin,
                                  int numOfColumns, int numOfRows, float rowHeight) throws IOException {
        float nextY = y;
        for (int i = 0; i <= numOfRows; i++) {
            contentStream.moveTo(50, nextY);
            contentStream.lineTo(50 + tableWidth, nextY);
            contentStream.stroke();
            nextY -= rowHeight;
        }
        // Draw columns
        float nextX = 50;
        for (int i = 0; i <= numOfColumns; i++) {
            contentStream.moveTo(nextX, y);
            contentStream.lineTo(nextX, y - (rowHeight * numOfRows));
            contentStream.stroke();
            nextX += (tableWidth / numOfColumns);
        }
    }

    private void writeHeaderRow(PDPageContentStream contentStream, float y, float tableWidth, float rowHeight,
                                float cellMargin) throws IOException {
        float nextY = y - rowHeight;
        String[] headers = {"Reservation ID", "Person Name", "Email", "Start Date", "End Date", "Room Numbers", "Final Cost"};
        float startX = 50;
        for (String header : headers) {
            float cellWidth = (tableWidth / 7);
            contentStream.beginText();
            contentStream.newLineAtOffset(startX + cellMargin, nextY - cellMargin);
            contentStream.showText(header);
            contentStream.endText();
            startX += cellWidth;
        }
    }

    private void writeDataRows(PDPageContentStream contentStream, float y, float tableWidth, float rowHeight,
                               float cellMargin, int numOfColumns, float cellHeight,
                               List<ReservationDTO> reservationDTOs) throws IOException {
        float nextY = y;
        for (ReservationDTO reservationDTO : reservationDTOs) {
            nextY -= rowHeight;
            float startX = 50;
            String[] rowData = {
                    reservationDTO.getReservationId().toString(),
                    reservationDTO.getPerson().getName(),
                    reservationDTO.getPerson().getEmail(),
                    reservationDTO.getReservationStart().toString(),
                    reservationDTO.getReservationEnd().toString(),
                    getRoomNumbersAsString(reservationDTO.getRooms()),
                    Integer.toString(reservationDTO.getReservationFinalCost())
            };
            for (String data : rowData) {
                float cellWidth = (tableWidth / numOfColumns);
                contentStream.beginText();
                contentStream.newLineAtOffset(startX + cellMargin, nextY - cellMargin - (cellHeight / 2));
                contentStream.showText(data);
                contentStream.endText();
                startX += cellWidth;
            }
        }
    }

    private String getRoomNumbersAsString(List<RoomsDTO> rooms) {
        StringBuilder roomNumbers = new StringBuilder();
        for (RoomsDTO roomDTO : rooms) {
            roomNumbers.append(roomDTO.getRoomNumber()).append(", ");
        }
        return roomNumbers.toString();
    }
}
