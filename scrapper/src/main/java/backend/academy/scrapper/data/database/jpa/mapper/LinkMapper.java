package backend.academy.scrapper.data.database.jpa.mapper;

import backend.academy.scrapper.data.database.jpa.entities.LinkEntity;
import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.util.LinkParser;

public final class LinkMapper {
    public static LinkEntity toEntity(final CustomLink link) {
        var a = LinkEntity.builder()
            .url(link.url())
            .lastTimeObserve(link.lastCheckedDate())
            .subscribers(link.subscribers())
            .service(link.service())
            .build();
        if (link.id() != null) {
            a.id(link.id());
        }
        return a;
    }

    public static CustomLink toCustomLink(LinkEntity entity) {
        if (entity == null) {
            return null;
        }

        var customLink = LinkParser.parseUrl(entity.url());
        customLink.id(entity.id());
        customLink.lastCheckedDate(entity.lastTimeObserve());
        customLink.subscribers(entity.subscribers());
        customLink.service(entity.service());
        return customLink;
    }
}
