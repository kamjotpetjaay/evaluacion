package org.example;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EvaluacionBatchGenerator {

    private static final String[] headers = {"Id del empleado", "Nombre", "Apellido paterno", "Apellido materno", "Sexo", "Fecha de nacimiento", "Teléfono celular", "Puesto", "Salario"};
    private static final String query = "SELECT empleado.id_empleado, empleado.nombre, empleado.apellido1, empleado.apellido2, " +
            "empleado.sexo, empleado.f_nacimiento, empleado.tel_celular, " +
            "salario.descripcion, salario.salario " +
            "FROM empleado " +
            "LEFT JOIN salario ON empleado.id_puesto = salario.id_puesto";
    private static final String[] columns = {"id_empleado", "nombre", "apellido1", "apellido2", "sexo", "f_nacimiento", "tel_celular", "descripcion", "salario"};


    public static void main(String[] args) {
        try {
            //Obtenemos las properties de la base de datos para utilizarlo en la conexión
            Properties properties = new Properties();
            properties.load(EvaluacionBatchGenerator.class.getClassLoader().getResourceAsStream("application.properties"));
            String urlDB = properties.getProperty("db.url");
            String userDB = properties.getProperty("db.username");
            String passwdDB = properties.getProperty("db.password");

            Connection connection = DriverManager.getConnection(urlDB, userDB, passwdDB);

            //Creamos los métodos para generar los reportes
            generatePdfReport(connection);
            generateExcelReport(connection);
            generateTxtReport(connection);

            //Empaquetar los archivos en un zip
            zipReports();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generatePdfReport(Connection connection) {
        String pdfFileName = "reporte.pdf";
        try (FileOutputStream fos = new FileOutputStream(pdfFileName)) {
            Document document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Reporte de Empleados", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(9); // 9 columnas de la tabla
            table.setWidthPercentage(100); // Ajusta al tamaño de la página
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6f);
                table.addCell(cell);
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            Font cellFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            while (resultSet.next()) {
                for (String header : columns) {
                    PdfPCell cell = new PdfPCell(new Phrase(resultSet.getString(header), cellFont));
                    cell.setPadding(3f);
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();
            System.out.println("PDF generado!!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateExcelReport(Connection connection) {
        String excelFileName = "reporte.xls";
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(excelFileName)) {
            Sheet sheet = workbook.createSheet("Reporte de Empleados");

            // Crear fila de encabezado
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // datos de las filas
            int rowNum = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < columns.length; i++) {
                    row.createCell(i).setCellValue(resultSet.getString(columns[i]));
                }
            }

            workbook.write(fos);
            System.out.println("XLS generado!!!!!!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error al generar el excel");
        }
    }
    //Generar el reporte en texto
    private static void generateTxtReport(Connection connection) {
        String txtFileName = "reporte.txt";
        try (FileWriter writer = new FileWriter(txtFileName)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // encabezados
            writer.write("Id\tNombre\tApellido paterno\tApellido materno\tSexo\tFecha de nacimiento\tTeléfono celular\tPuesto\tSalario \n");

            while (resultSet.next()) {
                writer.write(resultSet.getString("id_empleado") + "\t" +
                        String.format("%-15s",resultSet.getString("nombre")) +
                        String.format("%-15s",resultSet.getString("apellido1")) +
                        String.format("%-15s",resultSet.getString("apellido2")) + "\t" +
                        resultSet.getString("sexo") + "\t" +
                        resultSet.getString("f_nacimiento") + "\t" +
                        resultSet.getString("tel_celular") + "\t" +
                        String.format("%-20s",resultSet.getString("descripcion")) + "\t" +
                        resultSet.getString("salario") + "\n");
            }

            System.out.println("TXT generado.");

        } catch (Exception e) {
            System.out.println("Error al generar el texto "+e.getMessage());
            e.printStackTrace();
        }
    }

    //Método que genera el archivo zip usando los archivos previamente creados
    private static void zipReports() throws IOException {
        try (FileOutputStream fos = new FileOutputStream("reportes.zip");
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            addToZipFile("reporte.pdf", zos);
            addToZipFile("reporte.xls", zos);
            addToZipFile("reporte.txt", zos);
        }
        System.out.println("Empaquedado listo!!!");
    }

    //Método para agregar un archivo a un zip output
    private static void addToZipFile(String fileName, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            zos.closeEntry();
        }
    }

}