package com.example.placement.controller;

import com.example.placement.entity.Feedback;
import com.example.placement.service.FeedbackService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedbackController {

    private final FeedbackService service;

    public FeedbackController(FeedbackService service) {
        this.service = service;
    }

    @PostMapping("/feedback")
    public Feedback submitFeedback(@RequestBody Feedback feedback) {
        return service.saveFeedback(feedback);
    }

    @GetMapping("/admin/feedback-reports")
    public List<Feedback> getAllFeedback() {
        return service.getAllFeedback();
    }
}
