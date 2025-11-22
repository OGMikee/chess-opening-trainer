import React, { useState, useEffect } from 'react';
import './PlaythroughMode.css';
import ChessBoard from './ChessBoard';
import axios from 'axios';

function PlaythroughMode({ currentOpening }) {
    const [session, setSession] = useState(null);
    const [userMove, setUserMove] = useState('');
    const [feedback, setFeedback] = useState(null);
    const [moveHistory, setMoveHistory] = useState([]);

    const startSession = async () => {
        try {
            const response = await axios.post('http://localhost:8080/api/training/playthrough/start', {
                tree: currentOpening.tree,
                startPath: []
            });
            setSession(response.data);
            setMoveHistory([]);
            setFeedback(null);
            setUserMove('');
        } catch (error) {
            console.error('Error starting playthrough:', error);
            alert('Error starting playthrough. Make sure backend is running.');
        }
    };
    const submitMove = async () => {
        if (!userMove.trim()) {
            alert('Please enter a move');
            return;
        }
        try {
            const response = await axios.post('http://localhost:8080/api/training/playthrough/move', {
                session: session,
                move: userMove.trim()
            });
            const result = response.data;
            if (result.correct) {
                setMoveHistory([...session.movesPlayed, userMove.trim()]);
                setFeedback({
                    type: 'success',
                    message: result.computerMove
                        ? `✓ Correct! Computer played: ${result.computerMove}`
                        : '✓ Correct! Line complete!'
                });
                setSession(response.data.session || {
                    ...session,
                    movesPlayed: [...session.movesPlayed, userMove.trim(), result.computerMove].filter(Boolean),
                    complete: result.lineComplete,
                    gameFen: result.fen || session.gameFen
                });
                if (result.lineComplete) {
                    setTimeout(() => {
                        alert('Line complete! Great job!');
                    }, 500);
                }
            } else {
                setFeedback({
                    type: 'error',
                    message: '✗ Incorrect move! That\'s not in your repertoire.'
                });
            }
            setUserMove('');
        } catch (error) {
            console.error('Error submitting move:', error);
            alert('Error processing move');
        }
    };

    useEffect(() => {
        startSession();
    }, []);

    return (
        <div className="playthrough-mode">
            <div className="playthrough-header">
                <h2>Play-through Mode</h2>
                <button onClick={startSession} className="btn btn-secondary">
                    Restart
                </button>
            </div>
            {session ? (
                <div className="playthrough-content">
                    <div className="board-section">
                        <ChessBoard
                            fen={session.gameFen}
                        />
                    </div>
                    <div className="control-section">
                        <div className="move-history">
                            <h4>Moves Played:</h4>
                            <div className="moves-list">
                                {session.movesPlayed.map((move, i) => (
                                    <span key={i} className="move-item">
                    {Math.floor(i / 2) + 1}.{i % 2 === 0 ? '' : '..'} {move}
                  </span>
                                ))}
                            </div>
                            {session.movesPlayed.length === 0 && (
                                <p className="no-moves">No moves yet. You play first!</p>
                            )}
                        </div>
                        {!session.complete ? (
                            <div className="move-input">
                                <label>Your move:</label>
                                <input
                                    type="text"
                                    value={userMove}
                                    onChange={(e) => setUserMove(e.target.value)}
                                    onKeyPress={(e) => e.key === 'Enter' && submitMove()}
                                    placeholder="e.g., Nf3, d4, O-O"
                                />
                                <button onClick={submitMove} className="btn btn-primary">
                                    Submit Move
                                </button>
                            </div>
                        ) : (
                            <div className="complete-message">
                                <h3>✓ Line Complete!</h3>
                                <p>Great job! You completed this line.</p>
                                <button onClick={startSession} className="btn btn-success">
                                    Play Again
                                </button>
                            </div>
                        )}
                        {feedback && (
                            <div className={`feedback ${feedback.type}`}>
                                {feedback.message}
                            </div>
                        )}
                    </div>
                </div>
            ) : (
                <div className="loading">Loading playthrough...</div>
            )}
        </div>
    );
}

export default PlaythroughMode;