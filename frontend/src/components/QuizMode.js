import API_URL from '../config';
import React, { useState, useEffect } from 'react';
import './QuizMode.css';
import ChessBoard from './ChessBoard';
import axios from 'axios';

function isBlackOpening(tree) {
    return tree && tree.playerColor === 'BLACK';
}

function QuizMode({ currentOpening }) {
    const [quizQuestion, setQuizQuestion] = useState(null);
    const [selectedSquare, setSelectedSquare] = useState(null);
    const [userAnswer, setUserAnswer] = useState('');
    const [feedback, setFeedback] = useState(null);
    const [score, setScore] = useState({ correct: 0, total: 0 });

    const shouldFlipBoard = currentOpening && currentOpening.tree && isBlackOpening(currentOpening.tree);
    console.log('=== DEBUG QuizMode ===');
    console.log('currentOpening:', currentOpening);
    console.log('tree:', currentOpening?.tree);
    console.log('playerColor:', currentOpening?.tree?.playerColor);
    console.log('isBlackOpening result:', isBlackOpening(currentOpening?.tree));
    console.log('shouldFlipBoard:', shouldFlipBoard);
    console.log('======================');
    const getNewQuiz = async () => {
        try {
            const response = await axios.post(`${API_URL}/api/training/quiz`, currentOpening.tree);
            setQuizQuestion(response.data);
            setSelectedSquare(null);
            setUserAnswer('');
            setFeedback(null);
        } catch (error) {
            console.error('Error getting quiz:', error);
            alert('Error loading quiz. Make sure backend is running.');
        }
    };
    const checkAnswer = async () => {
        if (!userAnswer.trim()) {
            alert('Please enter a move');
            return;
        }
        try {
            const response = await axios.post(`${API_URL}/api/training/quiz/check`, {
                tree: currentOpening.tree,
                path: quizQuestion.pathToPosition,
                move: userAnswer.trim()
            });
            const isCorrect = response.data.correct;
            setFeedback({
                correct: isCorrect,
                message: isCorrect ? '✓ Correct!' : `✗ Wrong! Correct moves: ${quizQuestion.correctMoves.join(', ')}`
            });
            setScore(prev => ({
                correct: prev.correct + (isCorrect ? 1 : 0),
                total: prev.total + 1
            }));
        } catch (error) {
            console.error('Error checking answer:', error);
            alert('Error checking answer');
        }
    };
    useEffect(() => {
        getNewQuiz();
    }, []);

    const handleSquareClick = (square) => {
        setSelectedSquare(square);
    };
    return (
        <div className="quiz-mode">
            <div className="quiz-header">
                <h2>Quiz Mode</h2>
                <div className="score">
                    Score: {score.correct}/{score.total}
                    {score.total > 0 && ` (${Math.round((score.correct / score.total) * 100)}%)`}
                </div>
            </div>
            {quizQuestion ? (
                <div className="quiz-content">
                    <div className="board-section">
                        <ChessBoard
                            fen={quizQuestion.fen}
                            onSquareClick={handleSquareClick}
                            highlightedSquares={selectedSquare ? [selectedSquare] : []}
                            flipped={shouldFlipBoard}
                        />
                    </div>
                    <div className="answer-section">
                        <div className="path-display">
                            <h4>Line so far:</h4>
                            <div className="moves-list">
                                {quizQuestion.pathToPosition.map((move, i) => (
                                    <span key={i} className="move-item">
                    {Math.floor(i / 2) + 1}.{i % 2 === 0 ? '' : '..'} {move}
                  </span>
                                ))}
                            </div>
                        </div>
                        <div className="answer-input">
                            <label>Your move (e.g., Nf3, d4, O-O):</label>
                            <input
                                type="text"
                                value={userAnswer}
                                onChange={(e) => setUserAnswer(e.target.value)}
                                onKeyPress={(e) => e.key === 'Enter' && checkAnswer()}
                                placeholder="Enter move in SAN notation"
                            />
                            <button onClick={checkAnswer} className="btn btn-primary">
                                Check Answer
                            </button>
                        </div>
                        {feedback && (
                            <div className={`feedback ${feedback.correct ? 'correct' : 'incorrect'}`}>
                                {feedback.message}
                            </div>
                        )}
                        <button onClick={getNewQuiz} className="btn btn-secondary">
                            Next Question
                        </button>
                    </div>
                </div>
            ) : (
                <div className="loading">Loading quiz...</div>
            )}
        </div>
    );
}

export default QuizMode;