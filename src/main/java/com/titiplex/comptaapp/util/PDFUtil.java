package com.titiplex.comptaapp.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Account;
import com.titiplex.comptaapp.models.Event;
import com.titiplex.comptaapp.models.Transaction;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class PDFUtil {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private PDFUtil() {
    }

    /* =========================================================
       === EXPORT GÉNÉRIQUE : liste de transactions + titre ===
       ========================================================= */
    public static void exportTransactions(File file, String title,
                                          List<Transaction> tx) throws Exception {

        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 50, 36);
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        // Entête entreprise
        String company = DataStore.meta("companyName", "(Entreprise)");
        String country = DataStore.meta("country", "");
        Font h0 = new Font(Font.HELVETICA, 10, Font.ITALIC);
        doc.add(new Paragraph(company + (country.isBlank() ? "" : " – " + country), h0));
        doc.add(Chunk.NEWLINE);

        // Titre
        Font h1 = new Font(Font.HELVETICA, 18, Font.BOLD);
        doc.add(new Paragraph(title, h1));
        doc.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.setWidths(new float[]{2, 6, 4, 2});
        Stream.of("Date", "Description", "Compte", "Montant €")
                .forEach(col -> table.addCell(header(col)));

        double total = 0;
        for (Transaction t : tx) {
            table.addCell(cell(t.getDate().format(DF)));
            table.addCell(cell(t.getDescription()));
            table.addCell(cell(accountName(t.getAccountId())));
            table.addCell(cell(String.format("%.2f", t.getAmount())));
            total += t.getAmount();
        }

        PdfPCell tot = new PdfPCell(new Phrase("Solde"));
        tot.setColspan(3);
        tot.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tot.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(tot);
        table.addCell(cell(String.format("%.2f", total)));

        doc.add(table);
        doc.close();
    }

    /* ========= Helpers ======== */
    private static PdfPCell header(String s) {
        PdfPCell c = new PdfPCell(new Phrase(s));
        c.setBackgroundColor(Color.LIGHT_GRAY);
        return c;
    }

    private static PdfPCell cell(String s) {
        return new PdfPCell(new Phrase(s));
    }

    private static String accountName(int id) {
        return DataStore.accounts.stream().filter(a -> a.getId() == id)
                .findFirst().map(Account::getName).orElse("");
    }

    public static void exportFinancialReport(File file, Period period,
                                             String logoPath) throws Exception {

        /* ------------ Préparation des données ------------- */
        LocalDate from = period == null ? null : period.from();

        // Transactions triées
        List<Transaction> tx = DataStore.transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate))
                .toList();

        // Solde avant la période
        double soldeInitial = (from == null)
                ? 0                                           // pas de borne → 0
                : tx.stream()
                .filter(t -> t.getDate().isBefore(from)) // avant la borne
                .mapToDouble(Transaction::getAmount)
                .sum();


        // Filtré sur la période
        Map<Event, double[]> map = new LinkedHashMap<>();      // [recettes, dépenses]

        tx.stream()
                .filter(t -> PeriodDialog.inPeriod(t.getDate(), period))
                .forEach(t -> {
                    Event ev = DataStore.events.stream()
                            .filter(e -> e.getId() == t.getEventId())
                            .findFirst().orElse(null);
                    map.putIfAbsent(ev, new double[2]);
                    if (t.getAmount() > 0) map.get(ev)[0] += t.getAmount();
                    else //noinspection UnnecessaryUnaryMinus
                        map.get(ev)[1] += -t.getAmount();
                });

        double totRecettes = map.values().stream().mapToDouble(a -> a[0]).sum();
        double totDepenses = map.values().stream().mapToDouble(a -> a[1]).sum();
        double variation = totRecettes - totDepenses;
        double soldeFinal = soldeInitial + variation;

        /* ------------ Création du document ------------- */
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 50, 36);
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        // entête
        addHeader(doc, period, logoPath);

        /* ------------ Tableau par évènement ------------- */
        PdfPTable tab = new PdfPTable(4);
        tab.setWidths(new float[]{5, 3, 3, 3});
        Stream.of("Évènement", "Recettes", "Dépenses", "Net")
                .forEach(c -> tab.addCell(header(c)));

        for (var entry : map.entrySet()) {
            Event ev = entry.getKey();
            double rec = entry.getValue()[0];
            double dep = entry.getValue()[1];
            tab.addCell(cell(ev == null ? "(Sans évènement)" : ev.getName()));
            tab.addCell(money(rec));
            tab.addCell(money(dep));
            tab.addCell(money(rec - dep));
        }

        // ligne totaux
        PdfPCell tot = new PdfPCell(new Phrase("TOTAL"));
        tot.setColspan(1);
        tot.setBackgroundColor(Color.LIGHT_GRAY);
        tab.addCell(tot);
        tab.addCell(money(totRecettes));
        tab.addCell(money(totDepenses));
        tab.addCell(money(variation));
        doc.add(tab);

        doc.add(Chunk.NEWLINE);

        /* ------------ Solde initial / final ------------- */
        PdfPTable sol = new PdfPTable(2);
        sol.setWidths(new int[]{3, 2});
        sol.addCell(cell("Solde initial"));
        sol.addCell(money(soldeInitial));
        sol.addCell(cell("Variation"));
        sol.addCell(money(variation));
        PdfPCell fin = new PdfPCell(new Phrase("Solde final"));
        fin.setBackgroundColor(Color.LIGHT_GRAY);
        sol.addCell(fin);
        sol.addCell(money(soldeFinal));
        doc.add(sol);

        doc.close();
    }

    /* ---- helpers ---- */
    private static PdfPCell money(double v) {
        PdfPCell c = new PdfPCell(new Phrase(String.format("%.2f", v)));
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return c;
    }

    /* Entête avec logo et titre */
    private static void addHeader(Document doc, Period period, String logoPath) {
        PdfPTable head = new PdfPTable(2);
        head.setWidths(new int[]{6, 1});
        head.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        String company = DataStore.meta("companyName", "(Entreprise)");
        String country = DataStore.meta("country", "");
        Font fComp = new Font(Font.HELVETICA, 20, Font.BOLD);
        head.addCell(new Phrase(company + (country.isBlank() ? "" : " – " + country), fComp));

        if (logoPath != null && !logoPath.isBlank()) {
            try {
                URL logo = PDFUtil.class.getResource(logoPath);
                if (logo == null)
                    AlertUtil.warning("Logo cannot be found : " + logoPath);
                else {
                    Image img = Image.getInstance(logo);
                    img.scaleToFit(80, 40);
                    head.addCell(img);
                }
            } catch (Exception e) {
                head.addCell("");
            }         // pas de logo
        } else head.addCell("");

        doc.add(head);

        Font h1 = new Font(Font.HELVETICA, 18, Font.UNDERLINE);
        String pTxt;
        if (period == null || (period.from() == null && period.to() == null)) pTxt = "";
        else if (period.from() == null) pTxt = "jusqu’au " + period.to().format(DF);
        else if (period.to() == null) pTxt = "depuis le " + period.from().format(DF);
        else pTxt = "du " + period.from().format(DF) + " au " + period.to().format(DF);

        Paragraph title = new Paragraph("Bilan financier " + pTxt, h1);
        title.setSpacingBefore(10);
        title.setSpacingAfter(10);
        doc.add(title);
    }
}
