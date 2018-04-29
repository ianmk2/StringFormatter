package kr.mk2.stringformatter;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AppController implements InitializingBean, DisposableBean {
  Configuration freeMarker;


  @RequestMapping("")
  public Object indexPage(@RequestParam(defaultValue = "tab") String delimiter, String data, String format) {
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
      default:
        delimiter = "\t";
    }

    try {
      StringBuilder result = new StringBuilder();
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
          Template template = new Template("DATA", format, freeMarker);
          try (Writer out = new StringWriter()) {
            template.process(object, out);
            result.append(out.toString()).append("\n");
          }
        }
        mv.addObject("result", result);
      }
    } catch (Exception e) {
      e.printStackTrace();
      mv.addObject("error", e);
    }


    return mv;
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
