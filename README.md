# Spotify Track Explorer

Full-stack portfolio project demonstrating **Spring Boot (Java)** backend + **Next.js (TypeScript, Tailwind, daisyUI)** frontend.  
The app allows users to sign in, link their Spotify account, and explore playlists, tracks, and audio features.  
Built to showcase clean OOP design, API integration, and AWS deployment skills.

---

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
![Backend CI](https://github.com/aejmcclelland/spotify-track-explorer/actions/workflows/backend.yml)
![Frontend CI](https://github.com/aejmcclelland/spotify-track-explorer/actions/workflows/frontend.yml)
