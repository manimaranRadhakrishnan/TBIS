package com.tbis.api;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("hello")
@Produces(MediaType.APPLICATION_JSON)
public class Hello {

	@GET
	public List<String> getHello() {
		List<String> resultList = new ArrayList<>();
		resultList.add("Hello, Jersey!");
		resultList.add("Hello, Swagger!");
		resultList.add("Hello, Swagger UI!");

		return resultList;
	}
}
