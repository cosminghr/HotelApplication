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
    ReportRepository userReportRepo;


    public void exportToPdf(HttpServletResponse response) throws IOException {
        // get all user
        List<Reservation> data = userReportRepo.findAll();

        // export to pdf
        exportPDF.exportToPDF(response, data);
    }

}