package kr.mk2.stringformatter;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class AppController implements InitializingBean, DisposableBean {
  Configuration freeMarker;


  @RequestMapping("")
  public Object indexPage(@RequestParam(defaultValue = "tab") String delimiter,
                          String data,
                          MultipartFile file,
                          String format) {
    ModelAndView mv = new ModelAndView("index");
    mv.addObject("data", data);
    mv.addObject("format", format);
    mv.addObject("delimiter", delimiter);

    switch (delimiter) {
      case "tab":
        delimiter = "\t";
        break;
      case "comma":
        delimiter = ",";
        break;
      case "space":
        delimiter = " ";
        break;
      case "xlsx":
        delimiter = null;
        break;
      default:
        delimiter = "\t";
    }

    try {
      StringBuilder result = new StringBuilder();
      if (delimiter == null) {
        fromExcel(file, format, result);
      } else {
        fromPlainText(data, delimiter, format, result);
      }
      mv.addObject("result", result);

    } catch (IllegalArgumentException e) {
      mv.addObject("error", e);
    } catch (Exception e) {
      e.printStackTrace();
      mv.addObject("error", e);
    }


    return mv;
  }


  private void fromPlainText(String data, String delimiter, String format, StringBuilder result) throws Exception {
    if (StringUtils.isNotEmpty(data)) {
      String[] lines = data.split("\r\n");
      String header = lines[0];
      String[] headers = header.split(delimiter);
      for (int i = 1; i < lines.length; i++) {
        Map<String, String> object = new HashMap<>();
        String line = lines[i];
        String[] rawData = line.split(delimiter);
        for (int j = 0; j < rawData.length; j++) {
          String headerName = headers.length <= j ? null : headers[j];
          if (headerName == null) {
            continue;
          }
          object.put(headerName, rawData[j]);
        }
        String r = templateProcess(object, format);
        result.append(r).append("\n");
      }
    }
  }


  private void fromExcel(MultipartFile file, String format, StringBuilder result) throws Exception {
    if (file == null || file.isEmpty()) throw new IllegalArgumentException("Empty file");
    try (InputStream is = file.getInputStream()) {
      XSSFWorkbook workbook = new XSSFWorkbook(is);
      Sheet sheet = workbook.getSheetAt(0);
      Row headerRow = sheet.getRow(sheet.getFirstRowNum());
      short minColIx = headerRow.getFirstCellNum();
      short maxColIx = headerRow.getLastCellNum();
      Map<Integer, String> headerMap = new HashMap<>();
      for (short colIx = minColIx; colIx < maxColIx; colIx++) {
        Cell cell = headerRow.getCell(colIx);
        if (cell == null) {
          continue;
        }
        cell.setCellType(CellType.STRING);
        String v = cell.getStringCellValue();
        if (StringUtils.isEmpty(v)) {
          continue;
        }
        headerMap.put((int) colIx, v);
      }
      Set<Integer> cellNums = headerMap.keySet();
      for (int rowNum = sheet.getFirstRowNum() + 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
        Row row = sheet.getRow(rowNum);
        Map<String, String> valueMap = new HashMap<>();
        for (Integer cellNum : cellNums) {
          Cell cell = row.getCell(cellNum);
          if (cell == null) continue;
          cell.setCellType(CellType.STRING);
          String value = cell.getStringCellValue();
          String header = headerMap.get(cellNum);
          valueMap.put(header, value);
        }
        String r = templateProcess(valueMap, format);
        result.append(r).append("\n");
      }
    }
  }

  private String templateProcess(Map<String, String> object, String format) throws Exception {
    Template template = new Template("DATA", format, freeMarker);
    try (Writer out = new StringWriter()) {
      template.process(object, out);
      return out.toString();
    }

  }


  @Override
  public void destroy() throws Exception {
    freeMarker.clearEncodingMap();
    freeMarker.clearSharedVariables();
    freeMarker.clearTemplateCache();

  }

  @Override
  public void afterPropertiesSet() throws Exception {
    freeMarker = new Configuration(Configuration.VERSION_2_3_28);
    freeMarker.setDefaultEncoding("UTF-8");
    freeMarker.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    freeMarker.setRecognizeStandardFileExtensions(true);
    freeMarker.setNumberFormat("computer");
    freeMarker.setCacheStorage(new NullCacheStorage());

  }
}
