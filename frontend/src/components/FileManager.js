import React, { useRef } from 'react';
import './FileManager.css';

function FileManager({ currentOpening, setCurrentOpening }) {
    const fileInputRef = useRef(null);

    // Handle file upload
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

    // Create new opening
    const handleNewOpening = () => {
        const name = prompt('Enter opening name:');
        if (!name) return;
        const color = prompt('Enter your color (WHITE or BLACK):');
        if (!color || (color !== 'WHITE' && color !== 'BLACK')) {
            alert('Invalid color. Must be WHITE or BLACK');
            return;
        }

        const newOpening = {
            version: '1.0',
            name: name,
            tree: {
                playerColor: color,
                root: {
                    move: null,
                    enabled: true,
                    children: [],
                    playerMove: true
                }
            }
        };
        setCurrentOpening(newOpening);
        console.log('Created new opening:', newOpening);
    };

    return (
        <div className="file-manager">
            <button onClick={handleNewOpening} className="btn btn-primary">
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
        </div>
    );
}
export default FileManager;