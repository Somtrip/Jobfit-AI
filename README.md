# JobFit AI - AI-Powered Resume & Job Description Matcher

**Find your perfect job match in seconds, not weeks.**

JobFit AI is a comprehensive web platform that uses advanced AI to match resumes with job descriptions, providing instant feedback, skill gap analysis, and personalized improvement suggestions.

## 🚀 Features

### For Job Seekers
- **Smart Resume Upload**: Support for PDF, DOC, and DOCX formats
- **AI-Powered Analysis**: Automatic extraction of skills, experience, and education
- **Instant Matching**: Get match scores against job descriptions in seconds
- **Skill Gap Analysis**: Identify missing skills and get improvement suggestions
- **Learning Resources**: Curated recommendations for skill development
- **ATS Optimization**: Suggestions to make resumes more ATS-friendly

### For Recruiters
- **Batch Processing**: Upload multiple resumes and match against job descriptions
- **Detailed Analytics**: Comprehensive matching scores and insights
- **Candidate Ranking**: Sort candidates by match percentage
- **Export Reports**: Generate detailed PDF reports for stakeholders

## 🏗️ Architecture

```
[React Frontend]  <--->  [Spring Boot API]  <--->  [AI Matching Engine]
       |                          |                              |
       v                          v                              v
[File Upload]              [Resume Parser]                 [Similarity Analysis]
[Match Dashboard]          [JD Parser]                     [Skill Gap Detection]
[Results Visualization]     [Data Storage]                  [Learning Resources]
```

## 🛠️ Tech Stack

### Backend
- **Java 17** with **Spring Boot 3.2.0**
- **Spring Security** with JWT authentication
- **Spring Data JPA** for data persistence
- **MySQL 8.0** database
- **Apache Tika** for document parsing
- **Apache POI** for Office document handling

### Frontend
- **React 18** with modern hooks
- **Tailwind CSS** for styling
- **React Router** for navigation
- **Axios** for API communication
- **Recharts** for data visualization
- **React Dropzone** for file uploads

### AI & NLP
- **Hugging Face API** integration for advanced NLP tasks
- **Sentence Similarity** for semantic matching
- **Text Classification** for content analysis
- **Zero-Shot Classification** for skill detection

## 📋 Prerequisites

- **Java 17** or higher
- **Node.js 16** or higher
- **MySQL 8.0** or higher
- **Maven 3.6** or higher
- **Hugging Face API Token** (optional, for enhanced AI features)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd jobfit-ai
```

### 2. Backend Setup

#### Database Configuration
1. Create a MySQL database:
```sql
CREATE DATABASE jobfit_ai;
```

2. Update `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
huggingface.api.token=your_huggingface_token
```

#### Run the Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080/api`

### 3. Frontend Setup

#### Install Dependencies
```bash
cd frontend
npm install
```

#### Run the Frontend
```bash
npm start
```

The frontend will be available at `http://localhost:3000`

## 📱 Usage Guide

### 1. User Registration
- Navigate to `/register`
- Choose your role (Job Seeker or Recruiter)
- Create your account

### 2. Resume Upload
- Go to `/upload-resume`
- Drag & drop or browse for your resume file
- Supported formats: PDF, DOC, DOCX
- AI will automatically extract skills and experience

### 3. Job Description Creation
- Navigate to `/job-description`
- Enter job title, company, and description
- AI will analyze requirements and extract key information

### 4. AI Matching
- Visit `/matching-results`
- Select a resume and job description
- Get instant AI-powered matching results
- View detailed skill breakdown and improvement suggestions

## 🔧 Configuration

### Environment Variables
```bash
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=jobfit_ai
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Hugging Face API
HUGGINGFACE_API_TOKEN=your_token
HUGGINGFACE_API_URL=https://api-inference.huggingface.co/models
```

### Customization
- Modify `application.properties` for backend configuration
- Update `tailwind.config.js` for frontend styling
- Customize AI models in `MatchingService.java`

## 📊 API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration

### Resumes
- `POST /api/resumes/upload` - Upload resume
- `GET /api/resumes` - Get user resumes
- `GET /api/resumes/{id}` - Get specific resume
- `DELETE /api/resumes/{id}` - Delete resume

### Job Descriptions
- `POST /api/job-descriptions` - Create job description
- `GET /api/job-descriptions` - Get user job descriptions
- `PUT /api/job-descriptions/{id}` - Update job description
- `DELETE /api/job-descriptions/{id}` - Delete job description

### Matching
- `POST /api/matching/match` - Perform AI matching
- `GET /api/matching/results` - Get matching results

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 🚀 Deployment

### Backend Deployment
```bash
cd backend
mvn clean package
java -jar target/jobfit-ai-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
```bash
cd frontend
npm run build
# Deploy the build folder to your web server
```

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d
```

## 🔒 Security Features

- **JWT Authentication** with secure token management
- **Password Encryption** using BCrypt
- **CORS Configuration** for secure cross-origin requests
- **Input Validation** and sanitization
- **Role-based Access Control** (CANDIDATE, RECRUITER, ADMIN)

## 📈 Performance & Scalability

- **Async Processing** for large file uploads
- **Database Indexing** for fast queries
- **Caching Strategies** for frequently accessed data
- **Horizontal Scaling** ready with containerization
- **Message Queues** for batch processing (future enhancement)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: Check the code comments and this README
- **Issues**: Report bugs and feature requests via GitHub Issues
- **Discussions**: Join community discussions for help and ideas

## 🔮 Roadmap

### Phase 1 (Current)
- ✅ Basic resume and JD parsing
- ✅ AI-powered matching algorithm
- ✅ User authentication and management
- ✅ Basic dashboard and results visualization

### Phase 2 (Next)
- 🔄 Enhanced AI models integration
- 🔄 Batch processing for recruiters
- 🔄 Advanced analytics and reporting
- 🔄 Learning resource integration

### Phase 3 (Future)
- 📋 Multi-language support
- 📋 Mobile application
- 📋 Advanced ATS simulation
- 📋 Integration with job boards

## 🙏 Acknowledgments

- **Hugging Face** for providing excellent NLP models
- **Spring Boot** team for the robust backend framework
- **React** community for the amazing frontend ecosystem
- **Tailwind CSS** for the utility-first CSS framework

---

**Built with ❤️ for the future of job matching** 