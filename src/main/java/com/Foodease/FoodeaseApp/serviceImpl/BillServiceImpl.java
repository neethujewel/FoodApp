package com.Foodease.FoodeaseApp.serviceImpl;

import com.Foodease.FoodeaseApp.JWT.JwtFilter;
import com.Foodease.FoodeaseApp.POJO.Bill;
import com.Foodease.FoodeaseApp.constant.RestaurantConstants;
import com.Foodease.FoodeaseApp.dao.BillDao;
import com.Foodease.FoodeaseApp.service.BillService;
import com.Foodease.FoodeaseApp.utils.RestaurantUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService {
    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BillDao billDao;
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport ");
        try {
            String fileName;
            if (validateRequestMap(requestMap)){
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")){
                    fileName=(String) requestMap.get("uuid");
                }else {
                    fileName = RestaurantUtils.getUUId();
                    requestMap.put("uuid",fileName);
                    insertBill(requestMap);
                }
                String data = "name:"+requestMap.get("name")+"\n"+
                        "contactNumber:"+requestMap.get("contactNumber")+"\n"+
                        "email:"+requestMap.get("email")+"\n"+
                        "paymentMethod:"+requestMap.get("paymentMethod");

                Document document = new Document();
                PdfWriter.getInstance(document,new FileOutputStream(RestaurantConstants.STORE_LOCATION+"\\"+fileName+".pdf"));

                document.open();
                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph("Foodese System ",getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph = new Paragraph(data+"\n \n",getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = RestaurantUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
                for (int i=0;i<jsonArray.length();i++){

                    addRows(table,RestaurantUtils.getMapFromJSON(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total : "+requestMap.get("total")+"\n"
                        +"Thank You for Visiting !.",getFont("Data"));
                document.add(footer);
                document.close();

                return new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}",HttpStatus.OK);
            }
            return RestaurantUtils.getResponseEntity("Required Data is not Found",HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.Something_Went_Wrong, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows ");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double)data.get("price")));
        table.addCell(Double.toString((Double)data.get("total")));
    }


    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");
        Stream.of("Name","Category","Quantity","Price","Sub Total")
                .forEach(columnTitle ->{
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private Font getFont(String type) {
        log.info("Inside getFont");
        switch (type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;

            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;

            default:return new Font();
        }

    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577,825,18,15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBackgroundColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("total")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(bill);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") && requestMap.containsKey("productDetails")&&
                requestMap.containsKey("total");
    }
}
