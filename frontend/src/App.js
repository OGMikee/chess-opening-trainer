import React, { useState, useEffect } from 'react';
import './App.css';
import ChessBoard from './components/ChessBoard';
import FileManager from './components/FileManager';
import TreeViewer from './components/TreeViewer';
import axios from 'axios';

function App() {
    const [currentOpening, setCurrentOpening] = useState(null);
    const [view, setView] = useState('home');
    const [selectedPath, setSelectedPath] = useState([]);
    const [startFromPath, setStartFromPath] = useState([]);
    const [isRecording, setIsRecording] = useState(false);
    const [moveHistory, setMoveHistory] = useState([]);
    const [quizQuestion, setQuizQuestion] = useState(null);
    const [quizScore, setQuizScore] = useState({ correct: 0, total: 0 });
    const [quizFeedback, setQuizFeedback] = useState(null);
    const [playthroughSession, setPlaythroughSession] = useState(null);
    const [playthroughFeedback, setPlaythroughFeedback] = useState(null);
    const [playthroughStartPath, setPlaythroughStartPath] = useState([]);

    const handleQuizMove = async (from, to) => {
        if (!quizQuestion) return;

        try {
            const response = await axios.post('http://localhost:8080/api/chess/execute-move', {
                fen: quizQuestion.fen,
                from: from,
                to: to
            });

            if (!response.data.success) {
                setQuizFeedback({
                    correct: false,
                    message: '✗ Illegal move!',
                    showHelp: false
                });
                return;
            }

            const move = response.data.move;
            const isCorrect = quizQuestion.correctMoves.includes(move);

            if (isCorrect) {
                const newFen = response.data.newFen;
                setQuizQuestion({ ...quizQuestion, fen: newFen });

                setQuizFeedback({
                    correct: true,
                    message: '✓ Correct!',
                    showHelp: false
                });

                setQuizScore(prev => ({
                    correct: prev.correct + 1,
                    total: prev.total + 1
                }));

                setTimeout(async () => {
                    try {
                        const response = await axios.post('http://localhost:8080/api/training/quiz', currentOpening.tree);
                        setQuizQuestion(response.data);
                        setQuizFeedback(null);
                    } catch (error) {
                        console.error('Error getting next quiz:', error);
                    }
                }, 1000);
            } else {
                setQuizFeedback({
                    correct: false,
                    message: '✗ Wrong!',
                    showHelp: true
                });

                setQuizScore(prev => ({
                    correct: prev.correct,
                    total: prev.total + 1
                }));
            }
        } catch (error) {
            console.error('Error checking answer:', error);
        }
    };

    const handlePlaythroughMove = async (from, to) => {
        if (!playthroughSession) return;

        try {
            const moveResponse = await axios.post('http://localhost:8080/api/chess/execute-move', {
                fen: playthroughSession.gameFen,
                from: from,
                to: to
            });

            if (!moveResponse.data.success) {
                setPlaythroughFeedback({
                    type: 'error',
                    message: '✗ Illegal move!'
                });
                return;
            }

            const move = moveResponse.data.move;

            const response = await axios.post('http://localhost:8080/api/training/playthrough/move', {
                tree: currentOpening.tree,
                movesPlayed: playthroughSession.movesPlayed,
                move: move
            });

            const result = response.data;

            if (result.correct) {
                const newMovesPlayed = [...playthroughSession.movesPlayed, move];

                if (result.computerMove) {
                    newMovesPlayed.push(result.computerMove);
                }

                const newFenResponse = await axios.post('http://localhost:8080/api/chess/get-position', {
                    moves: newMovesPlayed
                });

                setPlaythroughSession({
                    ...playthroughSession,
                    movesPlayed: newMovesPlayed,
                    gameFen: newFenResponse.data.fen,
                    complete: result.lineComplete
                });

                if (result.lineComplete) {
                    setPlaythroughFeedback({
                        type: 'success',
                        message: '✓ Line Complete!'
                    });
                } else {
                    setPlaythroughFeedback({
                        type: 'success',
                        message: result.computerMove
                            ? `✓ Correct! Computer played: ${result.computerMove}`
                            : '✓ Correct!'
                    });
                }
            } else {
                setPlaythroughFeedback({
                    type: 'error',
                    message: '✗ Incorrect move! That\'s not in your repertoire.'
                });
            }
        } catch (error) {
            console.error('Error processing move:', error);
        }
    };

    return (
        <div className="App">
            <div className="sidebar">
                <h1>♔ Chess Trainer</h1>
                <nav>
                    <button
                        className={view === 'home' ? 'active' : ''}
                        onClick={() => setView('home')}
                    >
                        Home
                    </button>
                    <button
                        className={view === 'editor' ? 'active' : ''}
                        onClick={() => setView('editor')}
                    >
                        Editor
                    </button>
                    <button
                        className={view === 'quiz' ? 'active' : ''}
                        onClick={() => setView('quiz')}
                    >
                        Quiz Mode
                    </button>
                    <button
                        className={view === 'playthrough' ? 'active' : ''}
                        onClick={() => setView('playthrough')}
                    >
                        Play-through
                    </button>
                </nav>
            </div>

            <div className="board-container">
                {view === 'home' && <HomeBoard currentOpening={currentOpening} setCurrentOpening={setCurrentOpening} selectedPath={selectedPath} setSelectedPath={setSelectedPath} />}
                {view === 'editor' && <EditorBoard currentOpening={currentOpening} startFromPath={startFromPath} isRecording={isRecording} moveHistory={moveHistory} setMoveHistory={setMoveHistory} />}
                {view === 'quiz' && <QuizBoard currentOpening={currentOpening} quizQuestion={quizQuestion} onMove={handleQuizMove} />}
                {view === 'playthrough' && <PlaythroughBoard currentOpening={currentOpening} playthroughSession={playthroughSession} onMove={handlePlaythroughMove} />}
            </div>

            <div className="right-panel">
                {view === 'home' && <HomePanel currentOpening={currentOpening} setCurrentOpening={setCurrentOpening} />}
                {view === 'editor' && <EditorPanel currentOpening={currentOpening} setCurrentOpening={setCurrentOpening} startFromPath={startFromPath} setStartFromPath={setStartFromPath} isRecording={isRecording} setIsRecording={setIsRecording} moveHistory={moveHistory} setMoveHistory={setMoveHistory} />}
                {view === 'quiz' && <QuizPanel currentOpening={currentOpening} quizQuestion={quizQuestion} setQuizQuestion={setQuizQuestion} quizScore={quizScore} quizFeedback={quizFeedback} />}
                {view === 'playthrough' && <PlaythroughPanel currentOpening={currentOpening} setCurrentOpening={setCurrentOpening} playthroughSession={playthroughSession} setPlaythroughSession={setPlaythroughSession} playthroughFeedback={playthroughFeedback} setPlaythroughFeedback={setPlaythroughFeedback} playthroughStartPath={playthroughStartPath} setPlaythroughStartPath={setPlaythroughStartPath} />}
            </div>
        </div>
    );
}

