package com.example.hotelapplication.generator;

import com.example.hotelapplication.entities.Reservation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    ReportPDFService exportPDF;

    @Autowired
    ReportCSVService exportCSV;

    @Autowired
    ReportTXTService exportTXT;

    @Autowired
    ReportRepository userReportRepo;

    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<Reservation> data = userReportRepo.findAll();
        exportPDF.exportToPDF(response, data);
    }

    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Reservation> data = userReportRepo.findAll();
        exportCSV.exportToCSV(response, data);
    }

    public void exportToTXT(HttpServletResponse response) throws IOException {
        List<Reservation> data = userReportRepo.findAll();
        exportTXT.exportToTXT(response, data);
    }
}
