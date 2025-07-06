# RabbitMQ Spring Boot Integration POC

A proof of concept demonstrating high-throughput data ingestion using RabbitMQ and Spring Boot with tenant-based security using **JWT tokens**.

## Architecture

```
API Request → Producer (Port 8084) → RabbitMQ → Consumer (Port 8081) → PostgreSQL
```

## Features

- **High Throughput**: Handles 200+ transactions per second
- **Tenant-based Security**: JWT authentication with tenant isolation using "sub" claim
- **Message Reliability**: Dead letter queues for failed messages
- **Performance Monitoring**: Real-time metrics and health checks
- **Scalable Design**: Concurrent consumers with configurable throughput

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker and Docker Compose
- Git
- k6 (for load testing)
- Postman (for API testing)

## Quick Start

### 1. Clone and Setup

```bash
git clone <repository-url>
cd rabbitmq-springboot-poc
```

### 2. Start Infrastructure

```bash
# Start RabbitMQ and PostgreSQL
docker-compose up -d

# Wait for services to be ready
sleep 30
```

### 3. Build and Run Applications

```bash
# Build Producer
cd producer
mvn clean package -DskipTests
java -jar target/producer-1.0.0.jar

# In another terminal - Build Consumer
cd consumer
mvn clean package -DskipTests
java -jar target/consumer-1.0.0.jar
```

### 4. Test the System

#### Using Postman

1. **Create a new POST request:**
   - URL: `http://localhost:8084/api/data/ingest`
   - Method: POST

2. **Set Headers:**
   ```
   Content-Type: application/json
   Authorization: Bearer <your_jwt_token>
   ```

3. **Request Body:**
   ```json
   {
     "data": {
       "message": "Test message from Postman",
       "timestamp": "2025-07-06T10:30:00.000Z",
       "userId": "test-user-123"
     }
   }
   ```

> **Note:** The `tenant_id` is automatically extracted from the JWT token's "sub" claim. You don't need to include it in the request body.

## API Endpoints

### Producer (Port 8084)

#### Data Ingestion
```bash
POST /api/data/ingest
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "data": {
    "message": "Your message here",
    "timestamp": "2025-07-06T10:30:00.000Z",
    "userId": "user-123"
  }
}
```

#### Health Check
```bash
GET /api/status
# Returns: Service status and basic information
```

> **Note:** `tenant_id` is automatically extracted from the JWT token's "sub" claim. Do not include it in the request body.

### Consumer (Port 8081)

#### Status Check
```bash
GET /api/status
# Returns: Consumer service status and processed message count
```

## JWT Token Configuration

### Token Requirements
- **"sub" claim**: Used as the tenant identifier
- **Valid signature**: Token must be properly signed
- **Not expired**: Check expiration time

### Example JWT Payload
```json
{
  "sub": "tenant-123",
  "iat": 1751813125,
  "exp": 1751816725,
  "iss": "your-token-issuer"
}
```

The system will use `"sub": "tenant-123"` as the tenant ID for data isolation.

## Performance Testing

### Using k6 Load Testing

#### Basic Load Test
```bash
# Run k6 load test with 100 virtual users for 10 seconds
k6 run k6-producer-loadtest_Version2.js
```

#### Custom Load Test
```javascript
// k6-custom-test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 50,        // 50 virtual users
    duration: '30s' // Run for 30 seconds
};

export default function () {
    const payload = JSON.stringify({
        data: {
            message: `k6 test message from VU ${__VU}`,
            timestamp: new Date().toISOString()
        }
    });
    
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer YOUR_JWT_TOKEN'
        }
    };
    
    let res = http.post('http://localhost:8084/api/data/ingest', payload, params);
    
    check(res, { 'status was 200': (r) => r.status === 200 });
    sleep(0.1);
}
```

### Load Test Scenarios

#### 1. Ramp-up Test
```bash
# Gradually increase load
k6 run --stage 1m:10,5m:50,1m:0 k6-test.js
```

#### 2. Spike Test
```bash
# Sudden load spikes
k6 run --stage 30s:10,10s:100,30s:10 k6-test.js
```

#### 3. Stress Test
```bash
# High load for extended period
k6 run --vus 200 --duration 10m k6-test.js
```

## Configuration

### Producer Application (application.yml)

