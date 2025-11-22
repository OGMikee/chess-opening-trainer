import API_URL from '../config';
import React, { useState } from 'react';
import './OpeningEditor.css';
import ChessBoard from './ChessBoard';
import axios from 'axios';

function OpeningEditor({ currentOpening, setCurrentOpening }) {
    const [pgnInput, setPgnInput] = useState('');
    const [parsedMoves, setParsedMoves] = useState([]);
    const [isValid, setIsValid] = useState(null);
    const [previewFen, setPreviewFen] = useState('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1');
    const handleParsePgn = async () => {
        if (!pgnInput.trim()) {
            alert('Please enter PGN text');
            return;
        }
        try {
            const response = await axios.post(`${API_URL}/api/opening/parse-pgn`, {
                pgn: pgnInput
            });
            const moves = response.data.moves;
            setParsedMoves(moves);
            console.log('Parsed moves:', moves);
            const validationResponse = await axios.post(`${API_URL}/api/opening/validate-line`, {
                moves: moves
            });
            setIsValid(validationResponse.data.valid);
        } catch (error) {
            console.error('Error parsing PGN:', error);
            alert('Error parsing PGN. Make sure backend is running.');
        }
    };
    const handleAddLine = () => {
        if (!isValid) {
            alert('Cannot add invalid line');
            return;
        }
        if (parsedMoves.length === 0) {
            alert('No moves to add');
            return;
        }
        const updatedOpening = { ...currentOpening };
        addLineToTree(updatedOpening.tree, parsedMoves);
        setCurrentOpening(updatedOpening);
        setPgnInput('');
        setParsedMoves([]);
        setIsValid(null);

        alert('Line added successfully!');
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

    return (
        <div className="opening-editor">
            <div className="editor-panel">
                <h3>Add Line to Opening</h3>

                <div className="input-section">
                    <label>Paste PGN moves:</label>
                    <textarea
                        value={pgnInput}
                        onChange={(e) => setPgnInput(e.target.value)}
                        placeholder="1. e4 e5 2. Nf3 Nc6 3. Bb5"
                        rows={5}
                    />

                    <button onClick={handleParsePgn} className="btn btn-primary">
                        Parse PGN
                    </button>
                </div>

                {parsedMoves.length > 0 && (
                    <div className="parsed-section">
                        <h4>Parsed Moves:</h4>
                        <div className="moves-display">
                            {parsedMoves.map((move, i) => (
                                <span key={i} className="move-chip">
                  {Math.floor(i / 2) + 1}.{i % 2 === 0 ? '' : '..'} {move}
                </span>
                            ))}
                        </div>

                        <div className={`validation ${isValid ? 'valid' : 'invalid'}`}>
                            {isValid ? '✓ Valid moves' : '✗ Invalid moves'}
                        </div>

                        {isValid && (
                            <button onClick={handleAddLine} className="btn btn-success">
                                Add Line to Opening
                            </button>
                        )}
                    </div>
                )}

                {currentOpening && (
                    <div className="tree-info">
                        <h4>Current Tree Info:</h4>
                        <p>Total lines: {countLines(currentOpening.tree.root)}</p>
                        <p>Total nodes: {countNodes(currentOpening.tree.root)}</p>
                    </div>
                )}
            </div>

            <div className="board-panel">
                <ChessBoard fen={previewFen} />
            </div>
        </div>
    );
}

function countLines(node) {
    if (!node.children || node.children.length === 0) {
        return node.move ? 1 : 0;
    }
    return node.children.reduce((sum, child) => sum + countLines(child), 0);
}

function countNodes(node) {
    if (!node.children || node.children.length === 0) {
        return 1;
    }
    return 1 + node.children.reduce((sum, child) => sum + countNodes(child), 0);
}

export default OpeningEditor;