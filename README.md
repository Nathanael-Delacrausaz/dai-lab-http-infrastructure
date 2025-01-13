# DAI Lab - HTTP Infrastructure

## Step 1: Static Web Server

### Project Structure
```
.
├── docker-compose.yml  # Docker Compose configuration
└── static-web/
    ├── Dockerfile     # Docker configuration for Nginx
    ├── nginx.conf     # Nginx server configuration
    └── html/
        └── index.html # Static website content
```

### Configuration Details

#### Nginx Configuration (nginx.conf)
The `nginx.conf` file contains the following key configurations:
- `worker_connections 1024`: Defines how many simultaneous connections each worker process can handle
- `listen 80`: The server listens on port 80 for HTTP connections
- `root /usr/share/nginx/html`: Specifies the directory where static files are stored
- `index index.html`: Defines the default file to serve when accessing a directory

#### Docker Compose Configuration
The `docker-compose.yml` file defines the services and network configuration:
- Creates a service named `static-web` using the Dockerfile in the static-web directory
- Maps port 80 of the container to port 80 of the host
- Sets up a bridge network named `gd-network` for service communication

### Building and Running

1. Build the services:
```bash
docker-compose build
```

2. Start the services:
```bash
docker-compose up -d
```

3. Stop the services:
```bash
docker-compose down
```

### Accessing the Website
Once the services are running, you can access the static website by:
1. Opening a web browser
2. Navigating to http://localhost

