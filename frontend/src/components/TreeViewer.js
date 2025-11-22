import React, { useState } from 'react';
import './TreeViewer.css';

function TreeViewer({ opening, onToggleLine, onNodeClick }) {
    const [expandedNodes, setExpandedNodes] = useState(new Set(['root']));

    if (!opening) {
        return <p className="no-opening">No opening loaded</p>;
    }

    const toggleNode = (nodeId) => {
        const newExpanded = new Set(expandedNodes);
        if (newExpanded.has(nodeId)) {
            newExpanded.delete(nodeId);
        } else {
            newExpanded.add(nodeId);
        }
        setExpandedNodes(newExpanded);
    };

    const renderNode = (node, path = [], depth = 0) => {
        if (!node) return null;

        const nodeId = path.join('-') || 'root';
        const isExpanded = expandedNodes.has(nodeId);
        const hasChildren = node.children && node.children.length > 0;

        return (
            <div key={nodeId} className="tree-node" style={{ marginLeft: `${depth * 12}px` }}>
                <div
                    className={`node-content ${node.isPlayerMove ? 'player-move' : 'opponent-move'}`}
                    onClick={() => onNodeClick && onNodeClick(path)}
                >
                    {hasChildren && (
                        <button
                            className="expand-btn"
                            onClick={(e) => {
                                e.stopPropagation();
                                toggleNode(nodeId);
                            }}
                        >
                            {isExpanded ? '▼' : '▶'}
                        </button>
                    )}

                    <span className="move-text">
          {node.move || 'Start'}
        </span>

                    {node.move && (
                        <label className="toggle-switch" onClick={(e) => e.stopPropagation()}>
                            <input
                                type="checkbox"
                                checked={node.enabled}
                                onChange={() => onToggleLine && onToggleLine(path)}
                            />
                            <span className="slider"></span>
                        </label>
                    )}
                </div>

                {isExpanded && hasChildren && (
                    <div className="node-children">
                        {node.children.map((child, index) =>
                            renderNode(child, [...path, child.move], depth + 1)
                        )}
                    </div>
                )}
            </div>
        );
    };

    return (
        <div className="tree-viewer">
            {renderNode(opening.tree.root)}
        </div>
    );
}

export default TreeViewer;