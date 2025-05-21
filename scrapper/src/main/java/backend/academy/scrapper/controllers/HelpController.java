package backend.academy.scrapper.controllers;

import backend.academy.StringConstants;
import backend.academy.scrapper.data.repository.chats.tgChatStorage.TgChatStorageRepository;
import backend.academy.scrapper.data.repository.links.LinkStorage.LinkStorageRepository;
import backend.academy.scrapper.domain.mapper.CustomLinkMapper;
import backend.academy.scrapper.dto.CustomLinkDto;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(StringConstants.API_V1)
public class HelpController {
    private final LinkStorageRepository linkStorageRepository;
    private final TgChatStorageRepository tgChatStorageRepository;

    @GetMapping("/all")
    public Set<CustomLinkDto> getAllLinks() {
        return linkStorageRepository.__findAll_forTest().stream()
                .map(CustomLinkMapper::mapDto)
                .collect(Collectors.toSet());
    }

    @DeleteMapping("/delete")
    public void deleteAllLinks() {
        linkStorageRepository.clear();
        tgChatStorageRepository.clear();
    }
}
