# cityconnect
 Project for Spring Boot to check if two cities are connected by road
 
 The data is loaded at startus from an input file
 
## Data file format
 Portland,Seattle
 Portland,San Francisco
 Seattle,Vancouver
 San Francisco,Los Angeles
 Los Angeles,San Diego
 Spokane,Bellevue
 Bend,Eugene
 
## API details 
The connectivity between two cities is checked by making a GET call to the end-point /connected and passing two parameters - origin and destination
### Sample API calls
1. localhost:8080/connected?origin=Boston&destination=Philadelphia
2. localhost:8080/connected?origin=Portland&destination=Seattle