The website displays information about the most challenging Geometry Dash levels (it's a video game).

## Step 3: Dynamic API Server

### API Architecture

The API follows the MVC (Model-View-Controller) and DAO (Data Access Object) patterns:

1. **Model (Level.java)**
   - Represents a Geometry Dash level with properties:
     - id: Unique identifier
     - name: Level name
     - creator: Level creator
     - verifier: Person who verified the level
     - difficulty: Level difficulty (e.g., "Extreme Demon")
     - rating: Level rating (0-10)
     - length: Level length (e.g., "Long")
     - attempts: Number of attempts
     - completionPercentage: Completion percentage

2. **Controller (LevelController.java)**
   - Handles HTTP requests and responses
   - Implements CRUD operations endpoints:
     - GET /api/levels: List all levels
     - GET /api/levels/{id}: Get specific level
     - POST /api/levels: Create new level
     - PUT /api/levels/{id}: Update existing level
     - DELETE /api/levels/{id}: Delete level

3. **DAO (LevelDAO.java)**
   - Manages data persistence (currently in-memory)
   - Handles CRUD operations on Level objects
   - Includes sample data for testing

### API Endpoints

#### GET /api/levels
- Returns all levels
- Response: 200 OK with array of levels

#### GET /api/levels/{id}
- Returns a specific level
- Response: 200 OK with level or 404 Not Found

#### POST /api/levels
- Creates a new level
- Body: Level JSON object
- Response: 201 Created with created level

Example request body:
```json
{
    "name": "Sonic Wave Infinity",
    "creator": "APTeam",
    "verifier": "Trick",
    "difficulty": "Extreme Demon",
    "rating": 9.9,
    "length": "Long",
    "attempts": 28945,
    "completionPercentage": 100
}
```

#### PUT /api/levels/{id}
- Updates an existing level
- Body: Level JSON object
- Response: 200 OK with updated level or 404 Not Found

#### DELETE /api/levels/{id}
- Deletes a level
- Response: 204 No Content or 404 Not Found

### Testing the API

You can test the API using tools like Hoppscotch (https://hoppscotch.io)
(we use this tool to test the API)

1. Start the services:
```bash
docker-compose up -d
```

2. The API will be available at:
```
http://localhost:8080/api/levels
```

3. Test different endpoints:
   - Use GET to retrieve levels
   - Use POST to create new levels
   - Use PUT to update existing levels
   - Use DELETE to remove levels

### Implementation Details

- Built with Java and Javalin framework
- Uses Jackson for JSON serialization
- Runs on port 8080
- Containerized with Docker
- Integrated with the static web server via Docker Compose

## Step 4: Reverse Proxy with Traefik

### Overview
Traefik is used as a reverse proxy to route requests to the appropriate service based on the URL path. This setup allows us to access both our static website and API through a single domain.

### Configuration
The setup involves three main services:
1. `reverse-proxy` (Traefik)
2. `static-web` (Nginx static website)
3. `api` (Java API)

### Network Configuration
Two Docker networks are used:
- `traefik-public`: For communication between Traefik and services
- `gd-network`: For internal communication between services

### Traefik Configuration
```yaml
reverse-proxy:
  image: traefik:latest
  command:
    - "--api.insecure=true"
    - "--providers.docker=true"
    - "--providers.docker.exposedbydefault=false"
    - "--entrypoints.web.address=:80"
  ports:
    - "80:80"
    - "8080:8080"
  volumes:
    - /var/run/docker.sock:/var/run/docker.sock:ro
```

### Service Labels
Each service is configured with Traefik labels for routing:

**Static Web Server:**
```yaml
labels:
  - "traefik.enable=true"
  - "traefik.http.routers.static.rule=Host(`localhost`) && PathPrefix(`/`)"
  - "traefik.http.services.static.loadbalancer.server.port=80"
```

**API Server:**
```yaml
labels:
  - "traefik.enable=true"
  - "traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api`)"
  - "traefik.http.services.api.loadbalancer.server.port=8080"
```

### Accessing Services
- Static Website: http://localhost/
- API Endpoints: http://localhost/api/levels
- Traefik Dashboard: http://localhost:8080

### Starting the Services
```bash
# Create the Traefik network (if not exists)
docker network create traefik-public

# Start all services
docker compose up -d

```

## Step 5: Scalability and Load Balancing

### Overview
This step implements horizontal scaling for both the static web server and API server, allowing multiple instances to run simultaneously with automatic load balancing through Traefik.

### Configuration
The setup uses Docker Compose's `deploy` section to specify the number of replicas for each service:

```yaml
static-web:
  deploy:
    mode: replicated
    replicas: 3  # 3 instances of static web server

api:
  deploy:
    mode: replicated
    replicas: 2  # 2 instances of API server
```

### Load Balancing
- Traefik automatically detects all instances of each service
- Uses round-robin load balancing by default
- Dynamically updates when instances are added or removed

### Scaling Commands
To scale services up or down:
```bash
# Scale static web server
docker compose up -d --scale static-web=5

# Scale API server
docker compose up -d --scale api=3
```

### Verifying Load Balancing
1. Check running instances:
```bash
docker compose ps
```

2. View Traefik dashboard at http://localhost:8080 to see:
   - All running instances
   - Load balancing configuration
   - Health status of each instance

## Step 6: Load Balancing with Round-Robin and Sticky Sessions

### Overview
This step implements different load balancing strategies :
- Round-robin for the static web server (default behavior)
- Sticky sessions for the API server (to maintain session consistency)

### Configuration
Added sticky session configuration to the API service in `docker-compose.yml`:

```yaml
api:
  labels:
    - "traefik.http.services.api.loadbalancer.sticky.cookie=true"
    - "traefik.http.services.api.loadbalancer.sticky.cookie.name=API_STICKY"
```

### How it Works
1. **Static Web Server (Round-Robin)**:
   - Requests are distributed evenly across all instances
   - Each request can go to a different instance
   - Good for stateless content

2. **API Server (Sticky Sessions)**:
   - First request generates a cookie named "API_STICKY"
   - Subsequent requests with this cookie go to the same instance
   - Ensures session consistency for stateful operations

### Testing the Configuration

To verify the load balancing strategies:

1. **Round-Robin (Static Server)**:
```bash
# Watch the logs while making multiple requests to http://localhost/
docker compose logs static-web
```
Should see requests distributed across different instances.

2. **Sticky Sessions (API)**:
- Open browser developer tools (F12)
- Go to Network tab
- Make requests to http://localhost/api/levels
- Observe:
  - The "API_STICKY" cookie in response headers
  - All subsequent requests going to the same instance
- Clear cookies to get assigned to a potentially different instance

## Step 7: Securing Traefik with HTTPS

### Overview
This step implements HTTPS security for our infrastructure using Traefik as the SSL termination point. All external traffic is encrypted using HTTPS, while internal communication between Traefik and the services remains in HTTP.

### Implementation Steps

1. **Certificate Generation**
   - Created self-signed certificates using OpenSSL
   - Certificates are stored in the `certs` directory
   - Used for development/testing purposes (in production, use proper CA-signed certificates)

2. **Traefik Configuration**
   Created `traefik.yaml` with the following key configurations:
   ```yaml
   entryPoints:
     web:
       address: ":80"
       http:
         redirections:
           entryPoint:
             to: websecure
             scheme: https
     websecure:
       address: ":443"

   tls:
     certificates:
       - certFile: "/etc/traefik/certificates/cert.crt"
         keyFile: "/etc/traefik/certificates/cert.key"
   ```

3. **Service Configuration**
   Updated services in `docker-compose.yml` with HTTPS labels:
   ```yaml
   labels:
     - "traefik.http.routers.static.entrypoints=websecure"
     - "traefik.http.routers.static.tls=true"
   ```

### Testing
1. Access the services via HTTPS:
   - Static site: https://localhost/
   - API: https://localhost/api/levels
   - Traefik dashboard: http://localhost:8080

2. Verify that HTTP requests are automatically redirected to HTTPS

Note: Your browser will show a security warning because we're using a self-signed certificate. This is normal in development environments.

## Optional Step: Integration API - Static Web Site

### Overview
This step implements a dynamic integration between our static website and the API server using JavaScript's Fetch API. The integration provides real-time interaction with the Geometry Dash levels database.

### Features Implemented

1. **Real-time Data Display**
   - Automatic fetching of levels from the API every 5 seconds
   - Dynamic statistics dashboard showing total levels and difficulty distribution
   - Visual feedback for all API operations

2. **Interactive Level Management**
   ```javascript
   // Example of level creation
   async function addLevel(event) {
       const levelData = {
           name: document.getElementById('levelName').value,
           creator: document.getElementById('levelCreator').value,
           difficulty: document.getElementById('levelDifficulty').value
       };
       // API call...
   }
   ```

3. **CRUD Operations**
   - **Create**: Form to add new levels with name, creator, and difficulty
   - **Read**: Automatic fetching and display of all levels
   - **Delete**: Button on each level card for removal
   - Visual feedback for all operations through status messages

4. **User Interface Components**
   - Level creation form with validation
   - Dynamic statistics cards
   - Level cards with difficulty indicators
   - Status notifications for operation feedback

### Technical Implementation

1. **API Integration**
   ```javascript
   async function fetchLevels() {
       const response = await fetch('/api/levels');
       const levels = await response.json();
       updateStats(levels);
       updateDisplay(levels);
   }
   ```

2. **Error Handling**
   - Network error management
   - User feedback through status messages
   - Graceful degradation when API is unavailable

3. **Automatic Updates**
   ```javascript
   // Periodic refresh every 5 seconds
   setInterval(fetchLevels, 5000);
   ```

### Validation
You can verify the integration is working by:
1. Opening the browser's developer tools (F12)
2. Watching the Network tab for API calls
3. Observing real-time updates to the UI
4. Testing error scenarios by temporarily stopping the API service