function HomeBoard({ currentOpening, setCurrentOpening, selectedPath, setSelectedPath }) {
    const [boardFen, setBoardFen] = useState('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1');

    useEffect(() => {
        if (selectedPath && selectedPath.length > 0) {
            const fetchPosition = async () => {
                try {
                    const response = await axios.post('http://localhost:8080/api/chess/get-position', {
                        moves: selectedPath
                    });
                    if (response.data.fen) {
                        setBoardFen(response.data.fen);
                    }
                } catch (error) {
                    console.error('Error getting position:', error);
                }
            };
            fetchPosition();
        } else {
            setBoardFen('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1');
        }
    }, [selectedPath]);

    if (!currentOpening) {
        return (
            <div className="home-empty">
                <h2>Welcome to Chess Opening Trainer</h2>
                <p>Load or create an opening to get started</p>
            </div>
        );
    }

    const handleToggleLine = (path) => {
        if (!currentOpening) return;

        const updatedOpening = { ...currentOpening };
        let node = updatedOpening.tree.root;

        for (let move of path) {
            node = node.children.find(n => n.move === move);
            if (!node) return;
        }

        node.enabled = !node.enabled;
        setCurrentOpening(updatedOpening);
    };

    const handleNodeClick = (path) => {
        setSelectedPath(path);
    };

    return (
        <div className="home-content">
            <div className="tree-section-large">
                <h2>Opening Lines - {currentOpening.name}</h2>
                <TreeViewer
                    opening={currentOpening}
                    onToggleLine={handleToggleLine}
                    onNodeClick={handleNodeClick}
                />
            </div>

            {selectedPath.length > 0 && (
                <div className="board-preview-floating">
                    <h4>Position Preview</h4>
                    <ChessBoard fen={boardFen} size="small" />
                </div>
            )}
        </div>
    );
}

