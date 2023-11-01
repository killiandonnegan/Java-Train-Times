package Assignment7;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TrainTimes {

    public static void main(String[] args) throws Exception
    {
    	//url with XML document
        String url = "http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML_WithNumMins?StationCode=CLSLA&NumMins=0&format=xml";
        
        //parse XML and create a HTML table
        String[][] trainData = parseXml(url);
        String html = generateHTMLTable(trainData);
        
        //write to HTML file "TrainTimes.html"
        Files.write(Paths.get("TrainTimes.html"), html.getBytes());
        System.out.println("HTML table saved to TrainTimes.html");
    }

    public static String[][] parseXml(String url) throws Exception
    {
    	//HTTP client
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //GET request
        HttpGet httpget = new HttpGet(url);

        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();

        //if response has no content
        if (entity == null)
        {
            throw new RuntimeException("No XML response");
        }

        InputStream instream = entity.getContent();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(instream);
        
        //extracting data
        NodeList nodeList = doc.getElementsByTagName("objStationData");
        String[][] trainDataArray = new String[nodeList.getLength()][5];
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            trainDataArray[i][0] = element.getElementsByTagName("Exparrival").item(0).getTextContent();
            trainDataArray[i][1] = element.getElementsByTagName("Origin").item(0).getTextContent();
            trainDataArray[i][2] = element.getElementsByTagName("Destination").item(0).getTextContent();
            trainDataArray[i][3] = element.getElementsByTagName("Expdepart").item(0).getTextContent();
            trainDataArray[i][4] = element.getElementsByTagName("Scharrival").item(0).getTextContent();
        }

        instream.close();
        httpclient.close();
        return trainDataArray;
    }

    public static String generateHTMLTable(String[][] data)
   {
    	//generates HTML table with train data
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Maynooth Train Times</title></head><body>");
        html.append("<h1 style='text-align:center;'>Maynooth Train Times</h1>");
        html.append("<table style='margin:auto;' border='1'><tr>");
        html.append("<th>Expected Arrival Time</th>");
        html.append("<th>Origin</th>");
        html.append("<th>Destination</th>");
        html.append("<th>Expected Departure Time</th>");
        html.append("<th>Arrival Time at Destination</th></tr>");
        for (String[] row : data) {
            html.append("<tr>");
            for (String cell : row) {
                html.append("<td>").append(cell).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table></body></html>");
        return html.toString();
    }

}
