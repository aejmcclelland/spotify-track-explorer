# Spotify Track Explorer

Full-stack portfolio project demonstrating **Spring Boot (Java)** backend + **Next.js (TypeScript, Tailwind, daisyUI)** frontend.  
The app allows users to sign in, link their Spotify account, and explore playlists & tracks.  
Built to showcase clean OOP design, API integration, and AWS deployment skills.

---
## 🚀 Tech Highlights - (some of these features are coming soon!)

- **Spring Boot (Java 17)** – REST API with JWT auth, Spotify account linking, and PostgreSQL persistence.
- **Next.js 15 + Tailwind/DaisyUI** – modern UI with client/server components, dark mode, and responsive design.
- **OAuth 2.0 with PKCE** – secure Spotify linking flow, tokens stored/auto-refreshed server-side.
- **PostgreSQL + Flyway** – relational schema with versioned migrations for users, linked Spotify accounts, playlists, and tracks.
- **Testing & CI** – unit tests for services, slice tests for API, GitHub Actions pipeline for build + test.
- **AWS-ready** – designed for Elastic Beanstalk + RDS + Secrets Manager, with CloudWatch monitoring. Can run Next.js on Vercel for cost efficiency.
- **Cost awareness** – architecture runs comfortably in free/low-tier AWS and Vercel, with Budgets/alerts for guardrails.

--

##  Project Structure

- spotify-track-explorer/
- ste-client/   # Next.js frontend
- ste-server/   # Spring Boot backend
- .gitignore
- .editorconfig

---

##  Getting Started

### Backend (Spring Boot)
```bash
cd ste-server
./mvnw spring-boot:run
# or: mvn spring-boot:run
# visit http://localhost:8080/api/ping
```

### Frontend (Next.js)
```bash
cd ste-client
npm install
npm run dev
# visit http://localhost:3000
```

---

##  Tech Stack

- **Backend:** Spring Boot 3, Java 17+
- **Frontend:** Next.js 15, TypeScript, Tailwind CSS, daisyUI
- **Infrastructure:** AWS (planned)

---

##  Roadmap

- [x] Foundations
- [x] Backend skeleton
- [x] App authentication
- [x] Spotify account linking
- [x] Fetch playlists/tracks
- [x] Frontend MVP
- [ ] API docs/metrics
- [ ] AWS deployment
- [ ] Observability
- [ ] Nice-to-have features

---

## CI Status
![Backend CI](https://github.com/aejmcclelland/spotify-track-explorer/actions/workflows/backend.yml/badge.svg?branch=main)
![Frontend CI](https://github.com/aejmcclelland/spotify-track-explorer/actions/workflows/frontend.yml/badge.svg?branch=main)