function HomePanel({ currentOpening, setCurrentOpening }) {
    return (
        <>
            <div className="right-panel-section">
                <h3>File Management</h3>
                <FileManager
                    currentOpening={currentOpening}
                    setCurrentOpening={setCurrentOpening}
                />
            </div>

            <div className="right-panel-section">
                <h3>Opening Info</h3>
                {currentOpening ? (
                    <div className="opening-info">
                        <p><strong>Name:</strong> {currentOpening.name}</p>
                        <p><strong>Color:</strong> {currentOpening.tree.playerColor}</p>
                    </div>
                ) : (
                    <p className="no-opening">No opening loaded</p>
                )}
            </div>
        </>
    );
}

function EditorBoard({ currentOpening, startFromPath, isRecording, moveHistory, setMoveHistory }) {
    const [boardFen, setBoardFen] = useState('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1');

    useEffect(() => {
        if (startFromPath && startFromPath.length > 0) {
            const fetchPosition = async () => {
                try {
                    const response = await axios.post('http://localhost:8080/api/chess/get-position', {
                        moves: startFromPath
                    });
                    if (response.data.fen) {
                        setBoardFen(response.data.fen);
                    }
                } catch (error) {
                    console.error('Error getting position:', error);
                }
            };
            fetchPosition();
        } else {
            setBoardFen('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1');
        }
    }, [startFromPath]);

    useEffect(() => {
        if (!isRecording) {
            if (startFromPath && startFromPath.length > 0) {
                const fetchPosition = async () => {
                    const response = await axios.post('http://localhost:8080/api/chess/get-position', {
                        moves: startFromPath
                    });
                    if (response.data.fen) {
                        setBoardFen(response.data.fen);
                    }
                };
                fetchPosition();
            } else {
                setBoardFen('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1');
            }
        }
    }, [isRecording, startFromPath]);

    if (!currentOpening) {
        return (
            <div className="home-empty">
                <h2>Editor</h2>
                <p>Load an opening to start editing</p>
            </div>
        );
    }

    const handleMove = async (from, to) => {
        if (!isRecording) return;

        try {
            const response = await axios.post('http://localhost:8080/api/chess/execute-move', {
                fen: boardFen,
                from: from,
                to: to
            });

            if (response.data.success) {
                const move = response.data.move;
                setMoveHistory([...moveHistory, move]);
                setBoardFen(response.data.newFen);
            }
        } catch (error) {
            console.error('Move error:', error);
        }
    };

    return (
        <ChessBoard
            fen={boardFen}
            onMove={isRecording ? handleMove : null}
        />
    );
}

