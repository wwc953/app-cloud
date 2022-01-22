package com.example.appuser.execl;

import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ExeclController {

    @ApiOperation(value = "导入execl")
    @RequestMapping(value = "/importExecl", method = RequestMethod.POST)
    public List<Map<String, String>> importExecl(@RequestParam("file") MultipartFile file) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();
        XSSFWorkbook sheets = new XSSFWorkbook(file.getInputStream());
        //获取sheet
//        XSSFSheet sheet = sheets.getSheet("qqq");
        XSSFSheet sheet = sheets.getSheetAt(0);

        //获取行数
        int rowSize = sheet.getPhysicalNumberOfRows();
        XSSFRow title = sheet.getRow(0);

        for (int i = 1; i < rowSize; i++) {
            Map<String, String> obj = new HashMap<>();
            XSSFRow row = sheet.getRow(i);
            int columnSize = row.getPhysicalNumberOfCells();

            for (int j = 0; j < columnSize; j++) {
                String cell = row.getCell(j).toString();
                obj.put(title.getCell(j).toString(), cell);
            }
            result.add(obj);
        }
        return result;
    }


}
