package com.javainterviewpoint.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.javainterviewpoint.bean.Address;
import com.javainterviewpoint.bean.Country;
import com.javainterviewpoint.bean.Employee;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;

@Service
public class CountryService implements ICountryService {
	private static final Logger logger = LogManager.getLogger(CountryService.class);


    private final ArrayList<Country> countries;
    private final ArrayList<Employee> empl;

    public CountryService() {

        countries = new ArrayList<Country>();
        empl=new ArrayList<Employee>();
    }

    @SuppressWarnings("resource")
	@Override
    public ArrayList<Country> findAll() {


        try {

        	ObjectMapper objectMapper = new ObjectMapper();
    	  	//Set pretty printing of json
    	  	objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
          //Build reader instance
          CSVReader reader = new CSVReader(new FileReader("countries.csv"), ',', '"', 1);
          //CSVReader reader = new CSVReader(new FileReader("src/main/resources/countries.csv"), ',', '"', 1);
          
            String[] nextLine;
            reader.readNext();
            
            while ((nextLine = reader.readNext()) != null) {

                Country newCountry = new Country(nextLine[0],
                        Integer.valueOf(nextLine[1]));
                countries.add(newCountry);
            }
          logger.info("Country list are:" +countries);
        } catch (FileNotFoundException ex) {
            logger.error(CountryService.class.getName());
        } catch (IOException ex) {
            logger.error(CountryService.class.getName());
        } 

        return countries;
    }

	@Override
	public ArrayList<Employee> findEmpAll() {
		//create JsonParser object
		try {
			JsonParser jsonParser = new JsonFactory().createParser(new File("employee.json"));
			
			
			
			//loop through the tokens
			Employee emp=new Employee();
			Address address = new Address();
			
			
			ObjectMapper m = new ObjectMapper();
			JsonNode rootNode = m.readTree(new File("employee.json"));
			Iterator<JsonNode> it = rootNode.elements();

			while(it.hasNext()) {
				emp.setAddress(address);
				emp.setCities(new ArrayList<String>());
				emp.setProperties(new HashMap<String, String>());
				List<Long> phoneNums = new ArrayList<Long>();
				boolean insidePropertiesObj=false;
				
				parseJSON(jsonParser, emp, phoneNums, insidePropertiesObj);
				
				long[] nums = new long[phoneNums.size()];
				int index = 0;
				for(Long l :phoneNums){
					nums[index++] = l;
				}
				emp.setPhoneNumbers(nums);
				logger.info("" + it.next().toString());
				logger.info("Employee Object\n\n"+emp);
			    //System.out.println("" + it.next().toString());
			    //System.out.println("Employee Object\n\n"+emp);
			    empl.add(emp);
			}
			jsonParser.close();
			
		}catch(JsonParseException ex) {
			ex.printStackTrace();
		}catch(IOException ex) {
			ex.printStackTrace();
		}
				
				//print employee object
		return empl;
	}
	private static void parseJSON(JsonParser jsonParser, Employee emp,
			List<Long> phoneNums, boolean insidePropertiesObj) throws JsonParseException, IOException {
		
		//loop through the JsonTokens
		while(jsonParser.nextToken() != JsonToken.END_OBJECT){
			String name = jsonParser.getCurrentName();
			if("id".equals(name)){
				jsonParser.nextToken();
				emp.setId(jsonParser.getIntValue());
			}else if("name".equals(name)){
				jsonParser.nextToken();
				emp.setName(jsonParser.getText());
			}else if("permanent".equals(name)){
				jsonParser.nextToken();
				emp.setPermanent(jsonParser.getBooleanValue());
			}else if("address".equals(name)){
				jsonParser.nextToken();
				//nested object, recursive call
				parseJSON(jsonParser, emp, phoneNums, insidePropertiesObj);
			}else if("street".equals(name)){
				jsonParser.nextToken();
				emp.getAddress().setStreet(jsonParser.getText());
			}else if("city".equals(name)){
				jsonParser.nextToken();
				emp.getAddress().setCity(jsonParser.getText());
			}else if("zipcode".equals(name)){
				jsonParser.nextToken();
				emp.getAddress().setZipcode(jsonParser.getIntValue());
			}else if("phoneNumbers".equals(name)){
				jsonParser.nextToken();
				while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
					phoneNums.add(jsonParser.getLongValue());
				}
			}else if("role".equals(name)){
				jsonParser.nextToken();
				emp.setRole(jsonParser.getText());
			}else if("cities".equals(name)){
				jsonParser.nextToken();
				while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
					emp.getCities().add(jsonParser.getText());
				}
			}else if("properties".equals(name)){
				jsonParser.nextToken();
				while(jsonParser.nextToken() != JsonToken.END_OBJECT){
					String key = jsonParser.getCurrentName();
					jsonParser.nextToken();
					String value = jsonParser.getText();
					emp.getProperties().put(key, value);
				}
			}
		}
	}
	
}