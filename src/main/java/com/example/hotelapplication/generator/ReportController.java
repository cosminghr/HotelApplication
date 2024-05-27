package com.example.hotelapplication.generator;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    ReportService userReportService;

    @GetMapping("/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        userReportService.exportToPdf(response);
    }

    @GetMapping("/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        userReportService.exportToCSV(response);
    }

    @GetMapping("/txt")
    public void exportToTXT(HttpServletResponse response) throws IOException {
        userReportService.exportToTXT(response);
    }
}
