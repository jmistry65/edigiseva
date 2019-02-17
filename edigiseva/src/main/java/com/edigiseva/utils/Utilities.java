package com.edigiseva.utils;

import static com.itextpdf.kernel.pdf.canvas.parser.EventType.RENDER_IMAGE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.edigiseva.model.UserJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

public class Utilities {

	private static String UPLOADED_FOLDER_PDF = "src/main/resources/Addhar/";
	private static String UPLOADED_FOLDER_QR = "src/main/resources/QR/";
	private static String UPLOADED_FOLDER_XML = "src/main/resources/XML/";
	
	public static String readQRCode() throws IOException {
		File qrCodeimage = new File(UPLOADED_FOLDER_QR+"QR-0.jpg");
		BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		try {
			Result result = new MultiFormatReader().decode(bitmap);
			convertStringToXMLDocument(result.getText());
			return result.getText();
		} catch (NotFoundException e) {
			System.out.println("There is no QR code in the image");
			return null;
		}

	}
	private static Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         
        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();
             
            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DOMSource source = new DOMSource(doc.getDocumentElement());
            StreamResult result = new StreamResult(baos);
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.transform(source, result);
            OutputStream os = new FileOutputStream(UPLOADED_FOLDER_XML + "abc.xml");
			os.write(baos.toByteArray());
			os.close();
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
	
	public static void extractPfd() throws IOException {

		FileInputStream inputstream = new FileInputStream(new File(UPLOADED_FOLDER_PDF + "abc.pdf"));

		PdfReader reader = new PdfReader(inputstream);
		PdfDocument document = new PdfDocument(reader);
		PdfDocumentContentParser contentParser = new PdfDocumentContentParser(document);
		for (int page = 1; page <= document.getNumberOfPages(); page++) {
			contentParser.processContent(page, new IEventListener() {
				@Override
				public Set<EventType> getSupportedEvents() {
					return Collections.singleton(RENDER_IMAGE);
				}

				@Override
				public void eventOccurred(IEventData data, EventType type) {
					if (data instanceof ImageRenderInfo) {
						ImageRenderInfo imageRenderInfo = (ImageRenderInfo) data;
						byte[] bytes = imageRenderInfo.getImage().getImageBytes();
						try {
							String imgName = "";
							if(index == 0) {
								imgName = "QRCODE";
							}
							else if(index == 13) {
								imgName = "USERIMAGE";
							}
							Files.write(new File(UPLOADED_FOLDER_QR, imgName + ".jpg").toPath(), bytes);
							index++;	
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				int index = 0;

			});
		}
	}
	
	public static void writePdfFile(byte[] response) throws FileNotFoundException {
		try {

			OutputStream os = new FileOutputStream(UPLOADED_FOLDER_PDF + "abc.pdf");
			os.write(response);
			System.out.println("Successfully byte inserted");
			os.close();
			Utilities.extractPfd();
			Utilities.readQRCode();
		}

		catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
	public static String xmlToJson(Class classname) throws IOException {
		byte[] bFile = fileToByteArray(UPLOADED_FOLDER_XML + "abc.xml");
		XmlMapper xmlMapper = new XmlMapper();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(xmlMapper.readValue(bFile, classname));
	}
	
	public static byte[] fileToByteArray(String filePpath) throws IOException {
		  File file = new File(filePpath);
          byte[] bytesArray = new byte[(int) file.length()];

          //read file into bytes[]
          FileInputStream fileInputStream = new FileInputStream(file);
          fileInputStream.read(bytesArray);
		return bytesArray;
	}
	
	public static Object jsonToObject(String userJson, Class<?> className) {
		try {
			return new ObjectMapper().readValue(userJson, className);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String stringToSh556(String inputString) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(inputString.getBytes()); 
			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);
			 return hashtext; 
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return "";
	}
}