function EditorPanel({ currentOpening, setCurrentOpening, startFromPath, setStartFromPath, isRecording, setIsRecording, moveHistory, setMoveHistory }) {
    const [pgnInput, setPgnInput] = useState('');

    const handleParsePgn = async () => {
        if (!pgnInput.trim()) {
            alert('Please enter PGN text');
            return;
        }

        try {
            const response = await axios.post('http://localhost:8080/api/opening/parse-pgn', {
                pgn: pgnInput
            });

            const moves = response.data.moves;
            const fullLine = [...(startFromPath || []), ...moves];

            const validationResponse = await axios.post('http://localhost:8080/api/opening/validate-line', {
                moves: fullLine
            });

            if (validationResponse.data.valid) {
                const updatedOpening = { ...currentOpening };
                addLineToTree(updatedOpening.tree, fullLine);
                setCurrentOpening(updatedOpening);
                setPgnInput('');
                alert('Line added successfully!');
            } else {
                alert('Invalid moves');
            }
        } catch (error) {
            console.error('Error parsing PGN:', error);
            alert('Error parsing PGN');
        }
    };

    const startRecording = () => {
        setIsRecording(true);
        setMoveHistory([]);
    };

    const undoMove = async () => {
        if (moveHistory.length === 0) return;
        const newHistory = moveHistory.slice(0, -1);
        setMoveHistory(newHistory);
    };

    const saveRecordedLine = async () => {
        if (moveHistory.length === 0) {
            alert('No moves recorded');
            return;
        }

        try {
            const fullLine = [...(startFromPath || []), ...moveHistory];
            const response = await axios.post('http://localhost:8080/api/opening/validate-line', {
                moves: fullLine
            });

            if (response.data.valid) {
                const updatedOpening = { ...currentOpening };
                addLineToTree(updatedOpening.tree, fullLine);
                setCurrentOpening(updatedOpening);
                alert('Line added successfully!');
                setIsRecording(false);
                setMoveHistory([]);
            }
        } catch (error) {
            console.error('Save error:', error);
            alert('Error saving line');
        }
    };

    const addLineToTree = (tree, moves) => {
        let currentNode = tree.root;

        for (let i = 0; i < moves.length; i++) {
            const move = moves[i];
            const isPlayerMove = tree.playerColor === 'WHITE' ? i % 2 === 0 : i % 2 === 1;

            let child = currentNode.children.find(n => n.move === move);

            if (!child) {
                child = {
                    move: move,
                    enabled: true,
                    children: [],
                    playerMove: isPlayerMove
                };
                currentNode.children.push(child);
            }

            currentNode = child;
        }
    };

    const handleNodeClick = (path) => {
        setStartFromPath(path);
    };

    const handleBreadcrumbClick = (index) => {
        setStartFromPath(startFromPath.slice(0, index + 1));
    };

    return (
        <>
            <div className="right-panel-section">
                <h3>File Management</h3>
                <FileManager
                    currentOpening={currentOpening}
                    setCurrentOpening={setCurrentOpening}
                />
            </div>

            {currentOpening ? (
                <>
                    <div className="right-panel-section">
                        <h3>Record Line</h3>
                        {!isRecording ? (
                            <button onClick={startRecording} className="btn btn-primary">
                                Start Recording
                            </button>
                        ) : (
                            <div className="recording-active">
                                <div className="recording-indicator">
                                    <span className="recording-dot"></span>
                                    Recording...
                                </div>
                                <div className="move-history-display">
                                    {moveHistory.map((move, i) => (
                                        <span key={i} className="move-chip">
                                            {Math.floor((startFromPath?.length || 0) / 2) + Math.floor(i / 2) + 1}.{((startFromPath?.length || 0) + i) % 2 === 0 ? '' : '..'} {move}
                                        </span>
                                    ))}
                                </div>
                                <button onClick={undoMove} className="btn btn-secondary" disabled={moveHistory.length === 0}>
                                    Undo Move
                                </button>
                                <button onClick={saveRecordedLine} className="btn btn-success">
                                    Save Line
                                </button>
                                <button onClick={() => {
                                    setIsRecording(false);
                                    setMoveHistory([]);
                                }} className="btn btn-secondary">
                                    Cancel
                                </button>
                            </div>
                        )}
                    </div>

                    <div className="right-panel-section">
                        <h3>Branch Position</h3>
                        {startFromPath && startFromPath.length > 0 ? (
                            <div className="tree-breadcrumb">
                                <span
                                    className="tree-breadcrumb-item"
                                    onClick={() => setStartFromPath([])}
                                >
                                    Start
                                </span>
                                {startFromPath.map((move, i) => (
                                    <React.Fragment key={i}>
                                        <span className="tree-breadcrumb-separator">→</span>
                                        <span
                                            className="tree-breadcrumb-item"
                                            onClick={() => handleBreadcrumbClick(i)}
                                        >
                                            {Math.floor(i / 2) + 1}.{i % 2 === 0 ? '' : '..'} {move}
                                        </span>
                                    </React.Fragment>
                                ))}
                            </div>
                        ) : (
                            <p className="no-opening">Starting from initial position</p>
                        )}
                    </div>

                    <div className="right-panel-section">
                        <h3>Select Branch Point</h3>
                        <p style={{ fontSize: '0.85rem', color: '#aaa', marginBottom: '0.5rem' }}>
                            Click any move to branch from that position
                        </p>
                        <TreeViewer
                            opening={currentOpening}
                            onToggleLine={(path) => {
                                const updatedOpening = { ...currentOpening };
                                let node = updatedOpening.tree.root;
                                for (let move of path) {
                                    node = node.children.find(n => n.move === move);
                                    if (!node) return;
                                }
                                node.enabled = !node.enabled;
                                setCurrentOpening(updatedOpening);
                            }}
                            onNodeClick={handleNodeClick}
                        />
                    </div>

                    <div className="right-panel-section">
                        <h3>Add Line via PGN</h3>
                        <textarea
                            value={pgnInput}
                            onChange={(e) => setPgnInput(e.target.value)}
                            placeholder="1. e4 e5 2. Nf3 Nc6"
                            rows={5}
                            className="pgn-input"
                        />
                        <button onClick={handleParsePgn} className="btn btn-primary">
                            Add PGN Line
                        </button>
                    </div>
                </>
            ) : (
                <p className="no-opening">Load or create an opening to start editing</p>
            )}
        </>
    );
}

