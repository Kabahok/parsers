import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columpMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";


//      Парсинг из CSV в JSON

        List<Employee> employeeListCSV = parseCSV(columpMapping, fileName);

        writeString(System.getProperty("user.dir"), "data.json", listToJson(employeeListCSV));

//      Парсинг из XML в JSON
        List<Employee> employeesListXML = parseXML(String.format("%s/%s", System.getProperty("user.dir"), "data.xml"));
        writeString(System.getProperty("user.dir"), "data2.json", listToJson(employeesListXML));



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

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employees = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new File(fileName));

        Node node = document.getDocumentElement();
        NodeList nodeList = node.getChildNodes();

        System.out.println(nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE && nodeList.item(i).getNodeName().equals("employee")) {
                Element element = (Element) nodeList.item(i);

                employees.add(new Employee(
                        Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent()),
                        element.getElementsByTagName("firstName").item(0).getTextContent(),
                        element.getElementsByTagName("lastName").item(0).getTextContent(),
                        element.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                ));
            }
        }

        return employees;
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
