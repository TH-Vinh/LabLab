package com.example.springmvc.service.impl;

import com.example.springmvc.dto.AssetResponseDTO;
import com.example.springmvc.dto.ChemicalResponseDTO;
import com.example.springmvc.service.ExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Override
    public byte[] exportChemicalsToExcel(List<ChemicalResponseDTO> chemicals) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Hóa chất");
            
            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Mã hóa chất", "Tên", "Công thức", "Đơn vị", "Số lượng", 
                                "Nhà cung cấp", "Bao bì", "Vị trí lưu trữ", "Giá gốc"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Tạo data rows
            int rowNum = 1;
            for (ChemicalResponseDTO chemical : chemicals) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(chemical.getItemCode() != null ? chemical.getItemCode() : "");
                row.createCell(1).setCellValue(chemical.getName() != null ? chemical.getName() : "");
                row.createCell(2).setCellValue(chemical.getFormula() != null ? chemical.getFormula() : "");
                row.createCell(3).setCellValue(chemical.getUnit() != null ? chemical.getUnit() : "");
                row.createCell(4).setCellValue(chemical.getCurrentQuantity() != null ? 
                    chemical.getCurrentQuantity().doubleValue() : 0);
                row.createCell(5).setCellValue(chemical.getSupplier() != null ? chemical.getSupplier() : "");
                row.createCell(6).setCellValue(chemical.getPackaging() != null ? chemical.getPackaging() : "");
                row.createCell(7).setCellValue(chemical.getStorageLocation() != null ? 
                    chemical.getStorageLocation() : "");
                row.createCell(8).setCellValue(chemical.getOriginalPrice() != null ? 
                    chemical.getOriginalPrice().doubleValue() : 0);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] exportAssetsToExcel(List<AssetResponseDTO> assets) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Thiết bị");
            
            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Mã thiết bị", "Tên", "Đơn vị", "Năm sử dụng", "Trạng thái",
                                "Số lượng kế toán", "Số lượng tồn kho", "Nhà cung cấp", 
                                "Vị trí lưu trữ", "Giá gốc", "Giá trị còn lại"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Tạo data rows
            int rowNum = 1;
            for (AssetResponseDTO asset : assets) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(asset.getItemCode() != null ? asset.getItemCode() : "");
                row.createCell(1).setCellValue(asset.getName() != null ? asset.getName() : "");
                row.createCell(2).setCellValue(asset.getUnit() != null ? asset.getUnit() : "");
                row.createCell(3).setCellValue(asset.getYearInUse() != null ? asset.getYearInUse() : 0);
                row.createCell(4).setCellValue(asset.getStatusDetail() != null ? asset.getStatusDetail() : "");
                row.createCell(5).setCellValue(asset.getAccountingQuantity() != null ? 
                    asset.getAccountingQuantity() : 0);
                row.createCell(6).setCellValue(asset.getInventoryQuantity() != null ? 
                    asset.getInventoryQuantity() : 0);
                row.createCell(7).setCellValue(asset.getSupplier() != null ? asset.getSupplier() : "");
                row.createCell(8).setCellValue(asset.getStorageLocation() != null ? 
                    asset.getStorageLocation() : "");
                row.createCell(9).setCellValue(asset.getOriginalPrice() != null ? 
                    asset.getOriginalPrice().doubleValue() : 0);
                row.createCell(10).setCellValue(asset.getResidualValue() != null ? 
                    asset.getResidualValue().doubleValue() : 0);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ChemicalResponseDTO> importChemicalsFromExcel(MultipartFile file) {
        List<ChemicalResponseDTO> chemicals = new ArrayList<>();
        
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File không được để trống!");
        }
        
        try {
            Workbook workbook = null;
            String fileName = file.getOriginalFilename();
            System.out.println("Đang đọc file: " + fileName);
            
            try {
                if (fileName != null && fileName.endsWith(".xls")) {
                    workbook = new HSSFWorkbook(file.getInputStream());
                } else {
                    workbook = new XSSFWorkbook(file.getInputStream());
                }
            } catch (Exception e) {
                throw new RuntimeException("Không thể đọc file Excel. Vui lòng kiểm tra định dạng file: " + e.getMessage(), e);
            }
            
            try {
                // Đảm bảo đọc sheet đầu tiên (index 0)
                if (workbook.getNumberOfSheets() == 0) {
                    throw new RuntimeException("File Excel không có sheet nào!");
                }
                
                Sheet sheet = workbook.getSheetAt(0);
                if (sheet == null) {
                    throw new RuntimeException("Sheet đầu tiên không tồn tại!");
                }
                
                System.out.println("Đang đọc sheet: " + sheet.getSheetName() + " (Sheet đầu tiên)");
                System.out.println("Số dòng trong sheet: " + (sheet.getLastRowNum() + 1));
                
                // Tìm header row (có thể có merged cells, header có thể ở row 1 hoặc 2)
                int headerRowIndex = -1;
                Row headerRow = null;
                boolean isNewFormat = false;
                
                // Tìm header trong 5 dòng đầu
                for (int i = 0; i <= Math.min(5, sheet.getLastRowNum()); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    
                    String cell0 = getCellValueAsString(row.getCell(0));
                    String cell1 = getCellValueAsString(row.getCell(1));
                    
                    // Kiểm tra nếu có "STT" hoặc "TT" ở cột 0 và "Tên" ở cột 1
                    if (cell0 != null && (cell0.trim().equalsIgnoreCase("STT") || cell0.trim().equalsIgnoreCase("TT") || 
                        cell0.contains("STT") || cell0.contains("TT"))) {
                        if (cell1 != null && (cell1.contains("Tên hóa chất") || cell1.contains("Tên") || 
                            cell1.contains("Chemical") || cell1.contains("Name"))) {
                            headerRowIndex = i;
                            headerRow = row;
                            isNewFormat = true;
                            break;
                        }
                    }
                }
                
                // Nếu không tìm thấy, thử format cũ
                if (headerRowIndex == -1) {
                    headerRowIndex = 0;
                    headerRow = sheet.getRow(0);
                }
                
                // Bỏ qua header row và bắt đầu từ row sau header
                int startRow = headerRowIndex + 1;
                for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    
                    // Bỏ qua dòng trống hoặc dòng không có dữ liệu
                    Object cell0 = getCellValue(row.getCell(0));
                    Object cell1 = getCellValue(row.getCell(1));
                    if ((cell0 == null || cell0.toString().trim().isEmpty()) && 
                        (cell1 == null || cell1.toString().trim().isEmpty())) {
                        continue;
                    }
                    
                    ChemicalResponseDTO chemical = new ChemicalResponseDTO();
                
                    try {
                        if (isNewFormat) {
                            // Format mới: STT, Tên hóa chất, Tổng khối lượng, Đơn vị, Khối lượng (Kg-L), Đơn vị (Kg-L), Nơi lưu chứa, Ghi chú
                            // Cột 0: STT - bỏ qua (có thể là số thứ tự)
                            // Cột 1: Tên hóa chất -> name
                            if (cell1 != null) {
                                String name = cell1.toString().trim();
                                if (!name.isEmpty()) {
                                    chemical.setName(name);
                                    // Tạo mã từ tên nếu chưa có
                                    if (chemical.getItemCode() == null || chemical.getItemCode().isEmpty()) {
                                        chemical.setItemCode(generateCodeFromName(name));
                                    }
                                }
                            }
                            
                            // Cột 2: Tổng khối lượng -> currentQuantity
                            Object cell2 = getCellValue(row.getCell(2));
                            if (cell2 != null && !cell2.toString().trim().isEmpty()) {
                                try {
                                    double qtyValue = cell2 instanceof Number ? 
                                        ((Number) cell2).doubleValue() : Double.parseDouble(cell2.toString().replace(",", ""));
                                    chemical.setCurrentQuantity(new BigDecimal(qtyValue));
                                } catch (NumberFormatException e) {
                                    chemical.setCurrentQuantity(java.math.BigDecimal.ZERO);
                                }
                            } else {
                                chemical.setCurrentQuantity(java.math.BigDecimal.ZERO);
                            }
                            
                            // Cột 3: Đơn vị -> unit
                            Object cell3 = getCellValue(row.getCell(3));
                            if (cell3 != null) {
                                String unit = cell3.toString().trim();
                                if (!unit.isEmpty()) {
                                    chemical.setUnit(unit);
                                }
                            }
                            
                            // Cột 6: Nơi lưu chứa -> storageLocation
                            Object cell6 = getCellValue(row.getCell(6));
                            if (cell6 != null) {
                                String location = cell6.toString().trim();
                                if (!location.isEmpty()) {
                                    chemical.setStorageLocation(location);
                                }
                            }
                        } else {
                            // Format cũ: Mã, Tên, Công thức, Đơn vị, Số lượng, Nhà cung cấp, Bao bì, Vị trí, Giá gốc
                            if (cell0 != null) {
                                chemical.setItemCode(cell0.toString().trim());
                            }
                            
                            if (cell1 != null) {
                                chemical.setName(cell1.toString().trim());
                            }
                        
                        Object cell2 = getCellValue(row.getCell(2));
                        if (cell2 != null) {
                            chemical.setFormula(cell2.toString().trim());
                        }
                        
                        Object cell3 = getCellValue(row.getCell(3));
                        if (cell3 != null) {
                            chemical.setUnit(cell3.toString().trim());
                        }
                        
                        Object cell4 = getCellValue(row.getCell(4));
                        if (cell4 != null) {
                            try {
                                double qtyValue = cell4 instanceof Number ? 
                                    ((Number) cell4).doubleValue() : Double.parseDouble(cell4.toString());
                                chemical.setCurrentQuantity(new BigDecimal(qtyValue));
                            } catch (NumberFormatException e) {
                                chemical.setCurrentQuantity(java.math.BigDecimal.ZERO);
                            }
                        } else {
                            chemical.setCurrentQuantity(java.math.BigDecimal.ZERO);
                        }
                        
                        Object cell5 = getCellValue(row.getCell(5));
                        if (cell5 != null) {
                            chemical.setSupplier(cell5.toString().trim());
                        }
                        
                        Object cell6 = getCellValue(row.getCell(6));
                        if (cell6 != null) {
                            chemical.setPackaging(cell6.toString().trim());
                        }
                        
                        Object cell7 = getCellValue(row.getCell(7));
                        if (cell7 != null) {
                            chemical.setStorageLocation(cell7.toString().trim());
                        }
                        
                        Object cell8 = getCellValue(row.getCell(8));
                        if (cell8 != null) {
                            try {
                                double priceValue = cell8 instanceof Number ? 
                                    ((Number) cell8).doubleValue() : Double.parseDouble(cell8.toString());
                                chemical.setOriginalPrice(new BigDecimal(priceValue));
                            } catch (NumberFormatException e) {
                                chemical.setOriginalPrice(java.math.BigDecimal.ZERO);
                            }
                        } else {
                            chemical.setOriginalPrice(java.math.BigDecimal.ZERO);
                        }
                        }
                        
                        // Chỉ thêm nếu có tên (bắt buộc)
                        if (chemical.getName() != null && !chemical.getName().isEmpty()) {
                            // Nếu chưa có mã, tạo mã từ tên
                            if (chemical.getItemCode() == null || chemical.getItemCode().isEmpty()) {
                                chemical.setItemCode(generateCodeFromName(chemical.getName()));
                            }
                            chemicals.add(chemical);
                            System.out.println("Đã thêm hóa chất: " + chemical.getName() + " (Mã: " + chemical.getItemCode() + ")");
                        }
                    } catch (Exception e) {
                        // Bỏ qua dòng lỗi và tiếp tục
                        System.err.println("Lỗi khi đọc dòng " + (i + 1) + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                System.out.println("Tổng số hóa chất đã đọc được: " + chemicals.size());
            } finally {
                if (workbook != null) {
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        System.err.println("Lỗi khi đóng workbook: " + e.getMessage());
                    }
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e; // Re-throw để giữ nguyên thông báo lỗi
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof IOException) {
                throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage(), e);
            }
            throw new RuntimeException("Lỗi khi xử lý file Excel: " + e.getMessage(), e);
        }
        
        return chemicals;
    }
    
    private String generateCodeFromName(String name) {
        // Tạo mã từ tên: lấy 3-5 ký tự đầu và thêm số ngẫu nhiên
        if (name == null || name.isEmpty()) {
            return "HC" + (System.currentTimeMillis() % 10000);
        }
        try {
            // Lấy các ký tự chữ và số đầu tiên
            String code = name.substring(0, Math.min(10, name.length()))
                    .replaceAll("[^A-Za-z0-9]", "")
                    .toUpperCase();
            if (code.isEmpty()) {
                code = "HC";
            } else if (code.length() > 8) {
                code = code.substring(0, 8);
            }
            // Thêm số ngẫu nhiên để tránh trùng
            long random = Math.abs(System.currentTimeMillis() % 10000) + (int)(Math.random() * 1000);
            return code + random;
        } catch (Exception e) {
            return "HC" + (System.currentTimeMillis() % 10000);
        }
    }
    
    private String getCellValueAsString(Cell cell) {
        Object value = getCellValue(cell);
        return value != null ? value.toString() : null;
    }

    @Override
    public List<AssetResponseDTO> importAssetsFromExcel(MultipartFile file) {
        List<AssetResponseDTO> assets = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File không được để trống!");
        }

        Workbook workbook = null;
        String fileName = file.getOriginalFilename();
        System.out.println("Đang đọc file thiết bị: " + fileName);

        try {
            if (fileName != null && fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file Excel. Vui lòng kiểm tra định dạng file: " + e.getMessage(), e);
        }

        try {
            if (workbook.getNumberOfSheets() == 0) {
                throw new RuntimeException("File Excel không có sheet nào!");
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("Sheet đầu tiên không tồn tại!");
            }

            System.out.println("Đang đọc sheet: " + sheet.getSheetName() + " (Sheet đầu tiên)");
            System.out.println("Số dòng trong sheet: " + (sheet.getLastRowNum() + 1));

            int headerRowIndex = -1;
            Row headerRow = null;
            boolean isTSCDFormat = false; // Format TSCĐ: TT, Mã TSCĐ, Tên TSCĐ, ...
            boolean isNewFormat = false; // Format cc-dc: TT, Mã cc-dc, ...

            // Tìm header row trong 5 dòng đầu tiên
            for (int i = 0; i <= Math.min(5, sheet.getLastRowNum()); i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow == null) continue;

                String cell0 = getCellValueAsString(currentRow.getCell(0));
                String cell1 = getCellValueAsString(currentRow.getCell(1));
                String cell2 = getCellValueAsString(currentRow.getCell(2));

                // Format TSCĐ: TT, Mã TSCĐ, Tên TSCĐ
                if (cell0 != null && (cell0.trim().equalsIgnoreCase("TT") || cell0.trim().equalsIgnoreCase("STT"))) {
                    if (cell1 != null && (cell1.contains("Mã TSCĐ") || cell1.contains("Mã"))) {
                        if (cell2 != null && (cell2.contains("Tên TSCĐ") || cell2.contains("Tên"))) {
                            headerRowIndex = i;
                            headerRow = currentRow;
                            isTSCDFormat = true;
                            System.out.println("Nhận diện format TSCĐ tại dòng header: " + (i + 1));
                            break;
                        }
                    }
                }
                // Format cc-dc: TT, Mã cc-dc
                else if (cell0 != null && (cell0.contains("TT") || cell0.contains("STT"))) {
                    if (cell1 != null && cell1.contains("Mã")) {
                        headerRowIndex = i;
                        headerRow = currentRow;
                        isNewFormat = true;
                        System.out.println("Nhận diện format cc-dc tại dòng header: " + (i + 1));
                        break;
                    }
                }
            }

            if (headerRowIndex == -1) {
                throw new RuntimeException("Không tìm thấy dòng header hợp lệ trong 5 dòng đầu tiên của sheet!");
            }
            
            int startRow = headerRowIndex + 1;
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Object cell0Val = getCellValue(row.getCell(0));
                Object cell1Val = getCellValue(row.getCell(1));
                if ((cell0Val == null || cell0Val.toString().trim().isEmpty()) &&
                    (cell1Val == null || cell1Val.toString().trim().isEmpty())) {
                    System.out.println("Bỏ qua dòng trống hoặc không có dữ liệu ở dòng: " + (i + 1));
                    continue;
                }

                AssetResponseDTO asset = new AssetResponseDTO();

                try {
                    if (isTSCDFormat) {
                        // Format TSCĐ: TT, Mã TSCĐ, Tên TSCĐ, Năm đưa vào sử dụng, Đơn vị tính, 
                        // Theo sổ kế toán, Nguyên giá, GTCL, Nơi sử dụng, Xuất xứ
                        // Cột 0: TT - bỏ qua
                        // Cột 1: Mã TSCĐ -> itemCode
                        Object cell1 = getCellValue(row.getCell(1));
                        if (cell1 != null) {
                            asset.setItemCode(cell1.toString().trim());
                        }

                        // Cột 2: Tên TSCĐ -> name
                        Object cell2 = getCellValue(row.getCell(2));
                        if (cell2 != null) {
                            asset.setName(cell2.toString().trim());
                        }

                        // Cột 3: Năm đưa vào sử dụng -> yearInUse
                        Object cell3 = getCellValue(row.getCell(3));
                        if (cell3 != null && !cell3.toString().trim().isEmpty()) {
                            try {
                                int yearValue = cell3 instanceof Number ?
                                    ((Number) cell3).intValue() : Integer.parseInt(cell3.toString().trim());
                                asset.setYearInUse(yearValue);
                            } catch (NumberFormatException e) {
                                System.err.println("Lỗi parse năm ở dòng " + (i + 1) + ", cột 3: " + cell3 + " - " + e.getMessage());
                                asset.setYearInUse(null);
                            }
                        }

                        // Cột 4: Đơn vị tính -> unit
                        Object cell4 = getCellValue(row.getCell(4));
                        if (cell4 != null) {
                            asset.setUnit(cell4.toString().trim());
                        }

                        // Cột 5: Theo sổ kế toán -> accountingQuantity
                        Object cell5 = getCellValue(row.getCell(5));
                        if (cell5 != null && !cell5.toString().trim().isEmpty()) {
                            try {
                                int qtyValue = cell5 instanceof Number ?
                                    ((Number) cell5).intValue() : Integer.parseInt(cell5.toString().replace(",", "").trim());
                                asset.setAccountingQuantity(qtyValue);
                            } catch (NumberFormatException e) {
                                System.err.println("Lỗi parse số lượng ở dòng " + (i + 1) + ", cột 5: " + cell5 + " - " + e.getMessage());
                                asset.setAccountingQuantity(0);
                            }
                        } else {
                            asset.setAccountingQuantity(0);
                        }

                        // Cột 6: Nguyên giá (đồng) -> originalPrice
                        Object cell6 = getCellValue(row.getCell(6));
                        if (cell6 != null && !cell6.toString().trim().isEmpty()) {
                            try {
                                double priceValue = cell6 instanceof Number ?
                                    ((Number) cell6).doubleValue() : Double.parseDouble(cell6.toString().replace(",", "").trim());
                                asset.setOriginalPrice(new BigDecimal(priceValue));
                            } catch (NumberFormatException e) {
                                System.err.println("Lỗi parse giá ở dòng " + (i + 1) + ", cột 6: " + cell6 + " - " + e.getMessage());
                                asset.setOriginalPrice(java.math.BigDecimal.ZERO);
                            }
                        } else {
                            asset.setOriginalPrice(java.math.BigDecimal.ZERO);
                        }

                        // Cột 7: GTCL (đồng) -> residualValue
                        Object cell7 = getCellValue(row.getCell(7));
                        if (cell7 != null && !cell7.toString().trim().isEmpty()) {
                            try {
                                double value = cell7 instanceof Number ?
                                    ((Number) cell7).doubleValue() : Double.parseDouble(cell7.toString().replace(",", "").trim());
                                asset.setResidualValue(new BigDecimal(value));
                            } catch (NumberFormatException e) {
                                System.err.println("Lỗi parse GTCL ở dòng " + (i + 1) + ", cột 7: " + cell7 + " - " + e.getMessage());
                                asset.setResidualValue(java.math.BigDecimal.ZERO);
                            }
                        } else {
                            asset.setResidualValue(java.math.BigDecimal.ZERO);
                        }

                        // Cột 8: Nơi sử dụng -> storageLocation
                        Object cell8 = getCellValue(row.getCell(8));
                        if (cell8 != null) {
                            asset.setStorageLocation(cell8.toString().trim());
                        }

                        // Cột 9: Xuất xứ -> supplier
                        Object cell9 = getCellValue(row.getCell(9));
                        if (cell9 != null) {
                            asset.setSupplier(cell9.toString().trim());
                        }
                    } else if (isNewFormat) {
                        // Format mới: TT, Mã cc-dc, Tên, Nước sản xuất, Năm đưa vào SD, Cấp hạng, 
                        // Sổ sách (Số lượng, Thành tiền), Kiểm kê (Số lượng, Thành tiền), Chênh lệch, Đề nghị, Ghi chú
                        // Cột 0: TT - bỏ qua
                        // Cột 1: Mã cc-dc -> itemCode
                        Object cell1 = getCellValue(row.getCell(1));
                        if (cell1 != null) {
                            asset.setItemCode(cell1.toString().trim());
                        }
                        
                        // Cột 2: Tên, đặc điểm, ký hiệu cc-dc -> name
                        Object cell2 = getCellValue(row.getCell(2));
                        if (cell2 != null) {
                            asset.setName(cell2.toString().trim());
                        }
                        
                        // Cột 3: Nước sản xuất -> supplier
                        Object cell3 = getCellValue(row.getCell(3));
                        if (cell3 != null) {
                            asset.setSupplier(cell3.toString().trim());
                        }
                        
                        // Cột 4: Năm đưa vào SD -> yearInUse
                        Object cell4 = getCellValue(row.getCell(4));
                        if (cell4 != null) {
                            try {
                                int yearValue = cell4 instanceof Number ? 
                                    ((Number) cell4).intValue() : Integer.parseInt(cell4.toString());
                                asset.setYearInUse(yearValue);
                            } catch (NumberFormatException e) {
                                asset.setYearInUse(null);
                            }
                        }
                        
                        // Cột 5: Cấp hạng thông số kỹ thuật -> statusDetail (nếu có)
                        Object cell5 = getCellValue(row.getCell(5));
                        if (cell5 != null && !cell5.toString().trim().isEmpty()) {
                            asset.setStatusDetail(cell5.toString().trim());
                        }
                        
                        // Cột 6: Sổ sách - Số lượng -> accountingQuantity
                        Object cell6 = getCellValue(row.getCell(6));
                        if (cell6 != null) {
                            try {
                                int qtyValue = cell6 instanceof Number ? 
                                    ((Number) cell6).intValue() : Integer.parseInt(cell6.toString());
                                asset.setAccountingQuantity(qtyValue);
                            } catch (NumberFormatException e) {
                                asset.setAccountingQuantity(0);
                            }
                        } else {
                            asset.setAccountingQuantity(0);
                        }
                        
                        // Cột 7: Sổ sách - Thành tiền -> originalPrice
                        Object cell7 = getCellValue(row.getCell(7));
                        if (cell7 != null) {
                            try {
                                double priceValue = cell7 instanceof Number ? 
                                    ((Number) cell7).doubleValue() : Double.parseDouble(cell7.toString());
                                asset.setOriginalPrice(new BigDecimal(priceValue));
                            } catch (NumberFormatException e) {
                                asset.setOriginalPrice(java.math.BigDecimal.ZERO);
                            }
                        } else {
                            asset.setOriginalPrice(java.math.BigDecimal.ZERO);
                        }
                        
                        // Cột 8: Kiểm kê - Số lượng -> inventoryQuantity
                        Object cell8 = getCellValue(row.getCell(8));
                        if (cell8 != null) {
                            try {
                                int qtyValue = cell8 instanceof Number ? 
                                    ((Number) cell8).intValue() : Integer.parseInt(cell8.toString());
                                asset.setInventoryQuantity(qtyValue);
                            } catch (NumberFormatException e) {
                                asset.setInventoryQuantity(0);
                            }
                        } else {
                            asset.setInventoryQuantity(0);
                        }
                        
                        // Cột 9: Kiểm kê - Thành tiền -> có thể dùng làm residualValue
                        Object cell9 = getCellValue(row.getCell(9));
                        if (cell9 != null) {
                            try {
                                double value = cell9 instanceof Number ? 
                                    ((Number) cell9).doubleValue() : Double.parseDouble(cell9.toString());
                                asset.setResidualValue(new BigDecimal(value));
                            } catch (NumberFormatException e) {
                                asset.setResidualValue(java.math.BigDecimal.ZERO);
                            }
                        } else {
                            asset.setResidualValue(java.math.BigDecimal.ZERO);
                        }
                        
                        // Cột 12: Đề nghị thanh lý -> có thể thêm vào statusDetail
                        Object cell12 = getCellValue(row.getCell(12));
                        if (cell12 != null && !cell12.toString().trim().isEmpty()) {
                            String status = asset.getStatusDetail() != null ? asset.getStatusDetail() : "";
                            if (!status.isEmpty()) status += " - ";
                            status += "Đề nghị thanh lý: " + cell12.toString().trim();
                            asset.setStatusDetail(status);
                        }
                    } else {
                        // Format cũ: Mã, Tên, Đơn vị, Năm sử dụng, Trạng thái, Số lượng kế toán, Số lượng tồn kho, Nhà cung cấp, Vị trí, Giá gốc, Giá trị còn lại
                        Object cell0 = getCellValue(row.getCell(0));
                        if (cell0 != null) {
                            asset.setItemCode(cell0.toString().trim());
                        }
                        
                        Object cell1 = getCellValue(row.getCell(1));
                        if (cell1 != null) {
                            asset.setName(cell1.toString().trim());
                        }
                        
                        Object cell2 = getCellValue(row.getCell(2));
                        if (cell2 != null) {
                            asset.setUnit(cell2.toString().trim());
                        }
                        
                        Object cell3 = getCellValue(row.getCell(3));
                        if (cell3 != null) {
                            try {
                                int yearValue = cell3 instanceof Number ? 
                                    ((Number) cell3).intValue() : Integer.parseInt(cell3.toString());
                                asset.setYearInUse(yearValue);
                            } catch (NumberFormatException e) {
                                asset.setYearInUse(null);
                            }
                        }
                        
                        Object cell4 = getCellValue(row.getCell(4));
                        if (cell4 != null) {
                            asset.setStatusDetail(cell4.toString().trim());
                        }
                        
                        Object cell5 = getCellValue(row.getCell(5));
                        if (cell5 != null) {
                            try {
                                int qtyValue = cell5 instanceof Number ? 
                                    ((Number) cell5).intValue() : Integer.parseInt(cell5.toString());
                                asset.setAccountingQuantity(qtyValue);
                            } catch (NumberFormatException e) {
                                asset.setAccountingQuantity(0);
                            }
                        } else {
                            asset.setAccountingQuantity(0);
                        }
                        
                        Object cell6 = getCellValue(row.getCell(6));
                        if (cell6 != null) {
                            try {
                                int qtyValue = cell6 instanceof Number ? 
                                    ((Number) cell6).intValue() : Integer.parseInt(cell6.toString());
                                asset.setInventoryQuantity(qtyValue);
                            } catch (NumberFormatException e) {
                                asset.setInventoryQuantity(0);
                            }
                        } else {
                            asset.setInventoryQuantity(0);
                        }
                        
                        Object cell7 = getCellValue(row.getCell(7));
                        if (cell7 != null) {
                            asset.setSupplier(cell7.toString().trim());
                        }
                        
                        Object cell8 = getCellValue(row.getCell(8));
                        if (cell8 != null) {
                            asset.setStorageLocation(cell8.toString().trim());
                        }
                        
                        Object cell9 = getCellValue(row.getCell(9));
                        if (cell9 != null) {
                            try {
                                double priceValue = cell9 instanceof Number ? 
                                    ((Number) cell9).doubleValue() : Double.parseDouble(cell9.toString());
                                asset.setOriginalPrice(new BigDecimal(priceValue));
                            } catch (NumberFormatException e) {
                                asset.setOriginalPrice(java.math.BigDecimal.ZERO);
                            }
                        } else {
                            asset.setOriginalPrice(java.math.BigDecimal.ZERO);
                        }
                        
                        Object cell10 = getCellValue(row.getCell(10));
                        if (cell10 != null) {
                            try {
                                double value = cell10 instanceof Number ? 
                                    ((Number) cell10).doubleValue() : Double.parseDouble(cell10.toString());
                                asset.setResidualValue(new BigDecimal(value));
                            } catch (NumberFormatException e) {
                                asset.setResidualValue(java.math.BigDecimal.ZERO);
                            }
                        } else {
                            asset.setResidualValue(java.math.BigDecimal.ZERO);
                        }
                    }
                    
                    // Chỉ thêm nếu có mã và tên
                    if (asset.getItemCode() != null && !asset.getItemCode().isEmpty() &&
                        asset.getName() != null && !asset.getName().isEmpty()) {
                        assets.add(asset);
                        System.out.println("Đã thêm thiết bị: " + asset.getName() + " (Mã: " + asset.getItemCode() + ")");
                    } else {
                        System.err.println("Bỏ qua dòng " + (i + 1) + " do thiếu mã hoặc tên thiết bị.");
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi đọc dòng " + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Tổng số thiết bị đã đọc được: " + assets.size());
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    System.err.println("Lỗi khi đóng workbook: " + e.getMessage());
                }
            }
        }
        
        return assets;
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
