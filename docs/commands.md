# API Usage Examples

This file shows example `curl` commands to interact with the deployed Spring Boot service, showcasing a simple example.

## Base URL

The base URL will be referred to as ${BASE_URL} on the following commands, depending if we're working on the local or remote server:

```text
http://localhost:3000 (local)
https://inditex.onrender.com (remote)
```

Note you can define this environment variable as, for example:
```bash
export BASE_URL="https://inditex.onrender.com"
```

## Case-use

We are going to describe an example where we:
1) Center: create, update & read --> Eventually delete
2) Order: create, read & assign

**Note if you are using the remote approach, the database might already have some data. Consider changing the payload values**

### Create a Center

```bash
curl -X POST ${BASE_URL}/api/centers \
-H "Content-Type: application/json" \
-d '{
  "name": "Amazon Center",
  "capacity": "BMS",
  "status": "OCCUPIED",
  "maxCapacity": 4,
  "currentLoad": 1,
  "coordinates": { "latitude": 5.1, "longitude": 8.0 }
}'
```

### Update a Center

```bash
curl -X PATCH ${BASE_URL}/api/centers/1 \
-H "Content-Type: application/json" \
-d '{
  "status": "AVAILABLE",
  "maxCapacity": 5
}'
```

### Read all Centers

```bash
curl -X GET ${BASE_URL}/api/centers \
-H "Content-Type: application/json"
```

### Create an Order

```bash
curl -X POST ${BASE_URL}/api/orders \
-H "Content-Type: application/json" \
-d '{
  "customerId": 203,
  "size": "M",
  "coordinates": { "latitude": 51.5074, "longitude": -0.1278 }
}'
```

### Read all Orders

```bash
curl -X GET ${BASE_URL}/api/orders \
-H "Content-Type: application/json"
```

### Assign all "PENDING" Orders to an "AVAILABLE" Center

```bash
curl -X POST ${BASE_URL}/api/orders/order-assignations \
-H "Content-Type: application/json"
```

### Delete a Center

```bash
curl -X DELETE ${BASE_URL}/api/centers/1 \
-H "Content-Type: application/json"
```