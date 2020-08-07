package com.epam.cdp.module4.homework1.view;

import com.epam.cdp.module4.homework1.model.Ticket;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TicketPdfView extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=tickets_for_user.pdf");

        List<Ticket> tickets = (List<Ticket>) model.get("tickets");
        document.add(new Paragraph("Found tickets for user: " + LocalDate.now()));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100.0f);
        table.setSpacingBefore(10);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);

        if (tickets.isEmpty()) {
            cell.setPhrase(new Phrase("User doesn't have any ticket"));
            table.addCell(cell);
            return;
        }
        cell.setPhrase(new Phrase("Id"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Event id"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("User id"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Category"));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Place"));
        table.addCell(cell);

        for (Ticket ticket : tickets) {
            table.addCell(String.valueOf(ticket.getId()));
            table.addCell(String.valueOf(ticket.getEventId()));
            table.addCell(String.valueOf(ticket.getUserId()));
            table.addCell(ticket.getCategory().toString());
            table.addCell(String.valueOf(ticket.getPlace()));

        }

        document.add(table);
    }
}
