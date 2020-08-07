package com.epam.cdp.module4.homework1.view;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

import static com.epam.cdp.module4.homework1.util.WebConstant.TICKET_PDF_TEMPLATE_NAME;

public class PdfViewResolver implements ViewResolver {

    @Override
    public View resolveViewName(String viewName, Locale locale) {
        if (viewName.equals(TICKET_PDF_TEMPLATE_NAME)) {
            return new TicketPdfView();
        }
        return null;
    }
}
