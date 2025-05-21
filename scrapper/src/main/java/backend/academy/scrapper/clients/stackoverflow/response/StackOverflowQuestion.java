package backend.academy.scrapper.clients.stackoverflow.response;

import java.util.List;

public record StackOverflowQuestion(
        List<String> tags,
        List<StackOverflowAnswer> answers,
        List<StackOverflowComment> comments,
        StackOverflowOwner owner,
        String title,
        String body) {}
