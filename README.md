# Chess Opening Trainer

A full-stack web application for building, practicing, and mastering chess opening repertoires. Train with interactive quizzes, play through complete lines, and manage your opening library with an intuitive visual tree interface.

## 🎯 Live Demo

**[Try it now: chess-opening-trainer-omega.vercel.app](https://chess-opening-trainer-omega.vercel.app)**

## ✨ Features

### 📚 Opening Management
- **Visual Tree View**: Browse your opening repertoire as an interactive tree structure
- **File-Based System**: Save and load opening files (.opening format) locally
- **Multiple Lines**: Build complex repertoires with multiple variations and transpositions
- **Enable/Disable Lines**: Toggle specific variations for focused training

### ✏️ Opening Editor
- **PGN Import**: Add lines by pasting PGN notation
- **Interactive Recording**: Click and drag pieces to record new lines
- **Branch from Any Position**: Start new variations from any point in your repertoire
- **Smart Validation**: Automatic move legality checking with full chess rules

### 🧠 Training Modes

#### Quiz Mode
- Random position testing from your repertoire
- Instant feedback on correct/incorrect moves
- Score tracking with accuracy percentage
- Optional hints for learning

#### Play-through Mode
- Practice complete opening lines against computer responses
- Auto-advancing after correct moves
- Choose starting positions within your repertoire
- Line completion tracking

### ♟️ Chess Engine
- Full chess rules implementation from scratch
- Legal move generation with check/checkmate detection
- Support for castling, en passant, and pawn promotion
- FEN notation for position representation
- SAN (Standard Algebraic Notation) move parsing

## 🛠️ Technology Stack

### Backend
- **Java 21** with Spring Boot 3.5.7
- **Maven** for build management
- **JUnit 5** for testing
- Custom chess engine implementation
- RESTful API design

### Frontend
- **React 18** with functional components and hooks
- **Axios** for API communication
- Custom drag-and-drop chess board
- Responsive CSS design
- No external chess libraries - everything built from scratch

### DevOps
- **CI/CD**: GitHub Actions with automated testing
- **Backend Hosting**: Render (Docker deployment)
- **Frontend Hosting**: Vercel
- **Version Control**: Git with Commitizen for clean commits

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Node.js 16+ and npm
- Git

### Local Development

#### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```
The backend will start on `http://localhost:8080`

#### Frontend Setup
```bash
cd frontend
npm install
npm start
```
The frontend will start on `http://localhost:3000`

### Running Tests
```bash
cd backend
./mvnw test
```

## 📖 User Guide

### Getting Started

1. **Visit the app**: [chess-opening-trainer-omega.vercel.app](https://chess-opening-trainer-omega.vercel.app)

2. **Create a new opening**:
    - Click "New Opening" in the right panel
    - Enter a name (e.g., "My London System")
    - Choose your color (WHITE or BLACK)

3. **Add opening lines**:
    - Switch to "Editor" mode
    - Use either method:
        - **PGN Import**: Paste moves like `1. d4 Nf6 2. Bf4 d5 3. e3`
        - **Interactive Recording**: Click "Start Recording" and make moves on the board

### Training Your Openings

#### Quiz Mode
1. Load an opening and switch to "Quiz Mode"
2. Click "Start Quiz"
3. Make your move by clicking/dragging pieces
4. Get instant feedback and automatic progression
5. Track your accuracy over time

#### Play-through Mode
1. Switch to "Play-through" mode
2. Optionally select a starting position from your tree
3. Click "Start Play-through"
4. Play through the line - the computer will respond automatically
5. Practice until the line is complete

### Managing Opening Files

- **Save**: Click "Download Opening" to save as a `.opening` file
- **Load**: Click "Upload Opening" to load a previously saved file
- **Edit**: Use the tree view to enable/disable specific lines

## 🏗️ Architecture

### Project Structure
```
chess-opening-trainer/
├── backend/                 # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/ogmikee/chess/
│   │       ├── controller/  # REST API endpoints
│   │       ├── model/       # Chess domain models
│   │       ├── logic/       # Game logic & move generation
│   │       ├── opening/     # Opening tree management
│   │       ├── pgn/         # PGN parsing
│   │       ├── service/     # Training services
│   │       └── config/      # Spring configuration
│   └── src/test/           # JUnit tests
├── frontend/               # React frontend
│   └── src/
│       ├── components/     # React components
│       ├── assets/         # Chess piece images
│       └── config.js       # API configuration
└── .github/workflows/      # CI/CD pipelines
```

### Key Design Decisions

- **Stateless Backend**: Opening trees sent with each request for simplicity
- **File-Based Storage**: Users manage their own .opening files locally
- **Custom Chess Engine**: Full implementation from scratch for learning and control
- **Merged Tree Structure**: Efficient representation of transpositions and variations

## 🧪 Testing

The project includes comprehensive test coverage:
- Chess engine move generation
- Opening tree operations
- PGN parsing
- Line validation

Tests run automatically on every push via GitHub Actions.

## 📝 API Documentation

### Chess Endpoints
- `POST /api/chess/legal-moves` - Get legal moves for a position
- `POST /api/chess/execute-move` - Execute and validate a move
- `POST /api/chess/get-position` - Get FEN from move sequence

### Opening Endpoints
- `POST /api/opening/parse-pgn` - Parse PGN into move list
- `POST /api/opening/validate-line` - Validate move sequence

### Training Endpoints
- `POST /api/training/quiz` - Get random quiz question
- `POST /api/training/playthrough/start` - Start play-through session
- `POST /api/training/playthrough/move` - Process user move in play-through

## 🚢 Deployment

The application is deployed with:
- **Backend**: Render (free tier with cold starts)
- **Frontend**: Vercel (free tier)
- **CI/CD**: GitHub Actions

Note: The backend may take ~30 seconds to wake up after periods of inactivity (free tier limitation).

## 🤝 Contributing

This is a portfolio project, but feedback and suggestions are welcome! Feel free to open an issue.

## 📄 License

MIT License

Copyright (c) 2025 OGMikee

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## 👤 Author

**OGMikee**
- GitHub: [@OGMikee](https://github.com/OGMikee)
- Portfolio Project - Built to demonstrate full-stack development skills

## 🙏 Acknowledgments

- Chess piece images from [insert source if applicable]
- Built as a learning project to showcase software engineering skills
- No external chess libraries used - engine built from scratch

---

**Note for Recruiters**: This project demonstrates full-stack development, testing, CI/CD, deployment, and architectural decision-making. The chess engine and all components were built from scratch without using existing chess libraries.