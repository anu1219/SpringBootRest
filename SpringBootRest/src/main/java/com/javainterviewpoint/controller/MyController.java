package com.javainterviewpoint.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javainterviewpoint.bean.Country;
import com.javainterviewpoint.bean.Employee;
import com.javainterviewpoint.service.ICountryService;

@RestController
public class MyController {

	@Autowired
	private ICountryService countryService;
	
	@RequestMapping("/countries")
	public List<Country> listCountries() {

		return countryService.findAll();
	}
	
	@RequestMapping("/json/employee")
	public List<Employee> listEmployee() {

		return countryService.findEmpAll();
	}
}
