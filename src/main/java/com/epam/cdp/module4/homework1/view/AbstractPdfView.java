package com.epam.cdp.module4.homework1.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public abstract class AbstractPdfView extends AbstractView {

    private static final float PAPER_LEFT_MARGIN = 36;
    private static final float PAPER_TOP_MARGIN = 54;

    public AbstractPdfView() {
        setContentType("application/pdf");
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ByteArrayOutputStream outputStream = createTemporaryOutputStream();

        Document document = new Document(
                PageSize.A4.rotate(), PAPER_LEFT_MARGIN, PAPER_LEFT_MARGIN, PAPER_TOP_MARGIN, PAPER_LEFT_MARGIN);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        prepareWriter(writer);

        document.open();
        buildPdfDocument(model, document, writer, request, response);
        document.close();

        response.setHeader("Content-Disposition", "attachment");    // make browser to ask for download/display
        writeToResponse(response, outputStream);
    }

    protected void prepareWriter(PdfWriter writer) {
        writer.setViewerPreferences(getViewerPreferences());
    }

    protected int getViewerPreferences() {
        return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
    }


    protected abstract void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception;
}