```yaml
server:
  port: 8084

spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin123
  
  datasource:
    url: jdbc:postgresql://localhost:5432/tenant_data
    username: admin
    password: admin123

jwt:
  secret: your-jwt-secret-key
  expiration: 3600000  # 1 hour in milliseconds
```

### Consumer Application (application.yml)

```yaml
server:
  port: 8081

spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin123
    listener:
      simple:
        concurrency: 5
        max-concurrency: 10
        prefetch: 250
  
  datasource:
    url: jdbc:postgresql://localhost:5432/tenant_data
    username: admin
    password: admin123
    hikari:
      maximum-pool-size: 20
```

## Monitoring

### RabbitMQ Management UI
- URL: http://localhost:15672
- Username: admin
- Password: admin123

### Application Metrics

#### Producer Health
```bash
curl http://localhost:8084/api/status
```

#### Consumer Status
```bash
curl http://localhost:8081/api/status
```

## Security Features

- **JWT Token Authentication**: Secure token-based authentication
- **Tenant Isolation**: Each user/client is mapped to a tenant via JWT "sub" claim
- **Transport Security**: HTTPS ready (configure certificates)
- **Database Security**: Connection pooling with credentials

## Performance Optimizations

- **Connection Pooling**: HikariCP for database connections
- **Message Prefetch**: Optimized RabbitMQ consumer prefetch
- **Batch Processing**: Hibernate batch inserts
- **Concurrent Consumers**: Multiple consumer threads
- **Dead Letter Queues**: Handle failed messages gracefully

## Troubleshooting

### Common Issues

1. **Services not starting**
   ```bash
   # Check if ports are available
   netstat -tulpn | grep -E ':(5672|5432|8084|8081)'
   
   # Check Docker containers
   docker-compose ps
   ```

2. **JWT Authentication failures**
   ```bash
   # Check JWT token validity and expiration
   # Ensure "sub" claim is present in your token
   # Verify JWT secret configuration matches
   ```

3. **Connection issues from WSL to Windows**
   ```bash
   # Use Windows host IP instead of localhost
   curl -X POST http://172.17.0.1:8084/api/data/ingest
   
   # Or find your Windows IP
   ip route show | grep -i default | awk '{ print $3}'
   ```

3. **Low throughput**
   ```bash
   # Check RabbitMQ queue status
   curl -u admin:admin123 http://localhost:15672/api/queues
   
   # Check consumer status
   curl http://localhost:8081/api/status
   ```

### Testing with Postman

1. **Import Collection**: Create a new collection with your API endpoints
2. **Environment Variables**: Set up variables for:
   - `base_url`: `http://localhost:8084`
   - `jwt_token`: Your JWT token
   - `consumer_url`: `http://localhost:8081`

3. **Test Scenarios**:
   - Single message ingestion
   - Bulk message testing
   - Invalid token testing
   - Performance testing with Postman Runner

### Logs

```bash
# Producer logs
tail -f producer/logs/application.log

# Consumer logs
tail -f consumer/logs/application.log
```

## Scaling Considerations

- **Horizontal Scaling**: Run multiple consumer instances
- **Database Sharding**: Partition data by tenant
- **RabbitMQ Clustering**: For high availability
- **Load Balancing**: Use nginx or similar for producer instances

## Next Steps

This POC can be extended with:
- **Kubernetes Deployment**: Container orchestration
- **Monitoring Stack**: Prometheus + Grafana
- **Message Encryption**: End-to-end encryption
- **Multi-region Setup**: Geographic distribution
- **Advanced Security**: OAuth2, RBAC

## Getting Started with k6

### Install k6

**Windows:**
```bash
# Using Chocolatey
choco install k6

# Using winget
winget install k6

# Or download from https://k6.io/docs/getting-started/installation/
```

**Linux/WSL:**
```bash
# Using package manager
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

### Quick k6 Test

```bash
# Create a simple test file
cat > quick-test.js << 'EOF'
import http from 'k6/http';
import { check } from 'k6';

export default function () {
    const res = http.post('http://localhost:8084/api/data/ingest', 
        JSON.stringify({
            data: {
                message: "k6 test message",
                timestamp: new Date().toISOString()
            }
        }),
        {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer YOUR_JWT_TOKEN'
            }
        }
    );
    
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });
}
EOF

# Run the test
k6 run --vus 10 --duration 30s quick-test.js
```

## License

This project is for demonstration purposes only.