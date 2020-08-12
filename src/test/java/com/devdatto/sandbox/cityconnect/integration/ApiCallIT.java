package com.devdatto.sandbox.cityconnect.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.devdatto.sandbox.cityconnect.CityConnectApplication;

@SpringBootTest(classes = CityConnectApplication.class, 
				webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiCallIT {

	private static final String BASE_URL="http://localhost:";
	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();
	
	@Test
	public void testCityConnectivityTrue() throws JSONException {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				getUrl("/connected?origin=Boston&destination=Philadelphia"),
				HttpMethod.GET, entity, String.class);

		String expected = 
				"{" + 
				"    \"request\": {" + 
				"        \"origin\": \"Boston\"," + 
				"        \"destination\": \"Philadelphia\"" + 
				"    }," + 
				"    \"response\": {" + 
				"        \"pathExists\": true" + 
				"    }," + 
				"    \"hasErrorResponse\": false" + 
				"}";

		JSONAssert.assertEquals(expected, response.getBody(), false);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void testCityConnectivityFalse() throws JSONException {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				getUrl("/connected?origin=boston&destination=albany"),
				HttpMethod.GET, entity, String.class);

		String expected = 
				"{" + 
				"    \"request\": {" + 
				"        \"origin\": \"boston\"," + 
				"        \"destination\": \"albany\"" + 
				"    }," + 
				"    \"response\": {" + 
				"        \"pathExists\": false" + 
				"    }," + 
				"    \"hasErrorResponse\": false" + 
				"}";

		JSONAssert.assertEquals(expected, response.getBody(), false);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void testCityConnectivityFalseWithHTTPStatusNotFound() throws JSONException {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				getUrl("/connected?origin=Portland&destination=Seattle"),
				HttpMethod.GET, entity, String.class);

		String expected = 
				"{" + 
				"    \"request\": {" + 
				"        \"origin\": \"Portland\"," + 
				"        \"destination\": \"Seattle\"" + 
				"    }," + 
				"    \"response\": {" + 
				"        \"pathExists\": false" + 
				"    }," + 
				"    \"error\": {" + 
				"        \"errorMessage\": \"input location(s) not found in graph :: Portland and / or Seattle\"," + 
				"        \"errorCode\": 404," + 
				"        \"errorClassName\": \"com.devdatto.sandbox.cityconnect.exception.DataNotFoundException\"," + 
				"        \"errorDescriptionUrl\": \"ERR015:NOT_FOUND\"" + 
				"    }," + 
				"    \"hasErrorResponse\": true" + 
				"}";

		JSONAssert.assertEquals(expected, response.getBody(), false);
		assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}
	
	@Test
	public void testCityConnectivityFalseWithHTTPStatusBadRequest() throws JSONException {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				getUrl("/connected?origin=Portland&destination="),
				HttpMethod.GET, entity, String.class);

		String expected = 
				"{" + 
				"    \"error\": {" + 
				"        \"errorMessage\": \"Destination parameter blank.\"," + 
				"        \"errorCode\": 400,\r\n" + 
				"        \"errorClassName\": \"com.devdatto.sandbox.cityconnect.exception.BadRequestException\"," + 
				"        \"errorDescriptionUrl\": \"ERR010:BAD_REQUEST\"" + 
				"    }," + 
				"    \"hasErrorResponse\": true" + 
				"}";

		JSONAssert.assertEquals(expected, response.getBody(), false);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}
	
	String getUrl(String uri) {
		return BASE_URL + this.port + uri;
	}
}
