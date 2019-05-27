package com.javainterviewpoint.service;

import java.util.ArrayList;

import com.javainterviewpoint.bean.Country;
import com.javainterviewpoint.bean.Employee;

public interface ICountryService {
    
    public ArrayList<Country> findAll();
    
    public ArrayList<Employee> findEmpAll();
}
