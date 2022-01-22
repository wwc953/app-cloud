package com.example.appuser.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class PdfController {

    public static final String default_code = "ISO-8859-1";

    @ApiOperation(value = "远程调用文件下载PDF")
    @RequestMapping(value = "/pdf", method = RequestMethod.POST)
    public void pdf(HttpServletResponse response) throws Exception {
        String result = generatePDF();
        String decode = URLDecoder.decode(result, default_code);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(decode.getBytes(default_code));
        response.setContentType("application/pdf;charset=UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode("test", "UTF-8") + ".pdf");
        response.getOutputStream().write(IOUtils.toByteArray(inputStream));
    }

    public String generatePDF() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(new Rectangle(PageSize.A4));
        PdfWriter.getInstance(doc, baos); //PDF对象写入流
        doc.open();

        //解决中文问题
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font titltFont = new Font(bfChinese, 16, Font.BOLD);
        Font headFont = new Font(bfChinese, 14, Font.BOLD);
        Font keyFont = new Font(bfChinese, 12, Font.BOLD);
        Font testFont = new Font(bfChinese, 10, Font.NORMAL);

        Paragraph title = new Paragraph("我是标题" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), titltFont);
        title.setAlignment(Element.ALIGN_CENTER);//居中
        doc.add(title);

        Paragraph baseInfo = new Paragraph("\nA.用户信息" ,headFont);
        Paragraph zuobiao = new Paragraph("A1.坐标" ,keyFont);
        doc.add(baseInfo);
        doc.add(zuobiao);

        Paragraph p1 = new Paragraph("经度",keyFont);
        Chunk chunk = new Chunk("123.33443", keyFont);
        chunk.setUnderline(0.5f,-2f);
        p1.add(chunk);
        doc.add(p1);

        Paragraph hc = new Paragraph("\n",headFont);
        doc.add(hc);


        PdfPTable table = createTable(new float[]{10, 60, 60});
        table.addCell(createCell("序号", keyFont, Element.ALIGN_CENTER));
        table.addCell(createCell("姓名", keyFont, Element.ALIGN_CENTER));
        table.addCell(createCell("年龄", keyFont, Element.ALIGN_CENTER));
        for (int i = 0; i < 3; i++) {
            table.addCell(createCell(i + "", testFont));
            table.addCell(createCell("张三" + i, testFont));
            table.addCell(createCell("12" + i, testFont));
        }

        doc.add(table);

        if (doc != null) {
            doc.close();
        }

        String result = new String(baos.toByteArray(), default_code);
        String encode = URLEncoder.encode(result, default_code);
        return encode;
    }

    public static PdfPTable createTable(float[] widths) {
        PdfPTable table = new PdfPTable(widths);
        table.setTotalWidth(520);
        table.setLockedWidth(true);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setBorder(1);
        return table;
    }

    public static PdfPCell createCell(String value, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    public static PdfPCell createCell(String value, Font font, int align) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    public static PdfPCell createCell(String value, Font font, int align, int colspan, boolean boderFlag) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setPhrase(new Phrase(value, font));
        cell.setPadding(3.0f);
        if (!boderFlag) {
            cell.setBorder(0);
            cell.setPaddingTop(15.0f);
            cell.setPaddingBottom(8.0f);
        } else {
            cell.setBorder(0);
            cell.setPaddingTop(0.0f);
            cell.setPaddingBottom(15.0f);
        }
        return cell;
    }
}
