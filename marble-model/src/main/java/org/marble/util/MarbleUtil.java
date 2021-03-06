package org.marble.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

public class MarbleUtil {
    public static String getDatedMessage(String message) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date();
        return dateFormat.format(date) + ": " + message;
    }

    public static Comparator<List<Double>> sortComparator = new Comparator<List<Double>>() {
        @Override
        public int compare(List<Double> arg0, List<Double> arg1) {
            return arg0.get(0).compareTo(arg1.get(0));
        }
    };

    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = fullDateFormatter.parse(dateString);

        } catch (ParseException e1) {
            SimpleDateFormat onlyDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = onlyDateFormatter.parse(dateString);

            } catch (ParseException e2) {
                e2.printStackTrace();
            }
        }
        return date;
    }

    public static File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
        File convFile = File.createTempFile(multipart.getOriginalFilename(), ".tmp");
        multipart.transferTo(convFile);
        return convFile;
    }

    public static String getBasePath(HttpServletRequest request) {
        if (request.getContextPath().equals("/")) {
            return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        } else {
            return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }
    }
}
