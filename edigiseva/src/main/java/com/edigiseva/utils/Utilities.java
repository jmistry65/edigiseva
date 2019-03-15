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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.edigiseva.message.request.DigiSevaResponseEntity;
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

	public static String readQRCode(String udid) throws IOException {
		File qrCodeimage = new File(UPLOADED_FOLDER_QR + udid + Constants.JPG_EXTENTION);
		BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		try {
			Result result = new MultiFormatReader().decode(bitmap);
			convertStringToXMLDocument(result.getText(), udid);
			return result.getText();
		} catch (NotFoundException e) {
			System.out.println("There is no QR code in the image");
			return null;
		}

	}

	private static Document convertStringToXMLDocument(String xmlString, String udid) {
		// Parser that produces DOM object trees from XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// API to obtain DOM Document instance
		DocumentBuilder builder = null;
		try {
			// Create DocumentBuilder with default configuration
			builder = factory.newDocumentBuilder();

			// Parse the content to Document object
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DOMSource source = new DOMSource(doc.getDocumentElement());
			StreamResult result = new StreamResult(baos);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			transformer.transform(source, result);
			OutputStream os = new FileOutputStream(UPLOADED_FOLDER_XML + udid + Constants.XML_EXTENTION);
			os.write(baos.toByteArray());
			os.close();
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void extractPfd(String udid) throws IOException {

		FileInputStream inputstream = new FileInputStream(
				new File(UPLOADED_FOLDER_PDF + udid + Constants.PDF_EXTENTION));

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
							if (index == 0) {
								imgName = udid + Constants.QRCODE;
							} else if (index == 13) {
								imgName = udid + Constants.USERIMAGE;
							}
							Files.write(new File(UPLOADED_FOLDER_QR, imgName + Constants.JPG_EXTENTION).toPath(),
									bytes);
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

	public static void writePdfFile(Long udid, byte[] response) throws FileNotFoundException {
		try {

			OutputStream os = new FileOutputStream(UPLOADED_FOLDER_PDF + udid.toString() + Constants.PDF_EXTENTION);
			os.write(response);
			System.out.println("Successfully byte inserted");
			os.close();
			Utilities.extractPfd(udid.toString() + Constants.PDF_EXTENTION);
			Utilities.readQRCode(udid.toString());
		}

		catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	public static String xmlToJson(Class classname, Long udid) throws IOException {
		byte[] bFile = fileToByteArray(UPLOADED_FOLDER_XML + udid + Constants.XML_EXTENTION);
		XmlMapper xmlMapper = new XmlMapper();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(xmlMapper.readValue(bFile, classname));
	}

	public static byte[] fileToByteArray(String filePpath) throws IOException {
		File file = new File(filePpath);
		byte[] bytesArray = new byte[(int) file.length()];

		// read file into bytes[]
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

	public static ResponseEntity<DigiSevaResponseEntity> createResponse(boolean isError, String message,
			HttpStatus responseCode, Object value) {
		DigiSevaResponseEntity response = new DigiSevaResponseEntity();
		response.setError(isError);
		response.setMessage(message);
		response.setResponseCode(responseCode);
		response.setValue(value);
		return new ResponseEntity<DigiSevaResponseEntity>(response, responseCode);
	}

	public static String sendmail(String recipient, String newPassword) throws AddressException, MessagingException, IOException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("jainish.mistry@basilroot.com", "jainish@123");
			}
		});
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("jenis.6591@gmail.com", false));

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		msg.setSubject("New Password");
		msg.setContent("New Pssword", "text/html");
		msg.setSentDate(new Date());

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent("New Password is '" + newPassword + "'", "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		msg.setContent(multipart);
		Transport.send(msg);
		return "Success";
	}

	public static String generateRendomPassword() {
		byte[] array = new byte[6]; 
		new Random().nextBytes(array);
		String generatedPassword = new String(array, Charset.forName("UTF-8"));
		return generatedPassword;
	}

	public static void encryptData() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		PublicKey key = loadPublicKey();
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		JSONObject json = new JSONObject();
		Base64 encoder = new Base64();
		
		try {
			String username = "TESTIMPS";
			String password = encoder.encodeAsString(cipher.doFinal("TESTIMPS".getBytes("UTF-8")));
			String customerID = "TESTIMPS";
			String customerReferenceNumber = encoder.encodeAsString(cipher.doFinal("1234".getBytes("UTF-8")));
			String creditAccountNo = encoder.encodeAsString(cipher.doFinal("010405000002".getBytes("UTF-8")));
			String debitAccountNo = encoder.encodeAsString((cipher.doFinal("010405000001".getBytes("UTF-8"))));
			String transactionAmount = encoder.encodeAsString(cipher.doFinal("3".getBytes("UTF-8")));
			String debitNarration =  "Test IMPS By PayKun";
			String creditNarration = "Test IMPS By PayKun";
			json.put("customerReferenceNumber", customerReferenceNumber);
			json.put("debitAccountNumber", debitAccountNo);
			json.put("creditAccountNumber", creditAccountNo);
			json.put("password", password);
			json.put("username", username);
			json.put("transactionAmount", transactionAmount);
			json.put("debitNarration", debitNarration);
			json.put("creditNarration", creditNarration);
			json.put("customerID", customerID);
			JSONObject additionalDetails = new JSONObject();
			additionalDetails.put("remiMobileNumber", "9974770564");
			additionalDetails.put("IFSCCode", "ICIC0000014");
			additionalDetails.put("remarks", "Test IMPS in live");
			json.put("AdditionalDetails",additionalDetails);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("Json "+json);
	}

	private static PublicKey loadPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException {
		String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLGRwIPzfgsD11A9d030isYuMRPtS1zWgf4G/7nciglu7RQHKMR1J8RCDdax4I8fnXTm+wQz1BgwxoJHlL6nItQSkAxSnc5j3O/B4M4e4TzVT7VA6j7IWoBrEiVG6wmF4fZtK87+sLYJn/2/zeo0w8CQ3iKkXO1Ose77Fd+/t2VwIDAQAB"; // full key omitted for brevity
		PublicKey pub = null;
		Base64 decoder = new Base64();
		byte[] decodedBytes = decoder.decode(key);
		X509EncodedKeySpec ks = new X509EncodedKeySpec(decodedBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		pub = kf.generatePublic(ks);
		return pub;
	}
	
	private static char[] generateOTP() {
        String numbers = "0123456789"; 
        Random rndm_method = new Random(); 
        char[] otp = new char[6]; 
        for (int i = 0; i < 6; i++) 
        { 
            otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length())); 
        } 
        return otp; 
	}
}
