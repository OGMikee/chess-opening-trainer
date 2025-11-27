import API_URL from '../config';
import React, { useState } from 'react';
import './ChessBoard.css';
import axios from 'axios';

import wk from '../assets/pieces/wk.png';
import wq from '../assets/pieces/wq.png';
import wr from '../assets/pieces/wr.png';
import wb from '../assets/pieces/wb.png';
import wn from '../assets/pieces/wn.png';
import wp from '../assets/pieces/wp.png';
import bk from '../assets/pieces/bk.png';
import bq from '../assets/pieces/bq.png';
import br from '../assets/pieces/br.png';
import bb from '../assets/pieces/bb.png';
import bn from '../assets/pieces/bn.png';
import bp from '../assets/pieces/bp.png';

const PIECE_IMAGES = {
    'K': wk, 'Q': wq, 'R': wr, 'B': wb, 'N': wn, 'P': wp,
    'k': bk, 'q': bq, 'r': br, 'b': bb, 'n': bn, 'p': bp
};

function ChessBoard({ fen, onMove, highlightedSquares = [], showWrongMove = false, wrongMoveSquare = null, size = 'normal', flipped = false }) {
    const [selectedSquare, setSelectedSquare] = useState(null);
    const [legalMoves, setLegalMoves] = useState([]);
    const [draggedFrom, setDraggedFrom] = useState(null);
    const [dragPreview, setDragPreview] = useState(null);
    const [isDragging, setIsDragging] = useState(false);
    const [mouseDownPos, setMouseDownPos] = useState(null);

    const board = fenToBoard(fen || 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1');
    const currentTurn = fen ? fen.split(' ')[1] : 'w';

    const fetchLegalMoves = async (square) => {
        try {
            const response = await axios.post(`${API_URL}/api/chess/legal-moves`, {
                fen: fen,
                square: square
            });
            setLegalMoves(response.data.moves || []);
        } catch (error) {
            console.error('Error fetching legal moves:', error);
            setLegalMoves([]);
        }
    };

    const isPlayerPiece = (piece) => {
        if (!piece) return false;
        const isWhite = piece === piece.toUpperCase();
        return (currentTurn === 'w' && isWhite) || (currentTurn === 'b' && !isWhite);
    };

    const handleSquareClick = (square, piece) => {
        if (selectedSquare) {
            if (selectedSquare === square) {
                setSelectedSquare(null);
                setLegalMoves([]);
            } else if (piece && isPlayerPiece(piece)) {
                setSelectedSquare(square);
                fetchLegalMoves(square);
            } else {
                if (onMove) {
                    onMove(selectedSquare, square);
                }
                setSelectedSquare(null);
                setLegalMoves([]);
            }
        } else if (piece && isPlayerPiece(piece)) {
            setSelectedSquare(square);
            fetchLegalMoves(square);
        }
    };

    const handleMouseDown = (e, square, piece) => {
        if (!piece || !isPlayerPiece(piece)) return;
        e.preventDefault();
        setMouseDownPos({ x: e.clientX, y: e.clientY });
        setDraggedFrom(square);
    };

    const handleMouseMove = (e) => {
        if (draggedFrom && mouseDownPos) {
            const dx = e.clientX - mouseDownPos.x;
            const dy = e.clientY - mouseDownPos.y;
            const distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 5 && !isDragging) {
                setIsDragging(true);
                const piece = board[7 - Math.floor((draggedFrom.charCodeAt(1) - 49))][draggedFrom.charCodeAt(0) - 97];
                setDragPreview({
                    piece: piece,
                    x: e.clientX,
                    y: e.clientY
                });
                fetchLegalMoves(draggedFrom);
            } else if (isDragging && dragPreview) {
                setDragPreview({
                    ...dragPreview,
                    x: e.clientX,
                    y: e.clientY
                });
            }
        }
    };

    const handleMouseUp = (e, square) => {
        if (draggedFrom) {
            if (isDragging && square && draggedFrom !== square) {
                if (onMove) {
                    onMove(draggedFrom, square);
                }
                setSelectedSquare(null);
                setLegalMoves([]);
            } else if (!isDragging && square) {
                const rank = 7 - Math.floor((square.charCodeAt(1) - 49));
                const file = square.charCodeAt(0) - 97;
                const piece = board[rank] && board[rank][file];
                handleSquareClick(square, piece);
            }
        }
        setDraggedFrom(null);
        setDragPreview(null);
        setIsDragging(false);
        setMouseDownPos(null);
    };

    const isLegalDestination = (square) => {
        return legalMoves.includes(square);
    };

    // Determine which ranks and files to iterate through based on orientation
    const ranksToRender = flipped ? [0, 1, 2, 3, 4, 5, 6, 7] : [7, 6, 5, 4, 3, 2, 1, 0];
    const filesToRender = flipped ? [7, 6, 5, 4, 3, 2, 1, 0] : [0, 1, 2, 3, 4, 5, 6, 7];

    return (
        <div
            className={`chess-board-wrapper ${size === 'small' ? 'small': ''}`}
            onMouseMove={handleMouseMove}
            onMouseUp={() => handleMouseUp(null, null)}
        >
            <div className="chess-board">
                {ranksToRender.map((rankIndex) =>
                    filesToRender.map((fileIndex) => {
                        const rank = rankIndex;
                        const file = fileIndex;
                        const isLight = (rank + file) % 2 === 1; // FIXED: a1 is now dark
                        const squareName = `${String.fromCharCode(97 + file)}${rank + 1}`;
                        const piece = board[7 - rank][file];
                        const isSelected = selectedSquare === squareName;
                        const isLegalDest = isLegalDestination(squareName);
                        const isHighlighted = highlightedSquares.includes(squareName);
                        const isWrongMove = showWrongMove && wrongMoveSquare === squareName;
                        const isDraggingPiece = draggedFrom === squareName;

                        return (
                            <div
                                key={squareName}
                                className={`square ${isLight ? 'light' : 'dark'} 
                  ${isSelected ? 'selected' : ''} 
                  ${isHighlighted ? 'highlighted' : ''}
                  ${isWrongMove ? 'wrong-move' : ''}`}
                                onClick={() => handleSquareClick(squareName, piece)}
                                onMouseUp={(e) => handleMouseUp(e, squareName)}
                            >
                                {piece && !isDraggingPiece && (
                                    <img
                                        src={PIECE_IMAGES[piece]}
                                        alt={piece}
                                        className="piece"
                                        onMouseDown={(e) => handleMouseDown(e, squareName, piece)}
                                    />
                                )}
                                {isLegalDest && (
                                    <div className="legal-move-indicator"></div>
                                )}
                                {/* Adjust label positions based on board orientation */}
                                {(flipped ? file === 7 : file === 0) && (
                                    <span className="rank-label">{rank + 1}</span>
                                )}
                                {(flipped ? rank === 7 : rank === 0) && (
                                    <span className="file-label">{String.fromCharCode(97 + file)}</span>
                                )}
                                {isWrongMove && (
                                    <div className="wrong-move-x">✕</div>
                                )}
                            </div>
                        );
                    })
                )}
            </div>

            {dragPreview && (
                <img
                    src={PIECE_IMAGES[dragPreview.piece]}
                    alt="dragging"
                    className="drag-preview"
                    style={{
                        left: dragPreview.x - 40,
                        top: dragPreview.y - 40
                    }}
                />
            )}
        </div>
    );
}

function fenToBoard(fen) {
    const rows = fen.split(' ')[0].split('/');
    const board = [];

    for (let row of rows) {
        const boardRow = [];
        for (let char of row) {
            if (char >= '1' && char <= '8') {
                for (let i = 0; i < parseInt(char); i++) {
                    boardRow.push(null);
                }
            } else {
                boardRow.push(char);
            }
        }
        board.push(boardRow);
    }

    return board;
}

export default ChessBoard;