function QuizBoard({ currentOpening, quizQuestion, onMove }) {
    if (!currentOpening) {
        return (
            <div className="home-empty">
                <h2>Quiz Mode</h2>
                <p>Load an opening to start quiz</p>
            </div>
        );
    }

    if (!quizQuestion) {
        return (
            <div className="home-empty">
                <h2>Quiz Mode</h2>
                <p>Click "Start Quiz" to begin</p>
            </div>
        );
    }

    return <ChessBoard fen={quizQuestion.fen} onMove={onMove} />;
}

function QuizPanel({ currentOpening, quizQuestion, setQuizQuestion, quizScore, quizFeedback }) {
    const [showHelp, setShowHelp] = useState(false);

    useEffect(() => {
        setShowHelp(false);
    }, [quizQuestion]);

    const getNewQuestion = async () => {
        if (!currentOpening) return;

        try {
            const response = await axios.post('http://localhost:8080/api/training/quiz', currentOpening.tree);
            setQuizQuestion(response.data);
            setShowHelp(false);
        } catch (error) {
            console.error('Error getting quiz:', error);
            alert('Error loading quiz');
        }
    };

    if (!currentOpening) {
        return (
            <>
                <h3>Quiz Mode</h3>
                <p className="no-opening">Load an opening first</p>
            </>
        );
    }

    return (
        <>
            <div className="right-panel-section">
                <h3>Quiz Mode</h3>
                <div className="score-display">
                    <p><strong>Score:</strong> {quizScore.correct}/{quizScore.total}</p>
                    {quizScore.total > 0 && (
                        <p><strong>Accuracy:</strong> {Math.round((quizScore.correct / quizScore.total) * 100)}%</p>
                    )}
                </div>
            </div>

            <div className="right-panel-section">
                <button onClick={getNewQuestion} className="btn btn-primary">
                    {quizQuestion ? 'Skip Question' : 'Start Quiz'}
                </button>
            </div>

            {quizQuestion && (
                <>
                    <div className="right-panel-section">
                        <h3>Line so far:</h3>
                        <div className="move-history-display">
                            {quizQuestion.pathToPosition.map((move, i) => (
                                <span key={i} className="move-chip">
                                    {Math.floor(i / 2) + 1}.{i % 2 === 0 ? '' : '..'} {move}
                                </span>
                            ))}
                        </div>
                    </div>

                    <div className="right-panel-section">
                        <h3>Your Move:</h3>
                        <p style={{ fontSize: '0.9rem', color: '#aaa' }}>
                            Make your move on the board
                        </p>
                        {quizFeedback && !quizFeedback.correct && (
                            <>
                                <button
                                    onClick={() => setShowHelp(!showHelp)}
                                    className="btn btn-secondary"
                                    style={{ marginTop: '0.5rem' }}
                                >
                                    {showHelp ? 'Hide Help' : 'Show Help'}
                                </button>
                                {showHelp && (
                                    <div className="help-box">
                                        <p><strong>Correct moves:</strong></p>
                                        <p>{quizQuestion.correctMoves.join(', ')}</p>
                                    </div>
                                )}
                            </>
                        )}
                    </div>

                    {quizFeedback && (
                        <div className="right-panel-section">
                            <div className={`feedback-box ${quizFeedback.correct ? 'correct' : 'incorrect'}`}>
                                {quizFeedback.message}
                            </div>
                        </div>
                    )}
                </>
            )}
        </>
    );
}

function PlaythroughBoard({ currentOpening, playthroughSession, onMove }) {
    if (!currentOpening) {
        return (
            <div className="home-empty">
                <h2>Play-through Mode</h2>
                <p>Load an opening to start</p>
            </div>
        );
    }

    if (!playthroughSession) {
        return (
            <div className="home-empty">
                <h2>Play-through Mode</h2>
                <p>Click "Start Play-through" to begin</p>
            </div>
        );
    }

    return <ChessBoard fen={playthroughSession.gameFen} onMove={onMove} />;
}

