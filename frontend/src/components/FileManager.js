import React, { useRef, useState } from 'react';
import './FileManager.css';

function FileManager({ currentOpening, setCurrentOpening }) {
    const fileInputRef = useRef(null);
    const [showNewOpeningModal, setShowNewOpeningModal] = useState(false);
    const [newOpeningName, setNewOpeningName] = useState('');
    const [newOpeningColor, setNewOpeningColor] = useState('WHITE');

    const handleFileUpload = (event) => {
        const file = event.target.files[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = (e) => {
            try {
                const openingFile = JSON.parse(e.target.result);
                setCurrentOpening(openingFile);
                console.log('Loaded opening:', openingFile);
            } catch (error) {
                alert('Invalid opening file format');
                console.error('Parse error:', error);
            }
        };
        reader.readAsText(file);
    };

    const handleFileDownload = () => {
        if (!currentOpening) {
            alert('No opening to download');
            return;
        }
        const json = JSON.stringify(currentOpening, null, 2);
        const blob = new Blob([json], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${currentOpening.name || 'opening'}.opening`;
        link.click();
        URL.revokeObjectURL(url);
    };

    const handleNewOpeningClick = () => {
        setShowNewOpeningModal(true);
        setNewOpeningName('');
        setNewOpeningColor('WHITE');
    };

    const handleCreateOpening = () => {
        if (!newOpeningName.trim()) {
            alert('Please enter an opening name');
            return;
        }

        const newOpening = {
            version: '1.0',
            name: newOpeningName.trim(),
            tree: {
                playerColor: newOpeningColor,
                root: {
                    move: null,
                    enabled: true,
                    children: [],
                    playerMove: true
                }
            }
        };
        setCurrentOpening(newOpening);
        setShowNewOpeningModal(false);
        console.log('Created new opening:', newOpening);
    };

    return (
        <div className="file-manager">
            <button onClick={handleNewOpeningClick} className="btn btn-primary">
                New Opening
            </button>
            <button
                onClick={() => fileInputRef.current.click()}
                className="btn btn-secondary"
            >
                Upload Opening
            </button>
            <button
                onClick={handleFileDownload}
                className="btn btn-secondary"
                disabled={!currentOpening}
            >
                Download Opening
            </button>
            <input
                ref={fileInputRef}
                type="file"
                accept=".opening,.json"
                onChange={handleFileUpload}
                style={{ display: 'none' }}
            />


            {showNewOpeningModal && (
                <div className="modal-overlay" onClick={() => setShowNewOpeningModal(false)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <h3>Create New Opening</h3>

                        <div className="form-group">
                            <label>Opening Name:</label>
                            <input
                                type="text"
                                value={newOpeningName}
                                onChange={(e) => setNewOpeningName(e.target.value)}
                                placeholder="e.g., Sicilian Defense"
                                autoFocus
                            />
                        </div>

                        <div className="form-group">
                            <label>Your Color:</label>
                            <select
                                value={newOpeningColor}
                                onChange={(e) => setNewOpeningColor(e.target.value)}
                            >
                                <option value="WHITE">White</option>
                                <option value="BLACK">Black</option>
                            </select>
                        </div>

                        <div className="modal-actions">
                            <button onClick={handleCreateOpening} className="btn btn-primary">
                                Create
                            </button>
                            <button onClick={() => setShowNewOpeningModal(false)} className="btn btn-secondary">
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default FileManager;