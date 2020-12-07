package com.OrderExecution;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.boot.CommandLineRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.*;
import org.springframework.boot.CommandLineRunner;


public class Main implements CommandLineRunner
{

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	@Override
	public void run(String... args) throws Exception {
		try
		{

			Properties prop = readPropertiesFile("config.properties");
			String orderPath = prop.getProperty("ORDER_PATH");


			LOGGER.info("***PROCESING ORDER***");
//			FileReader reader = new FileReader("order.json");
			FileReader reader = new FileReader(orderPath + "/order.json");

			String jsonText = com.OrderExecution.JSONReader.readAll(reader);
			LOGGER.info("Order Received");
			ArrayList<Map<String,String>> catalog = com.OrderExecution.JSONReader.readJsonFromUrl("http://localhost:8080/phones");
			LOGGER.info("Phone Catalog Retrieved Successfully");

			ArrayList<Map<String,Map<String,Object>>> dataAsMap = (ArrayList<Map<String, Map<String, Object>>>) new ObjectMapper().readValue(jsonText, List.class);
			Map<String,Map<String,Object>> orderList = dataAsMap.get(0);
			JSONArray orderCompleted = new JSONArray();
			int orderNumber = 0; // Could be replaced with a more complex id
			for (Map.Entry<String, Map<String, Object>> order : orderList.entrySet()) {
				orderNumber += 1;
				LOGGER.info("Processing Order " + orderNumber);
				Map<String, Object> orderInfo = order.getValue();
				ArrayList phonesInOrder = (ArrayList) orderInfo.get("customerOrder");
				Double totalOrderPrice = 0.0;
				for(Object phone: phonesInOrder){
					String phoneName = phone.toString();
					Double phonePrice = getPrice(phoneName, catalog);
					totalOrderPrice += phonePrice;
				}
				orderCompleted.add(writeOrder(totalOrderPrice, orderInfo, orderNumber));
			}

			writeJSONFile(orderCompleted);
			LOGGER.info("Order JSON File Created");
			LOGGER.info("***Order Completed***");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static JSONObject parseOrderObject(JSONObject order)
	{
		//Get order object within list
		JSONObject orderObject = (JSONObject) order.get("order");
		
		//Get order first name
		String firstName = (String) orderObject.get("customerFirstName");
		System.out.println(firstName);
		
		//Get order last name
		String lastName = (String) orderObject.get("customerLastName");
		System.out.println(lastName);
		
		//Get order email name
		String email = (String) orderObject.get("customerEmail");
		System.out.println(email);

		ArrayList<String> orders = (ArrayList<String>) orderObject.get("customerOrder");
		System.out.println(orders);

		return orderObject;
	}

	private static Double getPrice (String phone, ArrayList<Map<String,String>> catalog ) {
		for (Map<String,String> item: catalog){
			String catalogItem = item.get("name");
			if (item.get("name").equals(phone)){
				return Double.parseDouble(item.get("price"));
			}
		}
		return 0.0;
	}

	public static JSONArray writeOrder(Double totalPriceOrder, Map<String, Object> order, int orderNumber) {

		String customerFirstName = (String) order.get("customerFirstName");
		String customerLastName = (String) order.get("customerLastName");
		String customerEmail = (String) order.get("customerEmail");
		ArrayList customerOrder = (ArrayList) order.get("customerOrder");

		JSONArray orderInfo = new JSONArray();
		orderInfo.add("Order - " + orderNumber);
		JSONObject orderDetails = new JSONObject();
		orderDetails.put("Customer First Name", customerFirstName);
		orderDetails.put("Customer Last Name", customerLastName);
		orderDetails.put("Customer Email", customerEmail);
		orderDetails.put("Customer Order", customerOrder);
		orderDetails.put("Total Price", totalPriceOrder);
		orderInfo.add(orderDetails);

		return orderInfo;
	}

	public static void writeJSONFile(JSONArray orders) {
		//Write JSON file
		try  {
			FileWriter file = new FileWriter("orderList.json");

			file.write(orders.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Properties readPropertiesFile(String fileName) throws IOException {
		FileInputStream fis = null;
		Properties prop = null;
		try {
			fis = new FileInputStream(fileName);
			prop = new Properties();
			prop.load(fis);
		} catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			fis.close();
		}
		return prop;
	}
}
