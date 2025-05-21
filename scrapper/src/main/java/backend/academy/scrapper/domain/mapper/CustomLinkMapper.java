package backend.academy.scrapper.domain.mapper;

import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.dto.CustomLinkDto;

public final class CustomLinkMapper {
    public static CustomLinkDto mapDto(final CustomLink link) {
        return new CustomLinkDto(link.id(), link.url(), link.subscribers(), link.service(), link.lastCheckedDate());
    }
}
