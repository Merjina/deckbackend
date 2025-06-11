package com.example.deck.service;

import com.example.deck.dto.QuotationDTO;
import com.example.deck.model.*;
import com.example.deck.repository.QuotationRepository;
import com.example.deck.repository.CartRepository;
import com.example.deck.repository.ProductRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

@Service
public class QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public Quotation fromDTO(QuotationDTO dto) {
        Quotation quotation = new Quotation();
        quotation.setCustomerName(dto.getClientName());
        quotation.setCustomerEmail(dto.getClientEmail());
        quotation.setProductName(dto.getProductName());
        quotation.setProductId(dto.getProductId());
        quotation.setCustomOptions(dto.getCustomOptions());
        quotation.setQuotedPrice(dto.getTotalAmount());
        quotation.setQuantity(dto.getQuantity());
        quotation.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        quotation.setCreatedDate(dto.getCreatedDate() != null ? dto.getCreatedDate() : new Date());
        return quotation;
    }

    public Quotation saveQuotation(Quotation quotation, boolean generatePDF) {
        if (quotation.getStatus() == null) quotation.setStatus("PENDING");
        if (quotation.getCreatedDate() == null) quotation.setCreatedDate(new Date());

        Quotation saved = quotationRepository.save(quotation);

        if (generatePDF) {
            generateQuotationPDF(saved);
        }

        return saved;
    }
    
    public List<Quotation> getQuotationsByEmail(String email) {
        return quotationRepository.findByCustomerEmail(email);
    }


    public List<Quotation> getAllQuotations() {
        return quotationRepository.findAll();
    }

    public Quotation getQuotationById(Long id) {
        return quotationRepository.findById(id).orElse(null);
    }

    public void deleteQuotation(Long id) {
        quotationRepository.deleteById(id);
    }

    public Quotation approveQuotation(Long quotationId) {
        Quotation quotation = getQuotationById(quotationId);
        if (quotation == null) return null;

        quotation.setStatus("APPROVED");

        Order order = new Order();
        order.setCustomerName(quotation.getCustomerName());
        order.setEmail(quotation.getCustomerEmail());
        order.setTotalAmount(quotation.getQuotedPrice());
        order.setAddress("TBD");
        orderService.placeOrder(order);

        try {
            Product product = productRepository.findByProductId(quotation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found for ID: " + quotation.getProductId()));

            Cart existing = cartRepository.findByProductId(product.getId());
            if (existing == null) {
                Cart cartItem = new Cart();
                cartItem.setProductId(product.getId());
                cartItem.setName(product.getName());
                cartItem.setImagePath(product.getImagePath());
                cartItem.setPrice(quotation.getQuotedPrice());
                cartItem.setQuantity(quotation.getQuantity() > 0 ? quotation.getQuantity() : 1);
                cartRepository.save(cartItem);
            }
        } catch (RuntimeException e) {
            System.err.println("⚠️ Failed to auto-add to cart: " + e.getMessage());
            // Don't stop the approval process if cart addition fails
        }

        return quotationRepository.save(quotation);
    }


    public Quotation rejectQuotation(Long quotationId, String reason) {
        Quotation quotation = getQuotationById(quotationId);
        if (quotation == null) return null;

        quotation.setStatus("REJECTED");
        quotation.setRejectionReason(reason != null ? reason : "No reason provided");

        return quotationRepository.save(quotation);
    }

    public List<Quotation> getQuotationsByStatus(String status) {
        return quotationRepository.findByStatusIgnoreCase(status);
    }

    public String generateQuotationPDF(Quotation quotation) {
        Document document = new Document();
        String filePath = "";

        try {
            File outputDir = new File("src/main/resources/quotations/");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            String fileName = "Quotation_" + quotation.getId() + ".pdf";
            filePath = outputDir.getAbsolutePath() + File.separator + fileName;

            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            try {
                Image logo = Image.getInstance("src/main/resources/static/decklinelogo.png");
                logo.scaleAbsolute(100, 50);
                logo.setAlignment(Element.ALIGN_RIGHT);
                document.add(logo);
            } catch (Exception e) {
                System.out.println("⚠️ Logo not found.");
            }

            document.add(new Paragraph("Quotation", titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Quotation ID: " + quotation.getId(), boldFont));
            document.add(new Paragraph("Date: " + new Date(), normalFont));
            document.add(new Paragraph("Client Name: " + quotation.getCustomerName(), normalFont));
            document.add(new Paragraph("Client Email: " + quotation.getCustomerEmail(), normalFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{3, 1, 2, 2, 2});

            table.addCell(new PdfPCell(new Phrase("Product Name", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Qty", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Dimensions", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Unit Price", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Total", boldFont)));

            table.addCell(new Phrase(quotation.getProductName(), normalFont));
            table.addCell(new Phrase(String.valueOf(quotation.getQuantity()), normalFont));
            table.addCell(new Phrase(quotation.getCustomOptions() != null ? quotation.getCustomOptions() : "-", normalFont));
            double unitPrice = quotation.getQuantity() > 0 ? quotation.getQuotedPrice() / quotation.getQuantity() : 0.0;
            table.addCell(new Phrase("₹" + unitPrice, normalFont));
            table.addCell(new Phrase("₹" + quotation.getQuotedPrice(), normalFont));

            document.add(table);

            Paragraph total = new Paragraph("Total Amount: ₹" + quotation.getQuotedPrice(), headerFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.add(new Paragraph("\nThank you for choosing Deckline!", boldFont));
            document.add(new Paragraph("Contact us at support@deckline.com | +91 98765 43210", normalFont));

            document.close();
            System.out.println("✅ PDF generated: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ PDF generation failed for quotation: " + quotation.getId());
        }

        return filePath;
    }
}
