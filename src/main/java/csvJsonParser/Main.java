package csvJsonParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args){
        String[] columpMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> employeeList = parseCSV(columpMapping, fileName);

        writeString(System.getProperty("user.dir"), "data.json", listToJson(employeeList));
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        List<Employee> employeeList = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(String.format("%s/%s", System.getProperty("user.dir"), fileName)))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            employeeList = csv.parse();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employeeList;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();

        Gson gson = new GsonBuilder().create();

        return gson.toJson(list, listType);
    }

    public static void writeString(String path, String fileName, String text) {
        try (FileWriter writer = new FileWriter(String.format("%s/%s", path, fileName))) {

            writer.write(text);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