function PlaythroughPanel({ currentOpening, setCurrentOpening, playthroughSession, setPlaythroughSession, playthroughFeedback, setPlaythroughFeedback, playthroughStartPath, setPlaythroughStartPath }) {
    const [showSettings, setShowSettings] = useState(false);

    const startPlaythrough = async () => {
        if (!currentOpening) return;

        try {
            const response = await axios.post('http://localhost:8080/api/training/playthrough/start', {
                tree: currentOpening.tree,
                startPath: playthroughStartPath
            });
            setPlaythroughSession(response.data);
            setPlaythroughFeedback(null);
            setShowSettings(false);
        } catch (error) {
            console.error('Error starting playthrough:', error);
            alert('Error starting playthrough');
        }
    };

    const handleBreadcrumbClick = (index) => {
        setPlaythroughStartPath(playthroughStartPath.slice(0, index + 1));
    };

    if (!currentOpening) {
        return (
            <>
                <h3>Play-through Mode</h3>
                <p className="no-opening">Load an opening first</p>
            </>
        );
    }

    return (
        <>
            <div className="right-panel-section">
                <h3>Play-through Mode</h3>
                {!playthroughSession && (
                    <button
                        onClick={() => setShowSettings(!showSettings)}
                        className="btn btn-secondary"
                        style={{ marginBottom: '0.5rem' }}
                    >
                        {showSettings ? 'Hide Settings' : 'Settings'}
                    </button>
                )}
                <button onClick={startPlaythrough} className="btn btn-primary">
                    {playthroughSession ? 'Restart' : 'Start Play-through'}
                </button>
            </div>

            {showSettings && !playthroughSession && (
                <>
                    <div className="right-panel-section">
                        <h3>Start From Position</h3>
                        {playthroughStartPath && playthroughStartPath.length > 0 ? (
                            <div className="tree-breadcrumb">
                                <span
                                    className="tree-breadcrumb-item"
                                    onClick={() => setPlaythroughStartPath([])}
                                >
                                    Start
                                </span>
                                {playthroughStartPath.map((move, i) => (
                                    <React.Fragment key={i}>
                                        <span className="tree-breadcrumb-separator">→</span>
                                        <span
                                            className="tree-breadcrumb-item"
                                            onClick={() => handleBreadcrumbClick(i)}
                                        >
                                            {Math.floor(i / 2) + 1}.{i % 2 === 0 ? '' : '..'} {move}
                                        </span>
                                    </React.Fragment>
                                ))}
                            </div>
                        ) : (
                            <p className="no-opening">Starting from initial position</p>
                        )}
                    </div>

                    <div className="right-panel-section">
                        <h3>Select Lines</h3>
                        <p style={{ fontSize: '0.85rem', color: '#aaa', marginBottom: '0.5rem' }}>
                            Click moves to select start position. Toggle switches to enable/disable lines.
                        </p>
                        <TreeViewer
                            opening={currentOpening}
                            onToggleLine={(path) => {
                                const updatedOpening = { ...currentOpening };
                                let node = updatedOpening.tree.root;
                                for (let move of path) {
                                    node = node.children.find(n => n.move === move);
                                    if (!node) return;
                                }
                                node.enabled = !node.enabled;
                                setCurrentOpening(updatedOpening);
                            }}
                            onNodeClick={setPlaythroughStartPath}
                        />
                    </div>
                </>
            )}

            {playthroughSession && (
                <>
                    <div className="right-panel-section">
                        <h3>Moves Played:</h3>
                        <div className="move-history-display">
                            {playthroughSession.movesPlayed.map((move, i) => (
                                <span key={i} className="move-chip">
                                    {Math.floor(i / 2) + 1}.{i % 2 === 0 ? '' : '..'} {move}
                                </span>
                            ))}
                            {playthroughSession.movesPlayed.length === 0 && (
                                <p className="no-opening">No moves yet. You play first!</p>
                            )}
                        </div>
                    </div>

                    {playthroughFeedback && (
                        <div className="right-panel-section">
                            <div className={`feedback-box ${playthroughFeedback.type}`}>
                                {playthroughFeedback.message}
                            </div>
                        </div>
                    )}
                </>
            )}
        </>
    );
}

export default App;