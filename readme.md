# DocuFlow

DocuFlow is a full-stack microservices platform featuring Spring Boot, Angular, RabbitMQ, PostgreSQL, AWS S3, Docker, JWT authentication, WebSocket real-time updates, and multi-threaded file-to-PDF conversion.

## Overview

- **Backend**: Spring Boot REST API (`/auth` and `/api/files`), JWT-based security
- **Frontend**: Angular application with file upload and WebSocket progress updates
- **Messaging**: RabbitMQ queue (`file-processing-queue`) running in Docker
- **Database**: PostgreSQL for metadata
- **Storage**: AWS S3 (optional) for PDF files
- **Processing**: Executor service with 4 threads for concurrent conversion

## Prerequisites

- Java 17+, Maven 3.6+
- Node.js 14+, npm
- Docker & Docker Compose
- (Optional) AWS credentials (access key, secret key)

## Setup

1. **Clone repository**
   ```bash
   git clone https://github.com/your-org/docuflow.git
   cd docuflow
   ```
2. **Configure backend** (`backend/src/main/resources/application.properties`)
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
   spring.datasource.username=postgres
   spring.datasource.password=<DB_PASSWORD>

   spring.rabbitmq.host=localhost
   spring.rabbitmq.port=5672
   spring.rabbitmq.username=guest
   spring.rabbitmq.password=guest

   jwt.secret=<YOUR_JWT_SECRET>
   jwt.expiration=3600
   jwt.issuer=docuflow-app

   cloud.aws.region=<AWS_REGION>
   cloud.aws.s3.bucket=<S3_BUCKET_NAME>

   spring.servlet.multipart.max-file-size=50MB
   spring.servlet.multipart.max-request-size=50MB
   ```
3. **Configure frontend** (`frontend/.env`)
   ```ini
   NG_APP_API_URL=http://localhost:8080/api
   NG_APP_WS_URL=ws://localhost:8080/ws/progress
   ```
4. **Start RabbitMQ**
   ```bash
   ```

docker-compose up -d

````
5. **Start PostgreSQL** (if not already running)
```bash
docker run --name docuflow-postgres -e POSTGRES_PASSWORD=<DB_PASSWORD> -p 5432:5432 -d postgres
````

6. **Build & run backend**
   ```bash
   cd backend
   mvn clean package
   java -jar target/*.jar
   ```
7. **Build & run frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

## Authentication Endpoints

| Endpoint         | Method | Description              |
| ---------------- | ------ | ------------------------ |
| `/auth/register` | POST   | Register new user        |
| `/auth/login`    | POST   | Authenticate and get JWT |

Include header `Authorization: Bearer <token>` for all `/api/files` routes.

## File API Endpoints

| Endpoint            | Method | Description                                  |
| ------------------- | ------ | -------------------------------------------- |
| `/api/files/upload` | POST   | Upload file for conversion                   |
| `/files/{fileId}`   | GET    | Get presigned download URL or status message |

- **Upload**: Returns `fileId` immediately.
- **Download**: Returns HTTP 200 + `{ "url": "<presignedUrl>" }` if done, 202 + status if processing, or 404 if not found.

## WebSocket Progress

- **URL**: `ws://<host>:8080/ws/progress`
- **Message format**:
  ```json
  { "fileId": "...", "phase": "conversion|saving|done|error", "percent": 0-100, "message"?: "..." }
  ```

## Processing Flow

1. Client uploads file via `/api/files/upload`.
2. Controller enqueues `FileMessage` to RabbitMQ (`file-processing-queue`).
3. `FileProcessingConsumer` (4-thread pool) converts file to PDF via `PdfConversionService`.
4. Conversion progress emitted over WebSocket.
5. PDF stored via `FileStorageService` (DB + S3).
6. Final status and presigned URL available from `/files/{fileId}`.

## Configuration

- **Max file size**: 50 MB
- **RabbitMQ queue**: `file-processing-queue`
- **Thread pool size**: 4 threads (defined in `FileProcessingConsumer`)

## Monitoring & Logging

- RabbitMQ UI at `http://localhost:15672`
- SQL queries logged (`spring.jpa.show-sql=true`)
- AWS SDK logs at DEBUG level

## Contributing

1. Fork the repo
2. Create a feature branch
3. Commit and push changes
4. Open a pull request

## License

MIT (see [LICENSE](LICENSE))

## Contact

Hagai Yehoshafat — [hagai.yehosh@gmail.com](mailto\:hagai.yehosh@gmail.com) — [GitHub](https://github.com/hagai007)

