package com.ogmikee.chess.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogmikee.chess.opening.OpeningTree;
import com.ogmikee.chess.service.QuizQuestion;
import com.ogmikee.chess.service.TrainingService;
import com.ogmikee.chess.service.PlaythroughSession;
import com.ogmikee.chess.service.MoveResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/training")
@CrossOrigin(origins = "*")
public class TrainingController {

    private final TrainingService trainingService = new TrainingService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/quiz")
    public QuizQuestion getQuiz(@RequestBody OpeningTree tree) {
        return trainingService.getRandomQuiz(tree);
    }

    @PostMapping("/quiz/check")
    public Map<String, Boolean> checkQuizAnswer(@RequestBody Map<String, Object> request) {
        OpeningTree tree = objectMapper.convertValue(request.get("tree"), OpeningTree.class);
        @SuppressWarnings("unchecked")
        List<String> path = (List<String>) request.get("path");
        String move = (String) request.get("move");
        boolean isValid = trainingService.checkQuizAnswer(tree, path, move);
        return Map.of("correct", isValid);  // Key should be "correct", not the move
    }

    @PostMapping("/playthrough/start")
    public PlaythroughSession startPlaythrough(@RequestBody Map<String, Object> request) {
        OpeningTree tree = objectMapper.convertValue(request.get("tree"), OpeningTree.class);
        @SuppressWarnings("unchecked")
        List<String> startPath = (List<String>) request.get("startPath");
        return trainingService.startPlaythrough(tree, startPath);
    }

    @PostMapping("/playthrough/move")
    public MoveResult processMove(@RequestBody Map<String, Object> request) {
        PlaythroughSession session = objectMapper.convertValue(request.get("session"), PlaythroughSession.class);
        String move = (String) request.get("move");
        return trainingService.processUserMove(session, move);
    }
}